package com.tajlpawa.ageofexpansionmodpackfix;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = AgeofExpansionModpackFix.MODID, value = Dist.CLIENT)
public final class AgeofExpansionModpackFixClient {
    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        if (ModList.get().isLoaded("curios")) {
            event.enqueueWork(com.tajlpawa.ageofexpansionmodpackfix.curios.client.PhantomWardCuriosRenderer::register);
        }
    }
}
