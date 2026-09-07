package com.tajlpawa.ageofexpansionmodpackfix.create;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import com.simibubi.create.content.kinetics.base.IRotate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public final class StressRouterPort extends StressPortBlockEntity {
    private static final AtomicLong IDS = new AtomicLong(new java.security.SecureRandom().nextLong());
    public final StressRouterBlockEntity router;
    public final Direction face;
    public StressRouterPort(StressRouterBlockEntity router, Direction face) {
        super(router.getBlockPos(), StressRouterContent.CONNECTOR.get().defaultBlockState().setValue(StressRouterBlock.FACING, face));
        this.router = router;
        this.face = face;
        owner = router.getBlockPos();
        setLevel(router.getLevel());
    }
    @Override public List<BlockPos> addPropagationLocations(IRotate block, BlockState state, List<BlockPos> neighbours) {
        neighbours.removeIf(pos -> !pos.equals(worldPosition.relative(face)));
        return neighbours;
    }
    @Override public Long createNetworkId() {
        // Reserve IDs whose encoded X lies outside Minecraft's legal world border.
        long id;
        do { id = ((long) 33_554_431 << 38) | (IDS.incrementAndGet() & ((1L << 38) - 1)); }
        while (com.tajlpawa.ageofexpansionmodpackfix.mixin.create.RouterTorqueAccessor.routerNetworks().getOrDefault(level, java.util.Map.of()).containsKey(id));
        return id;
    }
    @Override public void sendData() { if (router != null && router.getLevel() != null && !router.isRemoved()) router.sendData(); }
    @Override public void setChanged() { if (router != null) router.setChanged(); }
    @Override public void restore() {}
    public boolean exists() { return !isRemoved() && !router.isRemoved() && level != null && level.isLoaded(worldPosition) && level.getBlockEntity(worldPosition) == router; }
}
