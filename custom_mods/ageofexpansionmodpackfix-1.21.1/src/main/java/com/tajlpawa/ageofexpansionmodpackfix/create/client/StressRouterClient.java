package com.tajlpawa.ageofexpansionmodpackfix.create.client;

import com.tajlpawa.ageofexpansionmodpackfix.create.StressRouterContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = StressRouterContent.MOD_ID, value = Dist.CLIENT)
public final class StressRouterClient {
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event) { event.register(StressRouterContent.MENU.get(), StressRouterScreen::new); }
}
