package com.tajlpawa.ageofexpansionmodpackfix.mixin.mekanism;

import java.util.Collection;
import java.util.Collections;
import mekanism.common.content.network.transmitter.LogisticalTransporterBase;
import mekanism.common.content.transporter.TransporterStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// 仅跳过管道内的移动物品绘制，保留分流管道的配置覆盖层。
@Mixin(targets = "mekanism.client.render.transmitter.RenderLogisticalTransporter", remap = false)
public abstract class LogisticalTransporterRenderMixin {
    @Redirect(method = "render(Lmekanism/common/tile/transmitter/TileEntityLogisticalTransporterBase;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/util/profiling/ProfilerFiller;)V",
            at = @At(value = "INVOKE", target = "Lmekanism/common/content/network/transmitter/LogisticalTransporterBase;getTransit()Ljava/util/Collection;"), remap = false)
    private Collection<TransporterStack> ageofexpansion$hideTransitItems(LogisticalTransporterBase transporter) {
        return Collections.emptyList();
    }
}
