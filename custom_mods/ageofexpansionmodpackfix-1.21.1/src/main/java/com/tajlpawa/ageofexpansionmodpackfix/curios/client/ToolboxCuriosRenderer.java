package com.tajlpawa.ageofexpansionmodpackfix.curios.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import com.tajlpawa.ageofexpansionmodpackfix.create.client.ToolboxWornModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.client.ICurioRenderer;

// All toolbox colors share a replaceable, initially empty JSON model attached to the torso.
public final class ToolboxCuriosRenderer implements ICurioRenderer {
    public static void register() {
        for (DyeColor color : DyeColor.values()) CuriosRendererRegistry.register(AllBlocks.TOOLBOXES.get(color).asItem(), ToolboxCuriosRenderer::new);
    }

    @Override public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext context,
            PoseStack pose, RenderLayerParent<T, M> renderer, MultiBufferSource buffers, int light, float limbSwing,
            float limbSwingAmount, float partialTicks, float ageInTicks, float headYaw, float headPitch) {
        if (!(renderer.getModel() instanceof HumanoidModel<?> humanoid)) return;
        pose.pushPose();
        humanoid.body.translateAndRotate(pose);
        pose.translate(0, 0.65, 0);
        pose.scale(1, -1, -1);
        var mc = Minecraft.getInstance();
        var model = mc.getModelManager().getModel(ModelResourceLocation.standalone(ToolboxWornModel.LOCATION));
        mc.getItemRenderer().render(stack, ItemDisplayContext.FIXED, false, pose, buffers, light, OverlayTexture.NO_OVERLAY, model);
        pose.popPose();
    }
}
