package com.tajlpawa.ageofexpansionmodpackfix.gametest.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tajlpawa.ageofexpansionmodpackfix.minecraft.PhantomWardItems;
import net.minecraft.client.Minecraft;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber(modid = "ageofexpansionmodpackfix", value = Dist.CLIENT)
public final class PhantomWardClientSmoke {
    private static boolean checked;
    @SubscribeEvent public static void render(ScreenEvent.Render.Post event) {
        if (checked || !Boolean.getBoolean("ageofexpansion.routerClientSmoke")) return;
        var mc = Minecraft.getInstance();
        if (mc.getOverlay() != null) return;
        var stack = PhantomWardItems.CAT_EAR_HEADBAND.get().getDefaultInstance();
        var model = mc.getItemRenderer().getModel(stack, null, null, 0);
        if (model.usesBlockLight()) throw new AssertionError("Inventory wrapper incorrectly uses block lighting");
        var gui = model.applyTransform(ItemDisplayContext.GUI, new PoseStack(), false);
        var head = model.applyTransform(ItemDisplayContext.HEAD, new PoseStack(), false);
        if (gui == head || gui.getQuads(null, null, RandomSource.create()).isEmpty()) {
            throw new AssertionError("Head model must be separate from the visible inventory sprite");
        }
        if (ModList.get().isLoaded("curios") && top.theillusivec4.curios.api.client.CuriosRendererRegistry.getRenderer(stack.getItem()).isEmpty()) {
            throw new AssertionError("Curios worn renderer not registered");
        }
        var faces = new java.util.ArrayList<>(head.getQuads(null, null, RandomSource.create()));
        for (var direction : net.minecraft.core.Direction.values()) faces.addAll(head.getQuads(null, direction, RandomSource.create()));
        if (faces.isEmpty() || faces.stream().anyMatch(net.minecraft.client.renderer.block.model.BakedQuad::isShade)) {
            throw new AssertionError("Worn model must retain geometry without extra face darkening");
        }
        checked = true;
        com.tajlpawa.ageofexpansionmodpackfix.AgeofExpansionModpackFix.LOGGER.info("Cat ear headband: front lighting, unshaded worn geometry, separate HEAD/GUI models and optional Curios renderer verified");
    }
}
