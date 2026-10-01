package com.tajlpawa.ageofexpansionmodpackfix.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.BasePressurePlateBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.nio.file.*;
import java.util.*;

// 仅在本地隔离服务端显式启用，使用文件信道控制复现场景并返回权威方块状态。
@EventBusSubscriber(modid = "ageofexpansionmodpackfix")
public final class ReportedChunkServerProbe {
    private static final Path EXCHANGE = Path.of("../reportedChunkExchange").toAbsolutePath().normalize();
    private static final Map<BlockPos, BlockState> original = new LinkedHashMap<>();
    private static ServerPlayer player;
    private static String lastRequest = "";

    @SubscribeEvent public static void login(PlayerEvent.PlayerLoggedInEvent event) {
        if (!Boolean.getBoolean("ageofexpansion.reportedChunkServer")) return;
        player = (ServerPlayer) event.getEntity();
        player.setGameMode(GameType.CREATIVE);
        player.getAbilities().flying = true;
        player.onUpdateAbilities();
        System.out.println("REPORTED_CHUNK_PROBE server player connected");
    }

    @SubscribeEvent public static void tick(ServerTickEvent.Post event) throws Exception {
        if (!Boolean.getBoolean("ageofexpansion.reportedChunkServer") || player == null) return;
        Path request = EXCHANGE.resolve("request.txt");
        if (!Files.exists(request)) return;
        var lines = Files.readAllLines(request);
        if (lines.size() != 2 || lines.getFirst().equals(lastRequest)) return;
        lastRequest = lines.getFirst();
        String action = lines.get(1);
        var level = player.serverLevel();
        switch (action) {
            case "init" -> {
                for (var pos : BlockPos.betweenClosed(6778, 71, -5901, 6791, 75, -5885)) {
                    var state = level.getBlockState(pos);
                    if (state.getBlock() instanceof DoorBlock || state.getBlock() instanceof BasePressurePlateBlock)
                        original.put(pos.immutable(), state);
                }
                player.connection.teleport(6785.0, 72, -5892.0, 180, 12);
            }
            case "home" -> player.connection.teleport(6785.0, 72, -5892.0, 180, 12);
            case "away" -> player.connection.teleport(7300, 100, -5892, 180, 12);
            case "close", "open" -> {
                for (var pos : original.keySet()) {
                    var state = level.getBlockState(pos);
                    if (state.getBlock() instanceof DoorBlock)
                        level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.OPEN, action.equals("open")));
                }
            }
            case "remove" -> { for (var pos : original.keySet()) level.destroyBlock(pos, false); }
            case "restore" -> original.forEach(level::setBlockAndUpdate);
            case "snapshot", "rapid" -> {}
            default -> throw new IllegalArgumentException("Unknown probe action " + action);
        }
        List<String> reply = new ArrayList<>();
        reply.add(lastRequest);
        original.keySet().forEach(pos -> reply.add(pos.getX() + "," + pos.getY() + "," + pos.getZ() + "|" + level.getBlockState(pos)));
        Files.createDirectories(EXCHANGE);
        Files.write(EXCHANGE.resolve("reply.tmp"), reply);
        Files.move(EXCHANGE.resolve("reply.tmp"), EXCHANGE.resolve("reply.txt"), StandardCopyOption.REPLACE_EXISTING);
        System.out.println("REPORTED_CHUNK_PROBE server action=" + action + " tracked=" + original.size());
    }

    @SubscribeEvent public static void logout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (Boolean.getBoolean("ageofexpansion.reportedChunkServer") && player == event.getEntity()) player.server.halt(false);
    }
}
