package com.tajlpawa.ageofexpansionmodpackfix.create;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class StressRouting {
    public static BlockEntity lookup(Level level, BlockPos pos, KineticBlockEntity requester) {
        if (!level.isLoaded(pos)) return null;
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof StressRouterBlockEntity router) return router.portTowards(requester.getBlockPos());
        return entity;
    }
    public static boolean hasPorts(KineticBlockEntity entity) {
        return entity instanceof StressRouterPort || (entity.hasNetwork() && entity.getOrCreateNetwork().members.keySet().stream().anyMatch(StressRouterPort.class::isInstance));
    }
}
