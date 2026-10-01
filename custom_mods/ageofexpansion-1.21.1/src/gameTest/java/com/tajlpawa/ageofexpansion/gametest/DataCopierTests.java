package com.tajlpawa.ageofexpansion.gametest;

import com.tajlpawa.ageofexpansion.Config;
import com.tajlpawa.ageofexpansion.display.*;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.gametest.GameTestHolder;

@GameTestHolder("ageofexpansion")
public final class DataCopierTests {
    @GameTestGenerator public static Collection<TestFunction> tests(){
        List<TestFunction> tests=new ArrayList<>();
        add(tests,"copier_roundtrip_and_independent_snapshot",DataCopierTests::roundtrip);
        add(tests,"copier_limits_occupied_and_clear",DataCopierTests::limits);
        add(tests,"copier_interaction_recipe_tooltip",DataCopierTests::interaction);
        add(tests,"break_each_module_clears_whole_screen",DataCopierTests::breakEvery);
        return tests;
    }
    private static void add(List<TestFunction> list,String name,java.util.function.Consumer<GameTestHelper> run){list.add(new TestFunction("ageofexpansion:copier","ageofexpansion:"+name,"ageofexpansion:module_empty",200,0,true,run));}
    private static DisplayDocument text(){return new DisplayDocument("复制测试\nData copier 😀".codePoints().mapToObj(cp->new DisplayDocument.Glyph(cp,new DisplayDocument.Ink(0x12abcd,7))).toList(),175,2,0);}
    private static ServerPlayer player(GameTestHelper h,BlockPos pos){
        var player=net.neoforged.neoforge.common.util.FakePlayerFactory.get(h.getLevel(),new com.mojang.authlib.GameProfile(UUID.randomUUID(),"CopierTest"));
        player.setGameMode(GameType.SURVIVAL);player.setPos(pos.getX()+.5,pos.getY(),pos.getZ()-1);player.setItemInHand(InteractionHand.MAIN_HAND,DisplayContent.COPIER.get().getDefaultInstance());return player;
    }
    private static DisplayBlockEntity place(GameTestHelper h,BlockPos pos,int kind){
        var block=kind==0?DisplayContent.BLOCK:kind==1?DisplayContent.SQUARE:DisplayContent.MODULE;
        h.getLevel().setBlockAndUpdate(pos.south(),Blocks.STONE.defaultBlockState());h.getLevel().setBlockAndUpdate(pos,block.get().defaultBlockState());
        var be=(DisplayBlockEntity)h.getLevel().getBlockEntity(pos);
        if(kind==2){ModuleScreens.get(h.getLevel()).register(h.getLevel(),be);ModuleScreens.get(h.getLevel()).tick(h.getLevel());}return be;
    }
    private static void roundtrip(GameTestHelper h){
        BlockPos sourcePos=h.absolutePos(new BlockPos(7,8,8)),targetPos=sourcePos.west();var source=place(h,sourcePos,2);source.update(text());
        var player=player(h,sourcePos);ItemStack stack=player.getMainHandItem();
        h.assertTrue(DataCopierItem.copy(player,stack,source),"Copy succeeds");
        CompoundTag tag=stack.get(DataComponents.CUSTOM_DATA).copyTag();UUID id=tag.getUUID("display_snapshot");
        h.assertTrue(tag.toString().length()<200,"Inventory stores small snapshot reference");
        h.getLevel().setBlockAndUpdate(sourcePos,Blocks.AIR.defaultBlockState());
        for(int kind=0;kind<3;kind++){
            var target=place(h,targetPos,kind);
            h.assertTrue(DataCopierItem.paste(player,targetPos)&&target.document().equals(text()),"Cross-type paste retains all formatting: "+kind);
            h.getLevel().setBlockAndUpdate(targetPos,Blocks.AIR.defaultBlockState());
        }
        var restored=CopierData.load(CopierData.get(h.getLevel()).save(new CompoundTag(),h.getLevel().registryAccess()),h.getLevel().registryAccess());
        h.assertTrue(restored.document(id).equals(text()),"Copied snapshot survives source destruction and save/load");
        h.succeed();
    }
    private static void limits(GameTestHelper h){
        BlockPos a=h.absolutePos(new BlockPos(7,8,8));var source=place(h,a,1);source.update(text());var target=place(h,a.west(),0);var player=player(h,a);
        ItemStack stack=player.getMainHandItem();DataCopierItem.copy(player,stack,source);var before=stack.get(DataComponents.CUSTOM_DATA);
        h.assertTrue(!DataCopierItem.copy(player,stack,target)&&stack.get(DataComponents.CUSTOM_DATA).equals(before),"Empty source does not erase copier");
        int limit=Config.maxChar();try{Config.MAX_CHAR.set(1);h.assertTrue(!DataCopierItem.paste(player,a.west())&&!target.document().hasText(),"Too-long data rejected atomically");}finally{Config.MAX_CHAR.set(limit);}
        int oldMax=Config.MAX_FONT_SIZE.get();try{
            Config.MAX_FONT_SIZE.set(100);h.assertTrue(DataCopierItem.paste(player,a.west())&&target.document().equals(text().metadata(100,2,0)),"Copier clamps size to current range and retains text and formatting");
            target.update(DisplayDocument.EMPTY);
        }finally{Config.MAX_FONT_SIZE.set(oldMax);}
        target.update(text());h.assertTrue(!DataCopierItem.paste(player,a.west())&&target.document().equals(text()),"Occupied display never overwritten");
        ItemStack twin=stack.copy();DataCopierItem.clear(player,stack);
        h.assertTrue(!stack.has(DataComponents.CUSTOM_DATA)&&twin.has(DataComponents.CUSTOM_DATA),"Clear affects only this copier");
        target.update(DisplayDocument.EMPTY);h.assertTrue(!DataCopierItem.paste(player,a.west()),"Empty copier cannot paste");
        player.setItemInHand(InteractionHand.MAIN_HAND,twin);player.setPos(a.getX()+30,a.getY(),a.getZ());
        h.assertTrue(!DataCopierItem.paste(player,a.west()),"Out-of-reach paste rejected");h.succeed();
    }
    private static void interaction(GameTestHelper h){
        BlockPos a=h.absolutePos(new BlockPos(7,8,8));var be=place(h,a,0);be.update(text());var player=player(h,a);var stack=player.getMainHandItem();var item=DisplayContent.COPIER.get();
        var context=new UseOnContext(h.getLevel(),player,InteractionHand.MAIN_HAND,stack,new BlockHitResult(a.getCenter(),Direction.NORTH,a,false));
        h.assertTrue(item.onItemUseFirst(stack,context).consumesAction()&&stack.has(DataComponents.CUSTOM_DATA),"Right-click consumed before display GUI");
        for(GameType mode:List.of(GameType.SURVIVAL,GameType.CREATIVE)){
            player.setGameMode(mode);var event=new PlayerInteractEvent.LeftClickBlock(player,a,Direction.NORTH,PlayerInteractEvent.LeftClickBlock.Action.START);DataCopierItem.leftClick(event);
            h.assertTrue(event.isCanceled()&&!item.canAttackBlock(be.getBlockState(),h.getLevel(),a,player),"Left-click never destroys display in "+mode);
        }
        player.setShiftKeyDown(true);item.use(h.getLevel(),player,InteractionHand.MAIN_HAND);h.assertTrue(!stack.has(DataComponents.CUSTOM_DATA),"Sneak-right-click air clears data");
        var recipe=(CraftingRecipe)h.getLevel().getRecipeManager().byKey(net.minecraft.resources.ResourceLocation.parse("ageofexpansion:data_copier")).orElseThrow().value();
        var input=CraftingInput.of(3,3,List.of(ItemStack.EMPTY,new ItemStack(Items.LIGHTNING_ROD),ItemStack.EMPTY,new ItemStack(Items.IRON_INGOT),new ItemStack(Items.REDSTONE),new ItemStack(Items.IRON_INGOT),new ItemStack(Items.IRON_INGOT),new ItemStack(Items.IRON_INGOT),new ItemStack(Items.IRON_INGOT)));
        h.assertTrue(recipe.matches(input,h.getLevel())&&recipe.assemble(input,h.getLevel().registryAccess()).is(item),"Exact copier recipe");
        List<net.minecraft.network.chat.Component> basic=new ArrayList<>(),expanded=new ArrayList<>();var old=DataCopierItem.shiftDown;
        try{DataCopierItem.shiftDown=()->false;item.appendHoverText(stack,Item.TooltipContext.EMPTY,basic,TooltipFlag.NORMAL);DataCopierItem.shiftDown=()->true;item.appendHoverText(stack,Item.TooltipContext.EMPTY,expanded,TooltipFlag.NORMAL);}finally{DataCopierItem.shiftDown=old;}
        h.assertTrue(basic.size()==2&&expanded.size()>basic.size(),"Brief tooltip expands with Shift");h.succeed();
    }
    private static void breakEvery(GameTestHelper h){
        BlockPos origin=h.absolutePos(new BlockPos(9,10,8));var store=ModuleScreens.get(h.getLevel());
        for(int victim=0;victim<6;victim++){
            List<BlockPos> positions=new ArrayList<>();for(int y=0;y<2;y++)for(int x=0;x<3;x++){BlockPos p=origin.west(x).below(y);positions.add(p);place(h,p,2);}
            store.tick(h.getLevel());store.update(h.getLevel(),store.at(origin),text());
            h.getLevel().setBlockAndUpdate(positions.get(victim),Blocks.AIR.defaultBlockState());
            for(BlockPos p:positions)if(store.at(p)!=null)h.assertTrue(!store.at(p).document.hasText()&&!h.getLevel().getBlockState(p).getValue(DisplayBlock.LIT),"Every surviving tile clears immediately for victim "+victim);
            store.tick(h.getLevel());for(BlockPos p:positions)h.getLevel().setBlockAndUpdate(p,Blocks.AIR.defaultBlockState());store.tick(h.getLevel());
        }
        h.succeed();
    }
}
