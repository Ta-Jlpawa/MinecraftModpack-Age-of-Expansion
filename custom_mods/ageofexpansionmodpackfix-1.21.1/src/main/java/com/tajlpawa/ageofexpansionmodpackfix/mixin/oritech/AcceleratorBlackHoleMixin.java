package com.tajlpawa.ageofexpansionmodpackfix.mixin.oritech;

import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// 禁止粒子加速器的碰撞事件生成黑洞及其前置爆炸。
@Pseudo
@Mixin(targets = "rearth.oritech.block.entity.accelerator.AcceleratorControllerBlockEntity", remap = false)
public abstract class AcceleratorBlackHoleMixin {
    @Inject(method = "createBlackHole", at = @At("HEAD"), cancellable = true, remap = false)
    private void ageofexpansion$disableBlackHole(BlockPos pos, CallbackInfo ci) {
        ci.cancel();
    }
}
