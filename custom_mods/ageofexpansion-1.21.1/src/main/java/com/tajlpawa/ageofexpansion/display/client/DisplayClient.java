package com.tajlpawa.ageofexpansion.display.client;

import com.tajlpawa.ageofexpansion.AgeofExpansion;
import com.tajlpawa.ageofexpansion.display.DisplayContent;
import com.tajlpawa.ageofexpansion.display.DisplayNetwork;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = AgeofExpansion.MODID, value = Dist.CLIENT)
public final class DisplayClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event) { event.registerBlockEntityRenderer(DisplayContent.ENTITY.get(), DisplayRenderer::new); }
    @SubscribeEvent public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(()->com.tajlpawa.ageofexpansion.display.DataCopierItem.shiftDown=net.minecraft.client.gui.screens.Screen::hasShiftDown);
        event.enqueueWork(() -> DisplayNetwork.clientReceiver = packet -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (packet.action() == 0) minecraft.setScreen(new DisplayScreen(packet.token(), packet.document(), packet.meta()));
            else if (minecraft.screen instanceof DisplayScreen screen) screen.acknowledge(packet);
        });
    }
    @SubscribeEvent public static void tick(net.neoforged.neoforge.client.event.ClientTickEvent.Post event) {
        if(Minecraft.getInstance().getConnection()!=null) DisplayNetwork.clientTick();
    }
    @SubscribeEvent public static void render(net.neoforged.neoforge.client.event.RenderLevelStageEvent event) {
        if(event.getStage()==net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage.AFTER_SKY) DisplayRenderer.beginFrame();
    }
    @SubscribeEvent public static void logout(net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingOut event) { DisplayNetwork.clearClient(); }
}
