package com.tajlpawa.ageofexpansionmodpackfix.curios.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tajlpawa.ageofexpansionmodpackfix.minecraft.PhantomWardItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.client.ICurioRenderer;

// Use the same HEAD model and head transform as vanilla helmet-slot item rendering.
public final class PhantomWardCuriosRenderer implements ICurioRenderer {
    public static void register() {
        CuriosRendererRegistry.register(PhantomWardItems.CAT_EAR_HEADBAND.get(), PhantomWardCuriosRenderer::new);
    }
    @Override public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack,
            SlotContext context, PoseStack pose, RenderLayerParent<T, M> renderer, MultiBufferSource buffers,
            int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float headYaw, float headPitch) {
        if (!(renderer.getModel() instanceof HeadedModel headed)) return;
        pose.pushPose();
        headed.getHead().translateAndRotate(pose);
        CustomHeadLayer.translateToHead(pose, false);
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.HEAD, light,
                OverlayTexture.NO_OVERLAY, pose, buffers, context.entity().level(), context.entity().getId());
        pose.popPose();
    }
}
