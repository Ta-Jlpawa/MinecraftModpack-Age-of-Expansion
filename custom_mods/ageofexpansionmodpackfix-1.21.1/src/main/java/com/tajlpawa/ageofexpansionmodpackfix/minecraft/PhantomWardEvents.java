package com.tajlpawa.ageofexpansionmodpackfix.minecraft;

import com.tajlpawa.ageofexpansionmodpackfix.AgeofExpansionModpackFix;
import com.tajlpawa.ageofexpansionmodpackfix.curios.PhantomWardCurios;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerSpawnPhantomsEvent;

@EventBusSubscriber(modid = AgeofExpansionModpackFix.MODID)
public final class PhantomWardEvents {
    private PhantomWardEvents() {}
    // Reject only this player's spawn attempt without changing insomnia statistics or other players.
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPhantomSpawn(PlayerSpawnPhantomsEvent event) {
        var player = event.getEntity();
        if (player.level().isClientSide()) return;
        if (player.getItemBySlot(EquipmentSlot.HEAD).is(PhantomWardItems.CAT_EAR_HEADBAND.get())
                || (ModList.get().isLoaded("curios") && PhantomWardCurios.isWearing(player))) {
            event.setResult(PlayerSpawnPhantomsEvent.Result.DENY);
        }
    }
}
