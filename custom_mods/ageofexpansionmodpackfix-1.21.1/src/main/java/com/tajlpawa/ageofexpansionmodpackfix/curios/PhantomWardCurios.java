package com.tajlpawa.ageofexpansionmodpackfix.curios;

import com.tajlpawa.ageofexpansionmodpackfix.minecraft.PhantomWardItems;
import net.minecraft.world.entity.player.Player;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

// This bridge is only loaded when Curios is installed; cosmetic slots never provide protection.
public final class PhantomWardCurios {
    private PhantomWardCurios() {}
    public static void register() {
        CuriosApi.registerCurio(PhantomWardItems.CAT_EAR_HEADBAND.get(), new ICurioItem() {});
    }
    public static boolean isWearing(Player player) {
        return CuriosApi.getCuriosInventory(player).map(inventory -> {
            var equipped = inventory.getEquippedCurios();
            for (int i = 0; i < equipped.getSlots(); i++) {
                if (equipped.getStackInSlot(i).is(PhantomWardItems.CAT_EAR_HEADBAND.get())) return true;
            }
            return false;
        }).orElse(false);
    }
}
