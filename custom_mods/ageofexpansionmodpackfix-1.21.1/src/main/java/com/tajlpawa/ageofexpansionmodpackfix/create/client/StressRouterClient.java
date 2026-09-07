package com.tajlpawa.ageofexpansionmodpackfix.create.client;

import com.tajlpawa.ageofexpansionmodpackfix.create.StressRouterContent;
import com.tajlpawa.ageofexpansionmodpackfix.create.StressPortBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = StressRouterContent.MOD_ID, value = Dist.CLIENT)
public final class StressRouterClient {
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event) { event.register(StressRouterContent.MENU.get(), StressRouterScreen::new); }
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event) { event.registerBlockEntityRenderer(StressRouterContent.PORT_ENTITY.get(), PortRenderer::new); }
    // Render through Create's rotating buffer even when Flywheel is enabled; this type has no visual registered.
    private static class PortRenderer implements BlockEntityRenderer<StressPortBlockEntity> {
        PortRenderer(BlockEntityRendererProvider.Context context) {}
        @Override public void render(StressPortBlockEntity be, float partialTick, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
            KineticBlockEntityRenderer.renderRotatingKineticBlock(be,
                    KineticBlockEntityRenderer.shaft(KineticBlockEntityRenderer.getRotationAxisOf(be)), pose,
                    buffers.getBuffer(RenderType.solid()), light);
        }
    }
}
