package com.tajlpawa.ageofexpansionmodpackfix.gametest.client;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.motor.CreativeMotorBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;

// 显式开启后在隔离存档中重放区块卸载、重载、门动画及方块破坏。
@EventBusSubscriber(modid = "ageofexpansionmodpackfix", value = Dist.CLIENT)
public final class ChunkRenderProbe {
    private static final BlockPos DOOR = new BlockPos(8, 80, 8);
    private static final BlockPos PLATE = DOOR.north();
    private static final BlockPos ARM = DOOR.west(3);
    private static final BlockPos MIXER = DOOR.east(3).above(2);
    private static final BlockPos COG = DOOR.west(5).above();
    private static final BlockPos[] TARGETS = {DOOR, DOOR.above(), PLATE, ARM, MIXER, COG};
    private static volatile Map<BlockPos, BlockState> expected;
    private static int phase, ticks, round;
    private static long started;

    private static void log(String message) { System.out.println("CHUNK_RENDER_PROBE " + message); }

    private static void setup(ServerLevel level) {
        level.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(false, level.getServer());
        level.getGameRules().getRule(GameRules.RULE_WEATHER_CYCLE).set(false, level.getServer());
        level.getGameRules().getRule(GameRules.RULE_DOMOBSPAWNING).set(false, level.getServer());
        level.setDayTime(6000);
        for (int x = 0; x < 18; x++) for (int z = 0; z < 16; z++) {
            level.setBlockAndUpdate(new BlockPos(x, 79, z), Blocks.SMOOTH_STONE.defaultBlockState());
        }
        BlockState door = AllBlocks.ANDESITE_DOOR.getDefaultState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH);
        level.setBlockAndUpdate(DOOR, door);
        level.setBlockAndUpdate(DOOR.above(), door.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER));
        level.setBlockAndUpdate(PLATE, Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE.defaultBlockState());
        level.setBlockAndUpdate(ARM, AllBlocks.MECHANICAL_ARM.getDefaultState());
        level.setBlockAndUpdate(MIXER.below(2), AllBlocks.BASIN.getDefaultState());
        level.setBlockAndUpdate(MIXER, AllBlocks.MECHANICAL_MIXER.getDefaultState());
        level.setBlockAndUpdate(COG, AllBlocks.COGWHEEL.getDefaultState().setValue(BlockStateProperties.AXIS, Direction.Axis.Z));
        BlockPos motor = COG.south();
        level.setBlockAndUpdate(motor, AllBlocks.CREATIVE_MOTOR.getDefaultState()
                .setValue(BlockStateProperties.FACING, Direction.NORTH));
        var be = (CreativeMotorBlockEntity) level.getBlockEntity(motor);
        be.generatedSpeed.value = 64;
        be.updateGeneratedRotation();
    }

    private static void serverAction(java.util.function.Consumer<ServerLevel> action) {
        var mc = Minecraft.getInstance();
        expected = null;
        mc.getSingleplayerServer().execute(() -> {
            var level = mc.getSingleplayerServer().overworld();
            action.accept(level);
            Map<BlockPos, BlockState> states = new LinkedHashMap<>();
            for (var pos : TARGETS) states.put(pos, level.getBlockState(pos));
            expected = states;
        });
    }

    private static void move(boolean away) {
        var mc = Minecraft.getInstance();
        serverAction(level -> {
            var player = level.getServer().getPlayerList().getPlayer(mc.player.getUUID());
            player.setGameMode(GameType.CREATIVE);
            player.getAbilities().flying = true;
            player.onUpdateAbilities();
            player.connection.teleport(away ? 2048.5 : 8.5, 81, away ? 2048.5 : 2.0, 0, 12);
        });
    }

    private static void capture(String label) throws Exception {
        var mc = Minecraft.getInstance();
        var directory = mc.gameDirectory.toPath().resolve("probe-captures");
        Files.createDirectories(directory);
        try (var image = Screenshot.takeScreenshot(mc.getMainRenderTarget())) {
            image.writeToFile(directory.resolve(round + "-" + label + ".png"));
        }
        for (var entry : expected.entrySet()) {
            var actual = mc.level.getBlockState(entry.getKey());
            if (actual != entry.getValue()) throw new AssertionError("Client state differs from server at " + entry.getKey()
                    + ": expected " + entry.getValue() + ", got " + actual);
            var be = mc.level.getBlockEntity(entry.getKey());
            log(label + " " + entry.getKey().toShortString() + " " + actual
                    + " BE=" + (be == null ? "null" : be.getClass().getSimpleName())
                    + (be instanceof KineticBlockEntity kinetic ? " speed=" + kinetic.getSpeed() : ""));
        }
        log("captured round=" + round + " phase=" + label + " client/server states match");
        inspectRenderedEntities(DOOR);
    }

    private static Object field(Object object, String name) throws Exception {
        Class<?> type = object.getClass();
        while (type != null) {
            try { var field = type.getDeclaredField(name); field.setAccessible(true); return field.get(object); }
            catch (NoSuchFieldException ignored) { type = type.getSuperclass(); }
        }
        throw new NoSuchFieldException(name);
    }

    public static void inspectRenderedEntities(BlockPos center) throws Exception {
        var mc = Minecraft.getInstance();
        var sodiumType = Class.forName("net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer");
        Object renderer = sodiumType.getMethod("instance").invoke(null);
        java.util.List<net.minecraft.world.level.block.entity.BlockEntity> visible = new java.util.ArrayList<>();
        sodiumType.getMethod("iterateVisibleBlockEntities", java.util.function.Consumer.class)
                .invoke(renderer, (java.util.function.Consumer<net.minecraft.world.level.block.entity.BlockEntity>) visible::add);
        for (var be : visible) {
            if (be.getBlockPos().distSqr(center) < 144 && mc.level.getBlockEntity(be.getBlockPos()) != be)
                throw new AssertionError("Sodium still renders stale block entity at " + be.getBlockPos());
        }
        var manager = dev.engine_room.flywheel.api.visualization.VisualizationManager.get(mc.level);
        if (manager != null) {
            var storage = ((dev.engine_room.flywheel.impl.visualization.VisualManagerImpl<?, ?>) manager.blockEntities()).getStorage();
            var visuals = (Map<?, ?>) field(storage, "visuals");
            for (var key : visuals.keySet()) {
                var be = (net.minecraft.world.level.block.entity.BlockEntity) key;
                if (be.getBlockPos().distSqr(center) < 144 && mc.level.getBlockEntity(be.getBlockPos()) != be)
                    throw new AssertionError("Flywheel still animates stale block entity at " + be.getBlockPos());
            }
            log("render refs: sodium=" + visible.size() + " flywheel=" + visuals.size());
        }
    }

    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event) throws Exception {
        if (!Boolean.getBoolean("ageofexpansion.chunkRenderProbe")) return;
        var mc = Minecraft.getInstance();
        if (started == 0) started = System.currentTimeMillis();
        if (System.currentTimeMillis() - started > 600000) throw new AssertionError("Chunk probe timeout, phase=" + phase);
        if (phase == 0 && mc.screen instanceof TitleScreen && mc.getOverlay() == null) {
            phase = 1;
            if (Files.exists(mc.gameDirectory.toPath().resolve("saves/ChunkRenderProbe/level.dat"))) {
                mc.createWorldOpenFlows().openWorld("ChunkRenderProbe", () -> { throw new AssertionError("Cannot load probe world"); });
            } else {
                var settings = new LevelSettings("ChunkRenderProbe", GameType.CREATIVE, false, Difficulty.PEACEFUL,
                        true, new GameRules(), WorldDataConfiguration.DEFAULT);
                mc.createWorldOpenFlows().createFreshLevel("ChunkRenderProbe", settings, new WorldOptions(42L, false, false),
                        registry -> registry.registryOrThrow(Registries.WORLD_PRESET).getHolderOrThrow(WorldPresets.FLAT)
                                .value().createWorldDimensions(), mc.screen);
            }
            return;
        }
        if (mc.player == null || mc.level == null || mc.getOverlay() != null) return;
        if (phase == 1) {
            mc.setScreen(null);
            serverAction(ChunkRenderProbe::setup);
            phase = 2; ticks = 0; return;
        }
        if (expected == null) return;
        ticks++;
        switch (phase) {
            case 2 -> { if (ticks > 40) { move(false); phase = 3; ticks = 0; } }
            case 3 -> { if (ticks > 100) { capture("closed"); phase = 4; ticks = 0; } }
            case 4 -> { if (ticks > 20) {
                capture("rotation");
                serverAction(level -> { for (var pos : new BlockPos[]{DOOR, DOOR.above()})
                    level.setBlockAndUpdate(pos, level.getBlockState(pos).setValue(BlockStateProperties.OPEN, true)); });
                phase = 5; ticks = 0;
            } }
            case 5 -> { if (ticks > 60) {
                capture("open");
                serverAction(level -> { for (var pos : TARGETS) level.destroyBlock(pos, false); });
                phase = 6; ticks = 0;
            } }
            case 6 -> { if (ticks > 60) {
                capture("removed"); serverAction(ChunkRenderProbe::setup); phase = 7; ticks = 0;
            } }
            case 7 -> { if (ticks > 60) { move(true); phase = 8; ticks = 0; } }
            case 8 -> { if (ticks > 100 && mc.level.getChunkSource().getChunk(0, 0,
                    net.minecraft.world.level.chunk.status.ChunkStatus.FULL, false) == null) {
                log("target chunk unloaded round=" + round);
                if (++round == 3) { log("STATE_PASS: 3 unload/reload rounds; inspect captures for render persistence"); mc.stop(); phase = 10; }
                else { move(false); phase = 3; ticks = 0; }
            } }
        }
    }
}
