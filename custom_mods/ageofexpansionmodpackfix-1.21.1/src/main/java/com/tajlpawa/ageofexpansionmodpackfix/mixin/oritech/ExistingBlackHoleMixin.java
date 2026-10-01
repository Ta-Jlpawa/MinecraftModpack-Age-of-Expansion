package com.tajlpawa.ageofexpansionmodpackfix.mixin.oritech;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// 已在存档中的黑洞在加载后的第一次服务端刻清除。
@Pseudo
@Mixin(targets = "rearth.oritech.block.entity.accelerator.BlackHoleBlockEntity", remap = false)
public abstract class ExistingBlackHoleMixin {
    @Inject(method = "serverTick", at = @At("HEAD"), cancellable = true, remap = false)
    private void ageofexpansion$removeExisting(CallbackInfo ci) {
        BlockEntity entity = (BlockEntity) (Object) this;
        if (entity.getLevel() instanceof ServerLevel serverLevel) serverLevel.setBlock(entity.getBlockPos(), Blocks.AIR.defaultBlockState(), 3);
        ci.cancel();
    }
}
