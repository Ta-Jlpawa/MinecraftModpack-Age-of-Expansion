package com.tajlpawa.ageofexpansionmodpackfix.gametest;

import com.tajlpawa.ageofexpansionmodpackfix.mekanism.MekanismItems;
import java.util.ArrayList;
import java.util.Collection;
import mekanism.api.tier.AlloyTier;
import mekanism.common.lib.transmitter.ConnectionType;
import mekanism.common.tile.transmitter.TileEntityTransmitter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@GameTestHolder(TransmitterInstallerTests.MOD_ID)
@EventBusSubscriber(modid = TransmitterInstallerTests.MOD_ID)
public class TransmitterInstallerTests {
    static final String MOD_ID = "ageofexpansionmodpackfix";
    private static final String BATCH = MOD_ID + ":installers";
    private static final String TEMPLATE = MOD_ID + ":installer_empty";

    @SubscribeEvent
    public static void emptyTemplate(ServerStartingEvent event) throws Exception {
        ServerLevel level = event.getServer().overworld();
        // In-memory template keeps all placements inside test bounds without binary resources.
        level.getStructureManager().getOrCreate(ResourceLocation.parse(TEMPLATE)).load(
              level.registryAccess().lookupOrThrow(Registries.BLOCK),
              TagParser.parseTag("{size:[14,5,5],entities:[],blocks:[],palette:[{Name:\"minecraft:air\"}]}"));
    }

    @GameTestGenerator
    public static Collection<TestFunction> tests() {
        Collection<TestFunction> tests = new ArrayList<>();
        for (String kind : new String[]{"universal_cable", "mechanical_pipe", "pressurized_tube",
              "logistical_transporter", "thermodynamic_conductor"}) {
            tests.add(new TestFunction(BATCH, MOD_ID + ":" + kind + "/nearest_eight_and_migration",
                  TEMPLATE, 100, 0, true, helper -> upgradeNetwork(helper, kind)));
            tests.add(new TestFunction(BATCH, MOD_ID + ":" + kind + "/reject_same_or_higher",
                  TEMPLATE, 100, 0, true, helper -> rejectInstallers(helper, kind)));
            tests.add(new TestFunction(BATCH, MOD_ID + ":" + kind + "/reject_alloys",
                  TEMPLATE, 100, 0, true, helper -> rejectAlloys(helper, kind)));
        }
        return tests;
    }

    private static Block block(String tier, String kind) {
        return BuiltInRegistries.BLOCK.getOptional(ResourceLocation.fromNamespaceAndPath("mekanism", tier + "_" + kind))
              .orElseThrow();
    }

    private static TileEntityTransmitter tile(GameTestHelper helper, BlockPos pos) {
        helper.assertTrue(helper.getBlockEntity(pos) instanceof TileEntityTransmitter, "Missing transmitter at " + pos);
        return (TileEntityTransmitter) helper.getBlockEntity(pos);
    }

    private static InteractionResult use(GameTestHelper helper, Player player, ItemStack stack, BlockPos pos) {
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        BlockPos absolute = helper.absolutePos(pos);
        return stack.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND,
              new BlockHitResult(Vec3.atCenterOf(absolute), Direction.UP, absolute, false)));
    }

    private static void upgradeNetwork(GameTestHelper helper, String kind) {
        // Place in reverse order and click the far end: selection must be by distance, not insertion order.
        for (int x = 10; x >= 1; x--) {
            BlockPos pos = new BlockPos(x, 2, 2);
            helper.setBlock(pos, block("basic", kind));
            tile(helper, pos).getTransmitter().setConnectionTypeRaw(Direction.UP, ConnectionType.NONE);
            tile(helper, pos).getTransmitter().setConnectionTypeRaw(Direction.DOWN, ConnectionType.PULL);
        }
        BlockPos isolated = new BlockPos(10, 2, 4);
        helper.setBlock(isolated, block("basic", kind));
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack stack = new ItemStack(MekanismItems.ULTIMATE_TRANSMITTER_INSTALLER.get(), 3);
        helper.startSequence().thenWaitUntil(() -> {
            var network = tile(helper, new BlockPos(10, 2, 2)).getTransmitter().getTransmitterNetwork();
            helper.assertTrue(network != null && network.getTransmitters().size() == 10, "Waiting for ten-member network");
            for (int x = 1; x <= 10; x++) {
                helper.assertTrue(tile(helper, new BlockPos(x, 2, 2)).getTransmitter().getTransmitterNetwork() == network,
                      "All ten transmitters must share a network");
            }
            helper.assertTrue(tile(helper, isolated).getTransmitter().hasTransmitterNetwork(), "Waiting for isolated network");
            helper.assertTrue(tile(helper, isolated).getTransmitter().getTransmitterNetwork() != network, "Isolated network must differ");
        }).thenExecute(() -> {
            helper.assertTrue(use(helper, player, stack, new BlockPos(10, 2, 2)).consumesAction(), "Installer should succeed");
            helper.assertValueEqual(stack.getCount(), 2, "Exactly one installer consumed");
            assertUpgrade(helper, kind, isolated);
        }).thenIdle(5).thenExecute(() -> assertUpgrade(helper, kind, isolated)).thenSucceed();
    }

    private static void assertUpgrade(GameTestHelper helper, String kind, BlockPos isolated) {
        for (int x = 1; x <= 10; x++) {
            BlockPos pos = new BlockPos(x, 2, 2);
            helper.assertBlockPresent(block(x >= 3 ? "ultimate" : "basic", kind), pos);
            var transmitter = tile(helper, pos).getTransmitter();
            helper.assertValueEqual(transmitter.getConnectionTypeRaw(Direction.UP), ConnectionType.NONE, "UP connection at " + x);
            helper.assertValueEqual(transmitter.getConnectionTypeRaw(Direction.DOWN), ConnectionType.PULL, "DOWN connection at " + x);
            helper.assertValueEqual(transmitter.getConnectionTypeRaw(Direction.EAST), ConnectionType.NORMAL, "EAST connection at " + x);
            helper.assertValueEqual(transmitter.getConnectionTypeRaw(Direction.WEST), ConnectionType.NORMAL, "WEST connection at " + x);
        }
        helper.assertBlockPresent(block("basic", kind), isolated);
    }

    private static void rejectInstallers(GameTestHelper helper, String kind) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        String[] tiers = {"advanced", "elite", "ultimate"};
        var installers = new net.minecraft.world.item.Item[]{MekanismItems.ADVANCED_TRANSMITTER_INSTALLER.get(),
              MekanismItems.ELITE_TRANSMITTER_INSTALLER.get(), MekanismItems.ULTIMATE_TRANSMITTER_INSTALLER.get()};
        for (int i = 0; i < tiers.length; i++) {
            helper.setBlock(new BlockPos(2 + i * 3, 2, 2), block(tiers[i], kind));
        }
        helper.startSequence().thenWaitUntil(() -> {
            for (int i = 0; i < tiers.length; i++) {
                helper.assertTrue(tile(helper, new BlockPos(2 + i * 3, 2, 2)).getTransmitter().hasTransmitterNetwork(), "Waiting for network");
            }
        }).thenExecute(() -> {
            for (int target = 0; target < tiers.length; target++) {
                BlockPos pos = new BlockPos(2 + target * 3, 2, 2);
                for (int installer = 0; installer <= target; installer++) {
                    ItemStack stack = new ItemStack(installers[installer], 3);
                    helper.assertValueEqual(use(helper, player, stack, pos), InteractionResult.PASS, "Reject same/higher target");
                    helper.assertValueEqual(stack.getCount(), 3, "Rejected installer not consumed");
                    helper.assertBlockPresent(block(tiers[target], kind), pos);
                }
            }
        }).thenSucceed();
    }

    private static void rejectAlloys(GameTestHelper helper, String kind) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        String[] tiers = {"basic", "advanced", "elite"};
        String[] alloys = {"alloy_infused", "alloy_reinforced", "alloy_atomic"};
        AlloyTier[] alloyTiers = {AlloyTier.INFUSED, AlloyTier.REINFORCED, AlloyTier.ATOMIC};
        for (int i = 0; i < tiers.length; i++) {
            helper.setBlock(new BlockPos(2 + i * 3, 2, 2), block(tiers[i], kind));
        }
        helper.startSequence().thenWaitUntil(() -> {
            for (int i = 0; i < tiers.length; i++) {
                helper.assertTrue(tile(helper, new BlockPos(2 + i * 3, 2, 2)).getTransmitter().hasTransmitterNetwork(), "Waiting for alloy target network");
            }
        }).thenExecute(() -> {
            for (int i = 0; i < tiers.length; i++) {
                BlockPos pos = new BlockPos(2 + i * 3, 2, 2);
                ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.getOptional(
                      ResourceLocation.fromNamespaceAndPath("mekanism", alloys[i])).orElseThrow(), 3);
                use(helper, player, stack, pos);
                helper.assertBlockPresent(block(tiers[i], kind), pos);
                helper.assertValueEqual(stack.getCount(), 3, "Alloy use must not consume");
                // Bypass the alloy item's config gate to test the production Mixin's HEAD guard itself.
                tile(helper, pos).onAlloyInteraction(player, stack, alloyTiers[i]);
                helper.assertBlockPresent(block(tiers[i], kind), pos);
                helper.assertValueEqual(stack.getCount(), 3, "Direct alloy interaction must not consume");
            }
        }).thenSucceed();
    }
}
