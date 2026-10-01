package com.tajlpawa.ageofexpansion.gametest;
import com.tajlpawa.ageofexpansion.Config;
import com.tajlpawa.ageofexpansion.display.*;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;

@GameTestHolder("ageofexpansion")
public final class ModuleDisplayTests {
    @GameTestGenerator public static Collection<TestFunction> tests(){
        var tests=new ArrayList<TestFunction>();
        for(Direction facing:Direction.Plane.HORIZONTAL)add(tests,"module_rect_"+facing.getName(),h->rectangle(h,facing));
        add(tests,"module_conflict_split_merge",ModuleDisplayTests::conflicts);
        add(tests,"module_max_capacity_transfer",ModuleDisplayTests::maximum);
        add(tests,"module_irregular_partition",ModuleDisplayTests::irregular);
        add(tests,"new_display_recipes_and_legacy",ModuleDisplayTests::recipes);
        return tests;
    }
    private static void add(List<TestFunction> tests,String name,java.util.function.Consumer<GameTestHelper> run){
        tests.add(new TestFunction("ageofexpansion:module", "ageofexpansion:"+name,"ageofexpansion:module_empty",200,0,true,run));
    }
    private static DisplayDocument doc(String text){return new DisplayDocument(text.codePoints().mapToObj(cp->new DisplayDocument.Glyph(cp,new DisplayDocument.Ink(0x55ffaa,3))).toList(),150,2,0);}
    private static DisplayBlockEntity place(GameTestHelper h,BlockPos pos,Direction facing){
        var level=h.getLevel();level.setBlockAndUpdate(pos.relative(facing.getOpposite()),Blocks.STONE.defaultBlockState());
        level.setBlockAndUpdate(pos,DisplayContent.MODULE.get().defaultBlockState().setValue(DisplayBlock.FACING,facing));
        var be=(DisplayBlockEntity)level.getBlockEntity(pos);ModuleScreens.get(level).register(level,be);return be;
    }
    private static void rectangle(GameTestHelper h,Direction facing){
        BlockPos origin=h.absolutePos(new BlockPos(8,10,8));var store=ModuleScreens.get(h.getLevel());
        for(int y=0;y<2;y++)for(int x=0;x<3;x++)place(h,ModuleScreens.position(origin,facing,x,y),facing);
        store.tick(h.getLevel());var group=store.at(origin);
        h.assertTrue(group.width==3&&group.height==2&&group.members.size()==6,"3x2 rectangle "+facing);
        h.assertTrue(group.capacity()==6*Config.perBlock(),"Per-block capacity");
        DisplayDocument text=doc("共享文字");store.update(h.getLevel(),group,text);
        for(BlockPos p:group.members){
            var be=(DisplayBlockEntity)h.getLevel().getBlockEntity(p);
            h.assertTrue(be.document().equals(text)&&be.getBlockState().getLightEmission(h.getLevel(),p)==4,"Shared text and light");
        }
        var restored=ModuleScreens.load(store.save(new CompoundTag(),h.getLevel().registryAccess()),h.getLevel().registryAccess());
        h.assertTrue(restored.at(origin).document.equals(text)&&restored.at(origin).members.size()==6,"Persistent topology and text without loaded BEs");
        h.assertTrue(h.getLevel().getBlockState(origin).getValue(DisplayBlock.CONNECTIONS)==9,"Top-left connects right and down");
        h.succeed();
    }
    private static void conflicts(GameTestHelper h){
        BlockPos a=h.absolutePos(new BlockPos(8,10,8));Direction f=Direction.NORTH;BlockPos b=ModuleScreens.position(a,f,1,0),c=ModuleScreens.position(a,f,2,0);
        var store=ModuleScreens.get(h.getLevel());place(h,a,f);place(h,c,f);store.tick(h.getLevel());
        store.update(h.getLevel(),store.at(a),doc("A"));store.update(h.getLevel(),store.at(c),doc("C"));
        place(h,b,f);store.tick(h.getLevel());
        h.assertTrue(!store.at(a).id.equals(store.at(c).id),"Distinct content prevents merging");
        store.update(h.getLevel(),store.at(c),DisplayDocument.EMPTY);store.tick(h.getLevel());
        h.assertTrue(store.at(a).id.equals(store.at(c).id)&&store.at(a).document.equals(doc("A")),"Clearing merges while preserving text");
        h.getLevel().setBlockAndUpdate(b,Blocks.AIR.defaultBlockState());
        h.assertTrue(!store.at(a).document.hasText()&&!store.at(c).document.hasText(),"Breaking non-anchor immediately clears the whole former screen");
        store.tick(h.getLevel());
        h.assertTrue(!store.at(a).document.hasText()&&!store.at(c).document.hasText(),"All split components stay blank");
        place(h,b,f);store.tick(h.getLevel());
        h.assertTrue(store.at(a).members.size()==3&&!store.at(a).document.hasText(),"Rejoining remains blank");
        h.succeed();
    }
    private static void irregular(GameTestHelper h){
        BlockPos a=h.absolutePos(new BlockPos(8,10,8));Direction f=Direction.NORTH;var store=ModuleScreens.get(h.getLevel());
        place(h,a,f);place(h,ModuleScreens.position(a,f,1,0),f);place(h,a.below(),f);store.tick(h.getLevel());
        h.assertTrue(store.at(a).width==2&&store.at(a).height==1,"L shape tie selects wider rectangle");
        h.assertTrue(store.at(a.below()).members.size()==1,"Remaining tile is separate rectangle");
        h.succeed();
    }
    private static void maximum(GameTestHelper h){
        int oldX=Config.sizeX(),oldY=Config.sizeY(),oldPer=Config.perBlock();
        try{
            Config.SIZE_X.set(10);Config.SIZE_Y.set(10);Config.PER_BLOCK.set(2048);
            BlockPos a=h.absolutePos(new BlockPos(12,12,8));var store=ModuleScreens.get(h.getLevel());
            for(int y=0;y<10;y++)for(int x=0;x<10;x++)place(h,ModuleScreens.position(a,Direction.NORTH,x,y),Direction.NORTH);
            long start=System.nanoTime();store.tick(h.getLevel());long grouping=System.nanoTime()-start;
            var group=store.at(a);h.assertTrue(group.members.size()==100&&group.capacity()==204800,"10x10 maximum");
            var glyphs=new ArrayList<DisplayDocument.Glyph>(204800);
            for(int i=0;i<204800;i++)glyphs.add(new DisplayDocument.Glyph('中'+i%100,new DisplayDocument.Ink((i*12347)&0xffffff,i%8)));
            var doc=new DisplayDocument(glyphs,400,1,2);store.update(h.getLevel(),group,doc);
            long encode=System.nanoTime();byte[] bytes=doc.bytes();var decoded=DisplayDocument.bytes(bytes);long roundtrip=System.nanoTime()-encode;
            h.assertTrue(decoded.equals(doc)&&DisplayDocument.load(doc.save()).equals(doc),"Maximum high-style roundtrip");
            var parts=DisplayNetwork.frames(new DisplayNetwork.Packet(1,UUID.randomUUID(),doc));
            h.assertTrue(parts.size()>1&&parts.stream().allMatch(p->p.bytes().length<=24000),"Large payload split below 32KiB");
            var joined=new java.io.ByteArrayOutputStream();parts.forEach(p->joined.writeBytes(p.bytes()));
            h.assertTrue(DisplayDocument.bytes(joined.toByteArray()).equals(doc),"Split transmission reconstructs exact text");
            Config.PER_BLOCK.set(1);store.tick(h.getLevel());
            h.assertTrue(store.at(a).document.equals(doc)&&store.at(a).capacity()==100,"Capacity reduction preserves long document");
            com.tajlpawa.ageofexpansion.AgeofExpansion.LOGGER.info("MODULE_MAX_PERF grouping_ms={} packed_bytes={} encode_decode_ms={} frames={}",grouping/1e6,bytes.length,roundtrip/1e6,parts.size());
            h.succeed();
        }finally{Config.SIZE_X.set(oldX);Config.SIZE_Y.set(oldY);Config.PER_BLOCK.set(oldPer);}
    }
    private static void recipes(GameTestHelper h){
        for(String id:List.of("square_electronic_display","modular_electronic_display"))
            h.assertTrue(h.getLevel().getRecipeManager().byKey(net.minecraft.resources.ResourceLocation.parse("ageofexpansion:"+id)).isPresent(),"Recipe loaded "+id);
        CompoundTag legacy=new CompoundTag();legacy.putInt("size",100);legacy.putInt("alignment",2);
        var runs=new net.minecraft.nbt.ListTag();CompoundTag run=new CompoundTag();run.putString("text","旧屏");run.putInt("color",0x123456);run.putInt("flags",1);runs.add(run);legacy.put("runs",runs);
        var restored=DisplayDocument.load(legacy);
        h.assertTrue(restored.text(0,2).equals("旧屏")&&restored.vertical()==1&&restored.alignment()==2,"Old text migration defaults vertical center");
        h.assertTrue(net.minecraft.core.registries.BuiltInRegistries.ITEM.get(net.minecraft.resources.ResourceLocation.parse("ageofexpansion:example_item"))==net.minecraft.world.item.Items.AIR,"Example item removed");
        h.succeed();
    }
}

