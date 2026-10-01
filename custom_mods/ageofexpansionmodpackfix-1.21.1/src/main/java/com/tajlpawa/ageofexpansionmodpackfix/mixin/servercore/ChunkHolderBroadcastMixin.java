package com.tajlpawa.ageofexpansionmodpackfix.mixin.servercore;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ChunkHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ChunkHolder.class, priority = 500)
public abstract class ChunkHolderBroadcastMixin {
    @Shadow private boolean hasChangedSections;

    @Invoker(value = "servercore$requiresBroadcast", remap = false)
    protected abstract void ageofexpansion$enqueueBroadcast();

    // 区块暂时不可 tick 时，ServerCore 可能丢弃队列项却留下区段变化；后续变化必须重新入队。
    @Inject(method = "blockChanged", at = @At("TAIL"))
    private void ageofexpansion$requeuePendingChanges(BlockPos pos, CallbackInfo ci) {
        if (hasChangedSections) ageofexpansion$enqueueBroadcast();
    }
}
