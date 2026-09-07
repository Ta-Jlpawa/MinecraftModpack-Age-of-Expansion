package com.tajlpawa.ageofexpansionmodpackfix.mixin.create;

import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.tajlpawa.ageofexpansionmodpackfix.create.StressRouting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(value = GeneratingKineticBlockEntity.class, remap = false)
public abstract class RouterGeneratorMixin {
    @Redirect(method = "setSource", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"))
    private BlockEntity routerSource(Level level, BlockPos pos) { return StressRouting.lookup(level, pos, (KineticBlockEntity) (Object) this); }
}
