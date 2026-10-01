package com.tajlpawa.ageofexpansion.display;

import java.util.*;
import java.util.function.BooleanSupplier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public final class DataCopierItem extends Item {
    public static BooleanSupplier shiftDown=()->false;
    public DataCopierItem(Properties properties){super(properties);}
    private static Component message(String key,Object... args){return Component.translatable("item.ageofexpansion.data_copier."+key,args);}
    private static CompoundTag data(ItemStack stack){return stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();}
    private static boolean allowed(ServerPlayer player,DisplayBlockEntity be){
        return player.isAlive()&&!player.isSpectator()&&player.mayBuild()&&be.getLevel()==player.level()
            &&player.canInteractWithBlock(be.getBlockPos(),0)&&player.level().mayInteract(player,be.getBlockPos());
    }
    public static boolean copy(ServerPlayer player,ItemStack stack,DisplayBlockEntity be){
        if(be.imageScreen()||!(stack.getItem() instanceof DataCopierItem)||!allowed(player,be)||be.imageScreen())return false;
        if(be.kind()==2)ModuleScreens.get(player.serverLevel()).tick(player.serverLevel());
        DisplayDocument document=be.document();
        if(!document.hasText()){player.displayClientMessage(message("empty_source"),true);return false;}
        UUID id=CopierData.get(player.serverLevel()).store(document);
        CompoundTag data=data(stack);data.putUUID("display_snapshot",id);data.putInt("display_characters",document.glyphs().size());
        stack.set(DataComponents.CUSTOM_DATA,CustomData.of(data));
        player.displayClientMessage(message("copied",document.glyphs().size()),true);return true;
    }
    public static boolean paste(ServerPlayer player,BlockPos pos){
        ItemStack stack=player.getMainHandItem();
        if(!(stack.getItem() instanceof DataCopierItem)||!player.level().hasChunkAt(pos)
            ||!(player.level().getBlockEntity(pos) instanceof DisplayBlockEntity be)||!allowed(player,be)||be.imageScreen())return false;
        if(be.kind()==2)ModuleScreens.get(player.serverLevel()).tick(player.serverLevel());
        if(be.document().hasText()){player.displayClientMessage(message("occupied"),true);return false;}
        CompoundTag data=data(stack);
        DisplayDocument document=data.hasUUID("display_snapshot")?CopierData.get(player.serverLevel()).document(data.getUUID("display_snapshot")):null;
        if(document==null||!document.hasText()){player.displayClientMessage(message("empty_copier"),true);return false;}
        if(document.glyphs().size()>be.capacity()){player.displayClientMessage(message("too_long",document.glyphs().size(),be.capacity()),true);return false;}
        int size=com.tajlpawa.ageofexpansion.Config.clampFontSize(document.size());
        be.update(document.metadata(size,document.alignment(),document.vertical()));
        player.displayClientMessage(size==document.size()?message("pasted",document.glyphs().size()):message("pasted_resized",document.glyphs().size(),size),true);return true;
    }
    public static void clear(Player player,ItemStack stack){
        CompoundTag tag=data(stack);tag.remove("display_snapshot");tag.remove("display_characters");
        if(tag.isEmpty())stack.remove(DataComponents.CUSTOM_DATA);else stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));
        player.displayClientMessage(message("cleared"),true);
    }
    @Override public InteractionResult onItemUseFirst(ItemStack stack,UseOnContext context){
        Player player=context.getPlayer();if(player==null)return InteractionResult.PASS;
        if(player.isShiftKeyDown()){
            if(!context.getLevel().isClientSide)clear(player,stack);
            return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
        }
        if(context.getLevel().getBlockEntity(context.getClickedPos()) instanceof DisplayBlockEntity be){
            if(beImage(context))return InteractionResult.PASS;
            if(player instanceof ServerPlayer server)copy(server,stack,be);
            return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
        }
        return InteractionResult.PASS;
    }
    private static boolean beImage(UseOnContext c){return c.getLevel().getBlockEntity(c.getClickedPos()) instanceof DisplayBlockEntity be&&be.imageScreen();}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){
        ItemStack stack=player.getItemInHand(hand);
        if(player.isShiftKeyDown()){if(!level.isClientSide)clear(player,stack);return InteractionResultHolder.sidedSuccess(stack,level.isClientSide);}
        return InteractionResultHolder.pass(stack);
    }
    @Override public boolean canAttackBlock(BlockState state,Level level,BlockPos pos,Player player){return !(state.getBlock() instanceof DisplayBlock b)||b.imageScreen;}
    public static void leftClick(PlayerInteractEvent.LeftClickBlock event){
        if(!(event.getEntity().getMainHandItem().getItem() instanceof DataCopierItem)
            ||!(event.getLevel().getBlockState(event.getPos()).getBlock() instanceof DisplayBlock))return;
        if(event.getLevel().getBlockState(event.getPos()).getBlock() instanceof DisplayBlock b&&b.imageScreen)return;
        event.setCanceled(true);
        if(event.getLevel().isClientSide&&event.getAction()==PlayerInteractEvent.LeftClickBlock.Action.START){
            var meta=new DisplayNetwork.Meta(0,1,1,1024,event.getPos(),new UUID(0,0),0);
            DisplayNetwork.sendClient(new DisplayNetwork.Packet(7,new UUID(0,0),DisplayDocument.EMPTY,meta));
        }
    }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){
        lines.add(message("description").copy().withStyle(ChatFormatting.GRAY));
        if(shiftDown.getAsBoolean()){
            for(String key:List.of("help_copy","help_paste","help_clear","help_format"))lines.add(message(key).copy().withStyle(ChatFormatting.GRAY));
            lines.add(message("stored",data(stack).getInt("display_characters")).copy().withStyle(ChatFormatting.DARK_GRAY));
        }else lines.add(message("shift").copy().withStyle(ChatFormatting.DARK_GRAY));
    }
}



