package com.tajlpawa.ageofexpansionmodpackfix.create;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class StressPortBlockEntity extends GeneratingKineticBlockEntity {
    public BlockPos owner;
    private float demand;
    private float supplied;
    private int rpm;

    public StressPortBlockEntity(BlockPos pos, BlockState state) { super(StressRouterContent.PORT_ENTITY.get(), pos, state); }

    @Override public float getGeneratedSpeed() { return supplied > 0 ? rpm : 0; }
    @Override public float calculateStressApplied() {
        return lastStressApplied = getTheoreticalSpeed() == 0 ? 0 : demand / Math.abs(getTheoreticalSpeed());
    }
    @Override public float calculateAddedStressCapacity() {
        return lastCapacityProvided = rpm == 0 ? 0 : supplied / Math.abs(rpm);
    }

    public void setDemand(float value) {
        if (demand != value) setChanged();
        demand = value;
        if (hasNetwork()) {
            float impact = calculateStressApplied();
            if (Float.compare(getOrCreateNetwork().members.getOrDefault(this, -1f), impact) != 0)
                getOrCreateNetwork().updateStressFor(this, impact);
        }
    }

    public void setOutput(float value, int speed) {
        if (supplied == value && rpm == speed) return;
        // Remove source membership before switching off: Create checks isSource during removal.
        if (hasNetwork()) getOrCreateNetwork().sources.remove(this);
        supplied = value;
        rpm = speed;
        updateGeneratedRotation();
        if (hasNetwork()) {
            if (!isSource()) getOrCreateNetwork().sources.remove(this);
            getOrCreateNetwork().updateCapacity();
        }
        setChanged();
    }

    public float deliveredInput() { return hasNetwork() && getSpeed() != 0 ? demand : 0; }
    public float supplied() { return supplied; }

    @Override public void tick() {
        super.tick();
        if (level.isClientSide) return;
        if (this instanceof StressRouterPort) return;
        if (owner == null) { restore(); return; }
        if (!level.isLoaded(owner)) { setOutput(0, 0); setDemand(0); return; }
        if (!(level.getBlockEntity(owner) instanceof StressRouterBlockEntity router) || !router.ownsPort(worldPosition)) {
            restore();
            return;
        }
        setDemand(demand);
    }

    public void restore() {
        if (level == null || level.isClientSide || isRemoved()) return;
        setOutput(0, 0);
        setDemand(0);
        level.setBlock(worldPosition, AllBlocks.SHAFT.getDefaultState()
                .setValue(BlockStateProperties.AXIS, getBlockState().getValue(BlockStateProperties.AXIS))
                .setValue(BlockStateProperties.WATERLOGGED, getBlockState().getValue(BlockStateProperties.WATERLOGGED)), Block.UPDATE_ALL);
    }

    @Override public void onChunkUnloaded() {
        // Never recalculate or propagate during unload: Create's lookups can reload the unloading chunk.
        if (level != null && !level.isClientSide && hasNetwork()) {
            var network = getOrCreateNetwork();
            network.sources.remove(this);
            network.members.remove(this);
            network.members.keySet().forEach(member -> member.networkDirty = true);
        }
        super.onChunkUnloaded();
    }

    @Override protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        if (owner != null) tag.putLong("Router", owner.asLong());
        tag.putFloat("Demand", demand);
        tag.putFloat("Supplied", supplied);
        tag.putInt("OutputRPM", rpm);
    }

    @Override protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        owner = tag.contains("Router") ? BlockPos.of(tag.getLong("Router")) : null;
        demand = StressRouterBlockEntity.safeStress(tag.getFloat("Demand"));
        float savedSupply = tag.getFloat("Supplied");
        supplied = Float.isFinite(savedSupply) ? Math.clamp(savedSupply, 0, 2f * StressRouterBlockEntity.MAX_STRESS) : 0;
        rpm = Math.clamp(tag.getInt("OutputRPM"), -256, 256);
        super.read(tag, registries, clientPacket);
    }
}
