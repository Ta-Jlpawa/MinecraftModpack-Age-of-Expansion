package com.tajlpawa.ageofexpansionmodpackfix.mixin.create;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.tajlpawa.ageofexpansionmodpackfix.create.StressRouterPort;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = KineticNetwork.class, remap = false)
public abstract class RouterNetworkMixin {
    @WrapOperation(method = {"calculateCapacity", "calculateStress"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"))
    private BlockEntity routerIdentity(Level level, BlockPos pos, Operation<BlockEntity> original, @Local KineticBlockEntity member) {
        if (member instanceof StressRouterPort port) return port.exists() ? port : null;
        return original.call(level, pos);
    }
}
