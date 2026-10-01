package com.tajlpawa.ageofexpansionmodpackfix.oreexcavation;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = "ageofexpansionmodpackfix")
public final class DrillRules {
    @SubscribeEvent public static void anvil(AnvilUpdateEvent event) {
        if (DrillDurability.isDrill(event.getLeft()) && !event.getRight().isEmpty()
                && !event.getRight().is(Items.ENCHANTED_BOOK)) event.setCanceled(true);
    }
    @SubscribeEvent public static void tooltip(ItemTooltipEvent event) {
        var stack = event.getItemStack();
        if (!DrillDurability.isDrill(stack)) return;
        event.getToolTip().add(Component.translatable("tooltip.ageofexpansionmodpackfix.drill_durability",
                Math.max(0, stack.getMaxDamage() - stack.getDamageValue()), stack.getMaxDamage()).withStyle(ChatFormatting.GRAY));
        event.getToolTip().add(Component.translatable("tooltip.ageofexpansionmodpackfix.drill_oil_repair").withStyle(ChatFormatting.GRAY));
        if (DrillDurability.exhausted(stack)) event.getToolTip().add(Component.translatable("tooltip.ageofexpansionmodpackfix.drill_exhausted").withStyle(ChatFormatting.RED));
    }
}
