package com.tajlpawa.ageofexpansionmodpackfix.create;

import java.util.List;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class StressRouterBlockEntity extends SmartBlockEntity implements MenuProvider {
    public static final int MAX_STRESS = 16_777_216;
    public int inputA, inputB;
    public int ratio = 50, speedA = 16, speedB = 16;
    public int status;
    public float actualA, actualB;
    private StressRouterPort[] ports;
    private final CompoundTag[] savedPorts = new CompoundTag[3];

    public StressRouterBlockEntity(BlockPos pos, BlockState state) { super(StressRouterContent.ROUTER_ENTITY.get(), pos, state); }
    @Override public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}
    public boolean splitter() { return ((StressRouterBlock) getBlockState().getBlock()).splitter; }
    // Left and right are from the perspective of a player looking at the front face.
    public Direction direction(int port) {
        Direction front = getBlockState().getValue(StressRouterBlock.FACING);
        return port == 0 ? front : port == 1 ? front.getClockWise() : front.getCounterClockWise();
    }
    public boolean ownsPort(BlockPos pos) {
        for (int i = 0; i < 3; i++) if (worldPosition.relative(direction(i)).equals(pos)) return true;
        return false;
    }

    public StressRouterPort port(int index) {
        ensurePorts();
        return ports == null ? null : ports[index];
    }
    public StressRouterPort portTowards(BlockPos neighbour) {
        for (int i = 0; i < 3; i++) if (worldPosition.relative(direction(i)).equals(neighbour)) return port(i);
        return null;
    }
    private void ensurePorts() {
        if (ports != null || level == null || isRemoved()) return;
        ports = new StressRouterPort[3];
        for (int i = 0; i < 3; i++) ports[i] = new StressRouterPort(this, direction(i));
        for (int i = 0; i < 3; i++) {
            if (savedPorts[i] != null) ports[i].loadWithComponents(savedPorts[i], level.registryAccess());
            savedPorts[i] = null;
        }
    }
    private void migrateLegacyPorts() {
        for (int i = 0; i < 3; i++) {
            BlockPos pos = worldPosition.relative(direction(i));
            if (!level.isLoaded(pos)) continue;
            if (level.getBlockEntity(pos) instanceof StressPortBlockEntity old && !(old instanceof StressRouterPort)
                    && worldPosition.equals(old.owner)) old.restore();
        }
    }

    @Override public void tick() {
        super.tick();
        ensurePorts();
        if (ports == null) return;
        for (StressRouterPort port : ports) port.tick();
        if (level.isClientSide) return;
        migrateLegacyPorts();
        updateRedstone();
        if (getBlockState().getValue(BlockStateProperties.POWERED)) return;
        StressPortBlockEntity front = port(0), left = port(1), right = port(2);
        StressPortBlockEntity[] inputs = splitter() ? new StressPortBlockEntity[]{front} : new StressPortBlockEntity[]{left, right};
        StressPortBlockEntity[] outputs = splitter() ? new StressPortBlockEntity[]{left, right} : new StressPortBlockEntity[]{front};
        for (int i = 0; i < inputs.length; i++) {
            if (inputs[i] != null) { inputs[i].setOutput(0, 0); inputs[i].setDemand(i == 0 ? inputA : inputB); }
        }
        for (StressPortBlockEntity output : outputs) if (output != null) output.setDemand(0);

        boolean feedback = sameNetwork(inputs, outputs);
        float a = inputs[0] == null ? 0 : inputs[0].deliveredInput();
        float b = inputs.length < 2 || inputs[1] == null ? 0 : inputs[1].deliveredInput();
        float total = feedback ? 0 : a + b;
        actualA = splitter() ? total * ratio / 100f : total;
        actualB = splitter() ? total - actualA : 0;
        int maxSpeed = Math.min(256, AllConfigs.server().kinetics.maxRotationSpeed.get());
        for (int i = 0; i < outputs.length; i++) {
            if (outputs[i] != null) outputs[i].setOutput(i == 0 ? actualA : actualB,
                    (int) StressPortBlockEntity.convertToDirection(Math.clamp(i == 0 ? speedA : speedB, -maxSpeed, maxSpeed),
                            splitter() ? direction(i + 1) : direction(0)));
        }
        // New output propagation can reveal a feedback loop in this same tick.
        if (!feedback && sameNetwork(inputs, outputs)) {
            feedback = true;
            for (StressPortBlockEntity output : outputs) if (output != null) output.setOutput(0, 0);
            actualA = actualB = 0;
        }
        int nextStatus = feedback ? 2 : total > 0 ? 1 : 0;
        if (status != nextStatus) { status = nextStatus; sendData(); }
    }

    public void updateRedstone() {
        if (level == null || level.isClientSide || isRemoved()) return;
        boolean powered = level.hasNeighborSignal(worldPosition);
        if (getBlockState().getValue(BlockStateProperties.POWERED) != powered)
            level.setBlock(worldPosition, getBlockState().setValue(BlockStateProperties.POWERED, powered), Block.UPDATE_CLIENTS);
        if (powered) {
            if (ports != null) for (StressRouterPort port : ports) { port.setDemand(0); port.setOutput(0, 0); }
            actualA = actualB = 0;
            if (status != 3) { status = 3; sendData(); }
        }
    }

    private static boolean sameNetwork(StressPortBlockEntity[] inputs, StressPortBlockEntity[] outputs) {
        for (StressPortBlockEntity input : inputs)
            for (StressPortBlockEntity output : outputs)
                if (input != null && output != null && input.hasNetwork() && input.network.equals(output.network)) return true;
        return false;
    }

    public void releasePorts() {
        if (level == null || level.isClientSide) return;
        if (ports != null) {
            for (StressRouterPort port : ports) { port.setDemand(0); port.setOutput(0, 0); port.setRemoved(); }
            ports = null;
        }
        migrateLegacyPorts();
    }

    @Override public void remove() { releasePorts(); super.remove(); }
    @Override public void onChunkUnloaded() {
        if (ports != null) for (StressRouterPort port : ports) port.onChunkUnloaded();
        super.onChunkUnloaded();
    }
    public static int safeSpeed(int value) { return value == 0 ? 1 : Math.clamp(value, -256, 256); }

    public static float safeStress(float value) { return Float.isFinite(value) ? Math.clamp(value, 0, MAX_STRESS) : 0; }
    public void configure(int a, int b, int split, int rpmA, int rpmB) {
        inputA = Math.clamp(a, 0, MAX_STRESS);
        inputB = Math.clamp(b, 0, MAX_STRESS);
        ratio = Math.clamp(split, 0, 100);
        speedA = safeSpeed(rpmA);
        speedB = safeSpeed(rpmB);
        setChanged();
        sendData();
    }
    @Override protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean packet) {
        super.write(tag, registries, packet);
        tag.putInt("InputA", inputA); tag.putInt("InputB", inputB); tag.putInt("Ratio", ratio);
        tag.putInt("SpeedA", speedA); tag.putInt("SpeedB", speedB); tag.putInt("Status", status);
        if (ports != null) for (int i = 0; i < 3; i++) if (ports[i] != null) tag.put("Port" + i, ports[i].saveWithoutMetadata(registries));
    }
    @Override protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean packet) {
        super.read(tag, registries, packet);
        inputA = Math.clamp(tag.getInt("InputA"), 0, MAX_STRESS);
        inputB = Math.clamp(tag.getInt("InputB"), 0, MAX_STRESS);
        ratio = tag.contains("Ratio") ? Math.clamp(tag.getInt("Ratio"), 0, 100) : 50;
        speedA = tag.contains("SpeedA") ? safeSpeed(tag.getInt("SpeedA")) : 16;
        speedB = tag.contains("SpeedB") ? safeSpeed(tag.getInt("SpeedB")) : 16;
        status = tag.getInt("Status");
        for (int i = 0; i < 3; i++) {
            if (tag.contains("Port" + i)) {
                if (packet && ports != null) ports[i].loadWithComponents(tag.getCompound("Port" + i), registries);
                else savedPorts[i] = tag.getCompound("Port" + i).copy();
            }
        }
    }
    public int menuValue(int index) {
        return switch (index) {
            case 0 -> inputA; case 1 -> inputB; case 2 -> ratio; case 3 -> speedA; case 4 -> speedB;
            case 5 -> splitter() ? 1 : 0; case 6 -> status; case 7 -> Math.round(actualA); case 8 -> Math.round(actualB);
            case 11 -> 1; default -> 0;
        };
    }
    public void writeMenuData(net.minecraft.network.RegistryFriendlyByteBuf buf) { for (int i = 0; i < 12; i++) buf.writeInt(menuValue(i)); }
    @Override public Component getDisplayName() { return Component.translatable(getBlockState().getBlock().getDescriptionId()); }
    @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) { return new StressRouterMenu(id, inventory, this); }
}
