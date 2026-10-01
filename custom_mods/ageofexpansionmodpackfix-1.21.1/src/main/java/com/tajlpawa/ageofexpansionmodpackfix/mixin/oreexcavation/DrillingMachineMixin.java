package com.tajlpawa.ageofexpansionmodpackfix.mixin.oreexcavation;

import com.tajlpawa.ageofexpansionmodpackfix.oreexcavation.DrillDurability;
import com.tom.createores.block.entity.ExcavatingBlockEntity;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.tom.createores.block.entity.DrillBlockEntity", remap = false)
public abstract class DrillingMachineMixin {
    @Inject(method = "canExtract", at = @At("HEAD"), cancellable = true)
    private void stopExhausted(CallbackInfoReturnable<Boolean> ci) {
        if (DrillDurability.exhausted(((ExcavatingBlockEntity<?>) (Object) this).getDrill())) ci.setReturnValue(false);
    }
    @Inject(method = "onFinished", at = @At("RETURN"))
    private void wearOnSuccess(CallbackInfo ci) {
        var machine = (ExcavatingBlockEntity<?>) (Object) this;
        if (machine.getLevel() instanceof ServerLevel level) {
            DrillDurability.wear(machine.getDrill(), level);
            machine.setChanged();
            machine.sendData();
        }
    }
}
