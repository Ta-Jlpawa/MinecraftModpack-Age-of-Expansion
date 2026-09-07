package com.tajlpawa.ageofexpansionmodpackfix.create;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class StressRouterBlockItem extends BlockItem {
    public StressRouterBlockItem(StressRouterBlock block) { super(block, new Properties()); }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        boolean split = ((StressRouterBlock) getBlock()).splitter;
        tooltip.add(Component.translatable("gui.ageofexpansionmodpackfix.router." + (split ? "split_brief" : "combine_brief")).withStyle(ChatFormatting.GRAY));
        if (net.minecraft.client.gui.screens.Screen.hasShiftDown()) {
            for (String key : new String[]{split ? "split_route" : "combine_route", "ports", "sides", "settings", "redstone_help"})
                tooltip.add(Component.translatable("gui.ageofexpansionmodpackfix.router." + key).withStyle(ChatFormatting.GRAY));
        } else tooltip.add(Component.translatable("gui.ageofexpansionmodpackfix.router.shift_help").withStyle(ChatFormatting.DARK_GRAY));
    }
}
