package com.tajlpawa.ageofexpansionmodpackfix.minecraft;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

public final class PhantomWardItem extends Item implements Equipable {
    public PhantomWardItem() { super(new Item.Properties().stacksTo(1)); }
    @Override public EquipmentSlot getEquipmentSlot() { return EquipmentSlot.HEAD; }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return swapWithEquipmentSlot(this, level, player, hand);
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        lines.add(Component.translatable("tooltip.ageofexpansionmodpackfix.cat_ear_headband").withStyle(ChatFormatting.GRAY));
    }
}
