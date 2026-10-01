package com.tajlpawa.ageofexpansionmodpackfix.mixin.mekanism;

import com.tajlpawa.ageofexpansionmodpackfix.explosion.SphericalBlasts;
import mekanism.generators.common.content.fission.FissionReactorMultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// 保留原反应堆损伤与辐射处理，替换熔毁后反复触发的局部爆炸。
@Pseudo
@Mixin(value = FissionReactorMultiblockData.class, remap = false)
public abstract class FissionMeltdownMixin {
    @Shadow private int fuelAssemblies;

    @Inject(method = "createMeltdown", at = @At("HEAD"), cancellable = true, remap = false)
    private void ageofexpansion$singleMeltdown(Level level, CallbackInfo ci) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        FissionReactorMultiblockData reactor = (FissionReactorMultiblockData) (Object) this;
        BlockPos min = reactor.getMinPos();
        BlockPos max = reactor.getMaxPos();
        BlockPos center = new BlockPos(min.getX() + (max.getX() - min.getX()) / 2,
                min.getY() + (max.getY() - min.getY()) / 2, min.getZ() + (max.getZ() - min.getZ()) / 2);
        SphericalBlasts.start(serverLevel, center, SphericalBlasts.radiusForAssemblies(fuelAssemblies), SphericalBlasts.Kind.FISSION);
        reactor.meltdownHappened(level);
        ci.cancel();
    }
}
