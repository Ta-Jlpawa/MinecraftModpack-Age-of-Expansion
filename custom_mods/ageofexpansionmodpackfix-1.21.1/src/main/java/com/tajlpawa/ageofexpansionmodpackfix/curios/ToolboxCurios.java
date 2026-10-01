package com.tajlpawa.ageofexpansionmodpackfix.curios;

import com.simibubi.create.AllBlocks;
import com.tajlpawa.ageofexpansionmodpackfix.create.toolbox.CarriedToolboxes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public final class ToolboxCurios {
    private ToolboxCurios() {}

    public static void register() {
        var curio = new ICurioItem() {
            @Override public boolean canEquip(SlotContext context, ItemStack stack) {
                return context.identifier().equals("belt");
            }
        };
        for (DyeColor color : DyeColor.values()) CuriosApi.registerCurio(AllBlocks.TOOLBOXES.get(color).asItem(), curio);
    }

    // Only active functional belt slots provide access; cosmetic slots never hold working storage.
    public static ItemStack find(Player player) {
        return CuriosApi.getCuriosInventory(player).map(inventory -> {
            var belt = inventory.getStacksHandler("belt").orElse(null);
            if (belt != null) for (int i = 0; i < belt.getSlots(); i++) {
                var stack = belt.getStacks().getStackInSlot(i);
                if (inventory.isSlotActive("belt", i) && CarriedToolboxes.isToolbox(stack)) return stack;
            }
            return ItemStack.EMPTY;
        }).orElse(ItemStack.EMPTY);
    }
}
