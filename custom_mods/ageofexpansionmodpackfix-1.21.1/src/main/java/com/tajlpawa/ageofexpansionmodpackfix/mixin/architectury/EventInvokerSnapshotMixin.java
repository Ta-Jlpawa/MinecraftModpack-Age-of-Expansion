package com.tajlpawa.ageofexpansionmodpackfix.mixin.architectury;

import java.util.ArrayList;
import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Pseudo
@Mixin(targets = "dev.architectury.event.EventFactory$EventImpl", remap = false)
public abstract class EventInvokerSnapshotMixin {
    // Each cached invoker owns a stable snapshot; registration invalidates it for the next dispatch.
    @ModifyArg(method = "update", at = @At(value = "INVOKE",
            target = "Ljava/util/function/Function;apply(Ljava/lang/Object;)Ljava/lang/Object;"), index = 0)
    private Object ageofexpansion$snapshotListeners(Object listeners) {
        return new ArrayList<>((List<?>) listeners);
    }
}
