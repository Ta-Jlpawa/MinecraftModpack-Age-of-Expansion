package com.tajlpawa.ageofexpansionmodpackfix.mixin.create;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.tajlpawa.ageofexpansionmodpackfix.create.StressRouterBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = KineticBlockEntity.class, remap = false)
public abstract class RouterKineticMixin {
    // Resolve virtual ports after the lookup, preserving Create's checks and other mods' redirects.
    @ModifyVariable(method = {"setSource", "validateKinetics"}, at = @At("STORE"), ordinal = 0)
    private BlockEntity routerSource(BlockEntity source) {
        if (source instanceof StressRouterBlockEntity router) {
            return router.portTowards(((KineticBlockEntity) (Object) this).getBlockPos());
        }
        return source;
    }
}
