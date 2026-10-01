package com.tajlpawa.ageofexpansion.gametest;

import com.tajlpawa.ageofexpansion.display.*;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.nbt.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;

@GameTestHolder("ageofexpansion")
public final class HolographicDisplayTests {
    @GameTestGenerator public static Collection<TestFunction> tests(){
        var tests=new ArrayList<TestFunction>();
        add(tests,"holographic_light_and_support",HolographicDisplayTests::light);
        add(tests,"holographic_module_isolation_and_reload",HolographicDisplayTests::modules);
        add(tests,"holographic_break_every_member",HolographicDisplayTests::breaking);
        add(tests,"holographic_recipes_and_copier",HolographicDisplayTests::recipes);
        return tests;
    }
    private static void add(List<TestFunction> list,String name,java.util.function.Consumer<GameTestHelper> run){list.add(new TestFunction("ageofexpansion:holographic","ageofexpansion:"+name,"ageofexpansion:module_empty",200,0,true,run));}
    private static DisplayDocument text(){return new DisplayDocument("全息文字\nHologram".codePoints().mapToObj(cp->new DisplayDocument.Glyph(cp,new DisplayDocument.Ink(0x99ddff,3))).toList(),400,1,1);}
    private static DisplayBlock block(int kind){return (kind==0?DisplayContent.HOLOGRAPHIC:kind==1?DisplayContent.HOLOGRAPHIC_SQUARE:DisplayContent.HOLOGRAPHIC_MODULE).get();}
    private static DisplayBlockEntity place(GameTestHelper h,BlockPos p,DisplayBlock b){
        h.getLevel().setBlockAndUpdate(p.south(),Blocks.STONE.defaultBlockState());h.getLevel().setBlockAndUpdate(p,b.defaultBlockState());
        var be=(DisplayBlockEntity)h.getLevel().getBlockEntity(p);if(b.kind==2){var store=ModuleScreens.get(h.getLevel());store.register(h.getLevel(),be);store.tick(h.getLevel());}return be;
    }
    private static void light(GameTestHelper h){
        var p=h.absolutePos(new BlockPos(7,8,8));
        for(int kind=0;kind<3;kind++){
            var be=place(h,p,block(kind));h.assertTrue(be.holographic()&&be.kind()==kind,"Holographic family retains layout kind");
            be.update(text());h.assertTrue(be.getBlockState().getValue(DisplayBlock.LIT)&&be.getBlockState().getLightEmission(h.getLevel(),p)==7,"Nonempty hologram emits seven");
            var restored=new DisplayBlockEntity(p,be.getBlockState());restored.loadWithComponents(be.saveWithFullMetadata(h.getLevel().registryAccess()),h.getLevel().registryAccess());
            if(kind!=2)h.assertTrue(restored.document().equals(text()),"Ordinary hologram text survives NBT");
            be.update(new DisplayDocument(" \n　".codePoints().mapToObj(cp->new DisplayDocument.Glyph(cp,DisplayDocument.Ink.WHITE)).toList(),100,1));
            h.assertTrue(!be.getBlockState().getValue(DisplayBlock.LIT)&&be.getBlockState().getLightEmission(h.getLevel(),p)==0,"Whitespace restores visible unlit panel");
            var drops=net.minecraft.world.level.block.Block.getDrops(be.getBlockState(),h.getLevel(),p,be);
            h.assertTrue(drops.size()==1&&drops.getFirst().is(block(kind).asItem())&&!drops.getFirst().has(net.minecraft.core.component.DataComponents.BLOCK_ENTITY_DATA),"Correct blank item dropped");
            h.getLevel().setBlockAndUpdate(p.south(),Blocks.AIR.defaultBlockState());h.assertTrue(h.getLevel().getBlockState(p).isAir(),"Wall support still required");
        }h.succeed();
    }
    private static void modules(GameTestHelper h){
        var p=h.absolutePos(new BlockPos(9,10,8));var store=ModuleScreens.get(h.getLevel());
        place(h,p,block(2));place(h,p.west(),block(2));place(h,p.below(),block(2));place(h,p.west().below(),block(2));
        var normal=place(h,p.east(),DisplayContent.MODULE.get());store.tick(h.getLevel());
        var group=store.at(p);h.assertTrue(group.holographic&&group.members.size()==4&&group.width==2&&group.height==2,"Holographic rectangle joins");
        h.assertTrue(store.at(normal.getBlockPos())!=group&&!store.at(normal.getBlockPos()).holographic,"Electronic and holographic families remain separate");
        store.update(h.getLevel(),group,text());for(var member:group.members)h.assertTrue(h.getLevel().getBlockState(member).getValue(DisplayBlock.LIT)&&h.getLevel().getBlockState(member).getLightEmission(h.getLevel(),member)==7,"Entire group lit including tiles without glyphs");
        var tag=store.save(new CompoundTag(),h.getLevel().registryAccess());var loaded=ModuleScreens.load(tag,h.getLevel().registryAccess());
        h.assertTrue(loaded.at(p).holographic&&loaded.at(p).document.equals(text())&&!loaded.at(p.east()).holographic,"Family and content survive unloaded topology storage");
        for(var entry:tag.getList("groups",Tag.TAG_COMPOUND))if(entry instanceof CompoundTag g&&!g.getBoolean("holographic"))g.remove("holographic");
        h.assertTrue(!ModuleScreens.load(tag,h.getLevel().registryAccess()).at(p.east()).holographic,"Legacy groups default to electronic family");
        var meta=new DisplayNetwork.Meta(2,2,2,4096,p,group.id,group.revision,25,400,true);
        var frame=DisplayNetwork.frames(new DisplayNetwork.Packet(0,UUID.randomUUID(),text(),meta)).getFirst();var buf=new RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(),h.getLevel().registryAccess());
        try{DisplayNetwork.Frame.CODEC.encode(buf,frame);h.assertTrue(DisplayNetwork.Frame.CODEC.decode(buf).meta().equals(meta),"Full editor metadata including holographic family round trips");}finally{buf.release();}
        h.succeed();
    }
    private static void breaking(GameTestHelper h){
        var p=h.absolutePos(new BlockPos(9,10,8));var store=ModuleScreens.get(h.getLevel());
        for(int victim=0;victim<4;victim++){
            var members=List.of(p,p.west(),p.below(),p.west().below());for(var member:members)place(h,member,block(2));store.tick(h.getLevel());store.update(h.getLevel(),store.at(p),text());
            h.getLevel().setBlockAndUpdate(members.get(victim),Blocks.AIR.defaultBlockState());
            for(var member:members)if(store.at(member)!=null)h.assertTrue(!store.at(member).document.hasText()&&!h.getLevel().getBlockState(member).getValue(DisplayBlock.LIT),"Any removal immediately restores all remaining panels");
            for(var member:members)h.getLevel().setBlockAndUpdate(member,Blocks.AIR.defaultBlockState());store.tick(h.getLevel());
        }h.succeed();
    }
    private static void recipes(GameTestHelper h){
        var patterns=List.of(List.of(" C ","BAB"," C "),List.of("DCD","BAB","DCD"),List.of(" A ","ABA"," A "));
        for(int kind=0;kind<3;kind++){
            var keys=kind==2?Map.of('A',Items.IRON_INGOT,'B',block(1).asItem()):Map.of('A',Items.GLASS_PANE,'B',Items.REDSTONE,'C',Items.QUARTZ,'D',Items.IRON_INGOT);
            List<ItemStack> stacks=new ArrayList<>();for(var row:patterns.get(kind))for(char c:row.toCharArray())stacks.add(c==' '?ItemStack.EMPTY:new ItemStack(keys.get(c)));
            var recipe=(CraftingRecipe)h.getLevel().getRecipeManager().byKey(net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block(kind))).orElseThrow().value();var input=CraftingInput.of(3,3,stacks);
            h.assertTrue(recipe.matches(input,h.getLevel())&&recipe.assemble(input,h.getLevel().registryAccess()).is(block(kind).asItem()),"Exact holographic recipe: "+kind);
        }
        var p=h.absolutePos(new BlockPos(7,8,8));var source=place(h,p,DisplayContent.SQUARE.get());source.update(text());
        var player=net.neoforged.neoforge.common.util.FakePlayerFactory.get(h.getLevel(),new com.mojang.authlib.GameProfile(UUID.randomUUID(),"HoloCopier"));player.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);player.setPos(p.getX()+.5,p.getY(),p.getZ()-1);player.setItemInHand(InteractionHand.MAIN_HAND,DisplayContent.COPIER.get().getDefaultInstance());
        h.assertTrue(DataCopierItem.copy(player,player.getMainHandItem(),source),"Copy electronic source");
        for(int kind=0;kind<3;kind++){
            var target=place(h,p.west(),block(kind));h.assertTrue(DataCopierItem.paste(player,p.west())&&target.document().equals(text()),"Copier preserves all format into holographic kind "+kind);
            h.assertTrue(DataCopierItem.copy(player,player.getMainHandItem(),target),"Copy back from holographic source");h.getLevel().setBlockAndUpdate(p.west(),Blocks.AIR.defaultBlockState());
        }
        source.update(DisplayDocument.EMPTY);h.assertTrue(DataCopierItem.paste(player,p)&&source.document().equals(text()),"Paste back into electronic screen");h.succeed();
    }
}
