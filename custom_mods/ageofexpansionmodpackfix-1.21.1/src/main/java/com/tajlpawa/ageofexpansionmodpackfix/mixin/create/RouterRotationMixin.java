package com.tajlpawa.ageofexpansionmodpackfix.mixin.create;

import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.tajlpawa.ageofexpansionmodpackfix.create.StressRouting;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RotationPropagator.class, remap = false)
public abstract class RouterRotationMixin {
    @Shadow private static List<KineticBlockEntity> getConnectedNeighbours(KineticBlockEntity entity) { throw new AssertionError(); }
    @Shadow private static void propagateNewSource(KineticBlockEntity entity) { throw new AssertionError(); }
    @Redirect(method = "findConnectedNeighbour", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"))
    private static BlockEntity routerNeighbour(Level level, BlockPos target, KineticBlockEntity current, BlockPos neighbour) {
        return StressRouting.lookup(level, target, current);
    }
    @Redirect(method = "handleRemoved", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"))
    private static BlockEntity routerRemoved(Level level, BlockPos target, Level world, BlockPos pos, KineticBlockEntity removed) {
        return StressRouting.lookup(level, target, removed);
    }
    // Keep node identity while traversing a network containing several ports at one position.
    @Inject(method = "propagateMissingSource", at = @At("HEAD"), cancellable = true)
    private static void routerMissingSource(KineticBlockEntity root, CallbackInfo ci) {
        if (!StressRouting.hasPorts(root)) return;
        ci.cancel();
        List<KineticBlockEntity> candidates = new ArrayList<>();
        Deque<KineticBlockEntity> frontier = new ArrayDeque<>();
        Set<KineticBlockEntity> visited = Collections.newSetFromMap(new IdentityHashMap<>());
        BlockPos missing = root.source;
        frontier.add(root);
        while (!frontier.isEmpty()) {
            KineticBlockEntity current = frontier.removeFirst();
            if (!visited.add(current) || current.isRemoved()) continue;
            current.removeSource();
            current.sendData();
            for (KineticBlockEntity neighbour : getConnectedNeighbours(current)) {
                if (neighbour.getBlockPos().equals(missing) || !neighbour.hasSource()) continue;
                if (!neighbour.source.equals(current.getBlockPos())) { candidates.add(neighbour); continue; }
                if (neighbour.isSource()) candidates.add(neighbour);
                frontier.addLast(neighbour);
            }
        }
        for (KineticBlockEntity candidate : candidates) {
            if (candidate.hasSource() || candidate.isSource()) { propagateNewSource(candidate); return; }
        }
    }
}
