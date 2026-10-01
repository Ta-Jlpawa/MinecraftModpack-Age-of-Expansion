package com.tajlpawa.ageofexpansionmodpackfix.gametest.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.nio.file.*;
import java.util.*;

@EventBusSubscriber(modid = "ageofexpansionmodpackfix", value = Dist.CLIENT)
public final class ReportedChunkClientProbe {
    private static final Path EXCHANGE = Path.of("../reportedChunkExchange").toAbsolutePath().normalize();
    private static final Map<BlockPos, String> states = new LinkedHashMap<>();
    private static int phase, ticks, step, round, retries;
    private static String pending;
    private static long started;
    private static final String[] ACTIONS = {"close", "open", "rapid", "remove", "restore"};

    private static void request(String action) throws Exception {
        pending = UUID.randomUUID().toString();
        Files.createDirectories(EXCHANGE);
        Files.write(EXCHANGE.resolve("request.tmp"), List.of(pending, action));
        Files.move(EXCHANGE.resolve("request.tmp"), EXCHANGE.resolve("request.txt"), StandardCopyOption.REPLACE_EXISTING);
    }

    private static boolean received() throws Exception {
        Path reply = EXCHANGE.resolve("reply.txt");
        if (!Files.exists(reply)) return false;
        var lines = Files.readAllLines(reply);
        if (lines.isEmpty() || !lines.getFirst().equals(pending)) return false;
        states.clear();
        for (String line : lines.subList(1, lines.size())) {
            var parts = line.split("\\|", 2); var xyz = parts[0].split(",");
            states.put(new BlockPos(Integer.parseInt(xyz[0]), Integer.parseInt(xyz[1]), Integer.parseInt(xyz[2])), parts[1]);
        }
        pending = null;
        return true;
    }

    private static boolean capture(String label) throws Exception {
        var mc = Minecraft.getInstance();
        Path directory = mc.gameDirectory.toPath().resolve("reported-captures");
        Files.createDirectories(directory);
        try (var image = Screenshot.takeScreenshot(mc.getMainRenderTarget())) {
            image.writeToFile(directory.resolve(round + "-" + label + ".png"));
        }
        int mismatches = 0;
        for (var entry : states.entrySet()) {
            var actual = mc.level.getBlockState(entry.getKey());
            boolean matches = actual.toString().equals(entry.getValue());
            if (!matches) mismatches++;
            System.out.println("REPORTED_CHUNK_PROBE " + label + " pos=" + entry.getKey().toShortString()
                    + " client=" + actual + " server=" + entry.getValue() + " match=" + matches);
        }
        ChunkRenderProbe.inspectRenderedEntities(new BlockPos(6785, 72, -5891));
        if (mismatches > 0) {
            System.out.println("REPORTED_CHUNK_PROBE recheck round=" + round + " action=" + label + " attempt=" + retries);
            if (++retries >= 6) throw new AssertionError("Reported scene mismatches persisted across six delayed samples=" + mismatches);
            return false;
        }
        retries = 0;
        System.out.println("REPORTED_CHUNK_PROBE captured round=" + round + " " + label + " states=" + states.size());
        return true;
    }

    @SubscribeEvent public static void tick(ClientTickEvent.Post event) throws Exception {
        if (!Boolean.getBoolean("ageofexpansion.reportedChunkClient")) return;
        var mc = Minecraft.getInstance();
        if (started == 0) started = System.currentTimeMillis();
        if (System.currentTimeMillis() - started > 600000) throw new AssertionError("Reported scene timeout phase=" + phase);
        if (phase == 0 && mc.screen instanceof TitleScreen && mc.getOverlay() == null) {
            phase = 1;
            mc.options.pauseOnLostFocus = false;
            ConnectScreen.startConnecting(mc.screen, mc, ServerAddress.parseString("127.0.0.1:25588"),
                    new ServerData("Isolated chunk probe", "127.0.0.1:25588", ServerData.Type.OTHER), false, null);
        }
        if (mc.player == null || mc.level == null || mc.getOverlay() != null) return;
        if (phase == 1) { mc.setScreen(null); request("init"); phase = 2; ticks = 0; return; }
        if (pending != null && !received()) return;
        ticks++;
        switch (phase) {
            case 2 -> { if (ticks > 140) { request("snapshot"); phase = 3; ticks = 0; } }
            case 3 -> {
                if (ticks < 20) return;
                if (!capture("loaded")) { request("snapshot"); ticks = 0; return; }
                step = 0; request(ACTIONS[step]); phase = 4; ticks = 0;
            }
            case 4 -> {
                if (ACTIONS[step].equals("rapid") && ticks <= 60 && ticks % 2 == 0) {
                    var pos = new BlockPos(6782, 72, -5891);
                    var hit = new net.minecraft.world.phys.BlockHitResult(new net.minecraft.world.phys.Vec3(6783, 73, -5890.5),
                            net.minecraft.core.Direction.EAST, pos, false);
                    mc.player.setYRot(70);
                    var result = mc.gameMode.useItemOn(mc.player, net.minecraft.world.InteractionHand.MAIN_HAND, hit);
                    System.out.println("REPORTED_CHUNK_PROBE rapid click=" + ticks / 2 + " result=" + result);
                }
                if (ticks > (ACTIONS[step].equals("rapid") ? 100 : 60)) { request("snapshot"); phase = 5; ticks = 0; }
            }
            case 5 -> {
                if (ticks < 20) return;
                if (!capture(ACTIONS[step])) { request("snapshot"); ticks = 0; return; }
                if (++step < ACTIONS.length) { request(ACTIONS[step]); phase = 4; ticks = 0; }
                else { request("away"); phase = 6; ticks = 0; }
            }
            case 6 -> { if (ticks > 160 && mc.level.getChunkSource().getChunk(424, -369, ChunkStatus.FULL, false) == null) {
                System.out.println("REPORTED_CHUNK_PROBE actual target chunk unloaded round=" + round);
                if (++round == 3) { System.out.println("REPORTED_CHUNK_PROBE STATE_PASS all rounds complete"); phase = 7; mc.stop(); }
                else { request("home"); phase = 2; ticks = 0; }
            } }
        }
    }
}
