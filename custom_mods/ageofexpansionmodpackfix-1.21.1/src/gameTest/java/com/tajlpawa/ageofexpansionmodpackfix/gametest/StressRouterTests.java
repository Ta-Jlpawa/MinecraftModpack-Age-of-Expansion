package com.tajlpawa.ageofexpansionmodpackfix.gametest;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.motor.CreativeMotorBlockEntity;
import com.tajlpawa.ageofexpansionmodpackfix.create.*;
import java.util.ArrayList;
import java.util.Collection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.*;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.gametest.GameTestHolder;

@GameTestHolder(StressRouterContent.MOD_ID)
@EventBusSubscriber(modid = StressRouterContent.MOD_ID)
public class StressRouterTests {
    private static final String TEMPLATE = StressRouterContent.MOD_ID + ":router_empty";
    private static final BlockPos CENTER = new BlockPos(7, 2, 7);
    @SubscribeEvent public static void template(ServerStartingEvent event) throws Exception {
        var level = event.getServer().overworld();
        level.getStructureManager().getOrCreate(ResourceLocation.parse(TEMPLATE)).load(
                level.registryAccess().lookupOrThrow(Registries.BLOCK),
                TagParser.parseTag("{size:[15,5,15],entities:[],blocks:[],palette:[{Name:\"minecraft:air\"}]}"));
    }
    @GameTestGenerator public static Collection<TestFunction> tests() {
        var tests = new ArrayList<TestFunction>();
        add(tests, "combine_independent_and_speed_conservation", h -> combine(h, false));
        add(tests, "combine_overload_and_recovery", h -> combine(h, true));
        add(tests, "split_ratio_and_independent_speeds", h -> split(h, false));
        add(tests, "split_zero_and_reverse", h -> split(h, true));
        add(tests, "remove_router_restores_shafts_and_stress", StressRouterTests::remove);
        add(tests, "reject_input_output_loop", StressRouterTests::feedback);
        add(tests, "save_settings_and_large_menu_values", StressRouterTests::save);
        add(tests, "shared_input_network_adds_both_loads", h -> shared(h, false));
        add(tests, "shared_output_network_adds_both_capacities", h -> shared(h, true));
        add(tests, "output_overload_does_not_stop_input", StressRouterTests::outputOverload);
        add(tests, "overload_validation_retains_source_and_recovers", StressRouterTests::overloadValidation);
        add(tests, "input_speed_change_and_source_removal", StressRouterTests::inputChanges);
        add(tests, "direct_motor_input_and_cog_output", StressRouterTests::directMotor);
        add(tests, "stressometer_input_and_misaligned_shaft", StressRouterTests::meterInput);
        add(tests, "redstone_releases_stress_and_recovers", StressRouterTests::redstone);
        add(tests, "legacy_ports_migrate_without_replacing_neighbours", StressRouterTests::legacyPorts);
        add(tests, "initial_menu_snapshot_has_configuration", StressRouterTests::menuSnapshot);
        add(tests, "port_state_survives_unload_and_reload", StressRouterTests::reload);
        add(tests, "native_router_recipes_match_pattern", StressRouterTests::recipes);
        add(tests, "creative_tab_contains_all_registered_items", StressRouterTests::creativeTab);
        return tests;
    }
    private static void add(Collection<TestFunction> tests, String name, java.util.function.Consumer<GameTestHelper> run) {
        tests.add(new TestFunction(StressRouterContent.MOD_ID + ":routers", StressRouterContent.MOD_ID + ":" + name,
                TEMPLATE, 160, 0, true, run));
    }
    private static StressRouterBlockEntity setup(GameTestHelper h, boolean splitter) {
        h.setBlock(CENTER, (splitter ? StressRouterContent.SPLITTER : StressRouterContent.COMBINER).get()
                .defaultBlockState().setValue(StressRouterBlock.FACING, Direction.NORTH));
        shaft(h, CENTER.north(), Direction.Axis.Z);
        shaft(h, CENTER.east(), Direction.Axis.X);
        shaft(h, CENTER.west(), Direction.Axis.X);
        return (StressRouterBlockEntity) h.getBlockEntity(CENTER);
    }
    private static void shaft(GameTestHelper h, BlockPos pos, Direction.Axis axis) {
        h.setBlock(pos, AllBlocks.SHAFT.getDefaultState().setValue(BlockStateProperties.AXIS, axis));
    }
    private static void motor(GameTestHelper h, BlockPos pos, Direction facing) {
        h.setBlock(pos, AllBlocks.CREATIVE_MOTOR.getDefaultState().setValue(BlockStateProperties.FACING, facing));
    }
    private static void near(GameTestHelper h, float actual, float expected, String description) {
        h.assertTrue(Math.abs(actual - expected) < .1f, description + ": expected " + expected + ", actual " + actual);
    }
    private static StressPortBlockEntity port(GameTestHelper h, BlockPos pos) {
        var router = (StressRouterBlockEntity) h.getBlockEntity(CENTER);
        return router.portTowards(h.absolutePos(pos));
    }
    private static void combine(GameTestHelper h, boolean overload) {
        var router = setup(h, false);
        motor(h, CENTER.east(2), Direction.WEST);
        motor(h, CENTER.west(2), Direction.EAST);
        router.configure(overload ? StressRouterBlockEntity.MAX_STRESS : 4096, 2048, 50, 64, 16);
        h.runAtTickTime(30, () -> {
            var left = port(h, CENTER.east()); var right = port(h, CENTER.west()); var out = port(h, CENTER.north());
            near(h, left.getOrCreateNetwork().calculateStress(), overload ? StressRouterBlockEntity.MAX_STRESS : 4096, "Left load");
            near(h, right.getOrCreateNetwork().calculateStress(), 2048, "Right load");
            near(h, out.getOrCreateNetwork().calculateCapacity(), overload ? 2048 : 6144, "Output SU");
            near(h, Math.abs(out.getSpeed()), 64, "Output RPM");
            h.assertTrue(!left.network.equals(right.network) && !left.network.equals(out.network), "Networks isolated");
            if (overload) h.assertTrue(left.isOverStressed(), "Input should remain overloaded");
            router.configure(4096, 2048, 50, 128, 16);
        });
        h.runAtTickTime(60, () -> {
            var out = port(h, CENTER.north());
            near(h, out.getOrCreateNetwork().calculateCapacity(), 6144, "SU after RPM change/recovery");
            near(h, Math.abs(out.getSpeed()), 128, "New RPM");
            h.assertTrue(!port(h, CENTER.east()).isOverStressed(), "Input recovered");
            h.succeed();
        });
    }
    private static void split(GameTestHelper h, boolean extremes) {
        var router = setup(h, true);
        motor(h, CENTER.north(2), Direction.SOUTH);
        router.configure(8000, 0, extremes ? 100 : 25, extremes ? -32 : 32, 128);
        h.runAtTickTime(30, () -> {
            var left = port(h, CENTER.east()); var right = port(h, CENTER.west());
            near(h, port(h, CENTER.north()).getOrCreateNetwork().calculateStress(), 8000, "Front load");
            near(h, left.getOrCreateNetwork().calculateCapacity(), extremes ? 8000 : 2000, "Left SU");
            near(h, left.getSpeed(), extremes ? -32 : 32, "Left RPM");
            if (extremes) {
                near(h, right.getGeneratedSpeed(), 0, "Zero share has no generator");
                router.configure(8000, 0, 50, 32, 128);
                h.setBlock(CENTER.above(), Blocks.REDSTONE_BLOCK);
            } else {
                near(h, right.getOrCreateNetwork().calculateCapacity(), 6000, "Right SU");
                near(h, Math.abs(right.getSpeed()), 128, "Right RPM");
                h.assertTrue(!left.network.equals(right.network), "Independent outputs");
            }
        });
        h.runAtTickTime(60, () -> {
            if (extremes) {
                near(h, port(h, CENTER.east()).getSpeed(), 0, "Redstone stops left output");
                near(h, port(h, CENTER.west()).getSpeed(), 0, "Redstone stops right output");
                near(h, port(h, CENTER.north()).getOrCreateNetwork().calculateStress(), 0, "Redstone releases input stress");
            }
            h.succeed();
        });
    }
    private static void remove(GameTestHelper h) {
        var router = setup(h, true);
        motor(h, CENTER.north(2), Direction.SOUTH);
        router.configure(4096, 0, 50, 16, 16);
        h.runAtTickTime(30, () -> h.setBlock(CENTER, Blocks.AIR));
        h.runAtTickTime(50, () -> {
            h.assertBlockPresent(AllBlocks.SHAFT.get(), CENTER.north());
            h.assertBlockPresent(AllBlocks.SHAFT.get(), CENTER.east());
            h.assertBlockPresent(AllBlocks.SHAFT.get(), CENTER.west());
            var input = (KineticBlockEntity) h.getBlockEntity(CENTER.north());
            near(h, input.getOrCreateNetwork().calculateStress(), 0, "Load removed");
            near(h, ((KineticBlockEntity) h.getBlockEntity(CENTER.east())).getSpeed(), 0, "No residual generation");
            h.succeed();
        });
    }
    private static void gearbox(GameTestHelper h, BlockPos pos) {
        h.setBlock(pos, AllBlocks.GEARBOX.getDefaultState().setValue(BlockStateProperties.AXIS, Direction.Axis.Y));
    }
    private static void feedback(GameTestHelper h) {
        var router = setup(h, true);
        motor(h, CENTER.north(3), Direction.SOUTH);
        gearbox(h, CENTER.north(2));
        shaft(h, CENTER.north(2).east(), Direction.Axis.X);
        gearbox(h, CENTER.north(2).east(2));
        shaft(h, CENTER.north().east(2), Direction.Axis.Z);
        gearbox(h, CENTER.east(2));
        router.configure(4096, 0, 50, 16, 16);
        h.runAtTickTime(40, () -> {
            h.assertTrue(router.status == 2, "Feedback must be rejected, status=" + router.status);
            near(h, port(h, CENTER.east()).supplied(), 0, "Loop output disabled");
            near(h, port(h, CENTER.west()).supplied(), 0, "All export disabled on feedback");
            h.succeed();
        });
    }
    private static void save(GameTestHelper h) {
        var router = setup(h, true);
        router.configure(12345678, 7654321, 73, -256, 129);
        var saved = router.saveWithoutMetadata(h.getLevel().registryAccess());
        var copy = new StressRouterBlockEntity(router.getBlockPos(), router.getBlockState());
        copy.loadWithComponents(saved, h.getLevel().registryAccess());
        h.assertTrue(copy.inputA == 12345678 && copy.inputB == 7654321 && copy.ratio == 73 && copy.speedA == -256 && copy.speedB == 129, "Configuration round trip");
        var menu = new StressRouterMenu(1, h.makeMockPlayer(net.minecraft.world.level.GameType.SURVIVAL).getInventory(), router);
        h.assertTrue(menu.value(0) == 12345678 && menu.value(3) == -256, "Container 32-bit transport");
        router.configure(-5, Integer.MAX_VALUE, 500, -9999, 9999);
        h.assertTrue(router.inputA == 0 && router.inputB == StressRouterBlockEntity.MAX_STRESS && router.ratio == 100 && router.speedA == -256 && router.speedB == 256, "Server clamps config");
        h.succeed();
    }

    private static void shared(GameTestHelper h, boolean splitter) {
        var router = setup(h, splitter);
        gearbox(h, CENTER.east(2));
        gearbox(h, CENTER.west(2));
        shaft(h, CENTER.east(2).south(), Direction.Axis.Z);
        shaft(h, CENTER.west(2).south(), Direction.Axis.Z);
        gearbox(h, CENTER.east(2).south(2));
        gearbox(h, CENTER.west(2).south(2));
        shaft(h, CENTER.east().south(2), Direction.Axis.X);
        shaft(h, CENTER.south(2), Direction.Axis.X);
        shaft(h, CENTER.west().south(2), Direction.Axis.X);
        motor(h, splitter ? CENTER.north(2) : CENTER.east(3), splitter ? Direction.SOUTH : Direction.WEST);
        router.configure(6000, 2000, 25, 32, -64);
        h.runAtTickTime(40, () -> {
            var left = port(h, CENTER.east()); var right = port(h, CENTER.west());
            h.assertTrue(left.hasNetwork() && left.network.equals(right.network), "Shared network stays connected");
            if (splitter) {
                near(h, left.getOrCreateNetwork().calculateCapacity(), 6000, "Shares sum in one network");
                near(h, Math.abs(left.getSpeed()), 64, "Faster output takes over");
            } else {
                near(h, left.getOrCreateNetwork().calculateStress(), 8000, "Both input loads count");
                near(h, port(h, CENTER.north()).getOrCreateNetwork().calculateCapacity(), 8000, "Combined SU");
            }
            h.succeed();
        });
    }
    private static void outputOverload(GameTestHelper h) {
        var router = setup(h, false);
        motor(h, CENTER.east(2), Direction.WEST);
        h.setBlock(CENTER.north(2), AllBlocks.ENCASED_FAN.getDefaultState().setValue(BlockStateProperties.FACING, Direction.NORTH));
        router.configure(1, 0, 50, 64, 16);
        h.runAtTickTime(30, () -> {
            h.assertTrue(port(h, CENTER.north()).isOverStressed(), "Output overloads");
            h.assertTrue(!port(h, CENTER.east()).isOverStressed(), "Input unaffected");
            near(h, Math.abs(port(h, CENTER.east()).getSpeed()), 16, "Input still rotates");
            router.configure(4096, 0, 50, 64, 16);
        });
        h.runAtTickTime(60, () -> {
            h.assertTrue(!port(h, CENTER.north()).isOverStressed(), "Output recovers");
            h.succeed();
        });
    }
    // Validate the actual consumer while overloaded, then recover by removing its load.
    private static void overloadValidation(GameTestHelper h) {
        var router = setup(h, false);
        motor(h, CENTER.east(2), Direction.WEST);
        h.setBlock(CENTER.north(2), AllBlocks.ENCASED_FAN.getDefaultState().setValue(BlockStateProperties.FACING, Direction.NORTH));
        router.configure(1, 0, 50, 64, 16);
        h.runAtTickTime(30, () -> {
            var shaft = (KineticBlockEntity) h.getBlockEntity(CENTER.north());
            h.assertTrue(shaft.isOverStressed() && shaft.hasSource(), "Overloaded consumer retains source before validation");
            Long network = shaft.network;
            try {
                var validate = KineticBlockEntity.class.getDeclaredMethod("validateKinetics");
                validate.setAccessible(true);
                validate.invoke(shaft);
            } catch (ReflectiveOperationException e) {
                throw new AssertionError("Cannot run Create kinetic validation", e);
            }
            h.assertTrue(shaft.hasSource() && network.equals(shaft.network), "Validation must not detach an overloaded consumer");
            h.setBlock(CENTER.north(2), Blocks.AIR);
        });
        h.runAtTickTime(60, () -> {
            var shaft = (KineticBlockEntity) h.getBlockEntity(CENTER.north());
            near(h, Math.abs(shaft.getSpeed()), 64, "Consumer recovers after load removal without reconfiguration");
            h.succeed();
        });
    }
    private static void inputChanges(GameTestHelper h) {
        var router = setup(h, false);
        motor(h, CENTER.east(2), Direction.WEST);
        router.configure(4096, 0, 50, 64, 16);
        h.runAtTickTime(30, () -> {
            var motor = (CreativeMotorBlockEntity) h.getBlockEntity(CENTER.east(2));
            motor.generatedSpeed.value = 128;
            motor.updateGeneratedRotation();
        });
        h.runAtTickTime(60, () -> {
            near(h, port(h, CENTER.east()).getOrCreateNetwork().calculateStress(), 4096, "Constant SU at different input speed");
            near(h, port(h, CENTER.north()).getOrCreateNetwork().calculateCapacity(), 4096, "Constant export");
            h.setBlock(CENTER.east(2), Blocks.AIR);
        });
        h.runAtTickTime(90, () -> {
            near(h, port(h, CENTER.north()).getSpeed(), 0, "Source removal stops output");
            near(h, port(h, CENTER.north()).supplied(), 0, "No capacity after source removed");
            h.succeed();
        });
    }
    private static void directMotor(GameTestHelper h) {
        var router = setup(h, false);
        motor(h, CENTER.east(), Direction.WEST);
        h.setBlock(CENTER.north(), AllBlocks.COGWHEEL.getDefaultState().setValue(BlockStateProperties.AXIS, Direction.Axis.Z));
        router.configure(4096, 0, 50, 48, 16);
        h.runAtTickTime(40, () -> {
            h.assertBlockPresent(AllBlocks.CREATIVE_MOTOR.get(), CENTER.east());
            h.assertBlockPresent(AllBlocks.COGWHEEL.get(), CENTER.north());
            near(h, port(h, CENTER.east()).getOrCreateNetwork().calculateStress(), 4096, "Direct motor load");
            near(h, ((KineticBlockEntity) h.getBlockEntity(CENTER.north())).getSpeed(), -48, "Direct cog output");
            h.succeed();
        });
    }
    private static void meterInput(GameTestHelper h) {
        var router = setup(h, false);
        h.setBlock(CENTER.east(), AllBlocks.STRESSOMETER.getDefaultState().setValue(BlockStateProperties.FACING, Direction.NORTH)
                .setValue(com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE, true));
        motor(h, CENTER.east(2), Direction.WEST);
        router.configure(4096, 0, 50, 32, 16);
        h.runAtTickTime(30, () -> {
            near(h, port(h, CENTER.north()).getOrCreateNetwork().calculateCapacity(), 4096, "Stressometer connects");
            h.assertBlockPresent(AllBlocks.STRESSOMETER.get(), CENTER.east());
            h.setBlock(CENTER.east(), AllBlocks.SHAFT.getDefaultState().setValue(BlockStateProperties.AXIS, Direction.Axis.Z));
        });
        h.runAtTickTime(60, () -> {
            near(h, port(h, CENTER.north()).supplied(), 0, "Misaligned shaft cannot supply stress");
            h.succeed();
        });
    }
    private static void redstone(GameTestHelper h) {
        var router = setup(h, false);
        motor(h, CENTER.east(), Direction.WEST);
        router.configure(4096, 0, 50, 32, 16);
        h.runAtTickTime(25, () -> h.setBlock(CENTER.above(), Blocks.REDSTONE_BLOCK));
        h.runAtTickTime(40, () -> {
            near(h, port(h, CENTER.north()).supplied(), 0, "Redstone export is zero");
            near(h, port(h, CENTER.east()).getOrCreateNetwork().calculateStress(), 0, "Redstone consumes no input");
            h.assertTrue(router.status == 3 && router.getBlockState().getValue(BlockStateProperties.POWERED), "Redstone state and texture property");
            h.setBlock(CENTER.above(), Blocks.AIR);
        });
        h.runAtTickTime(65, () -> {
            near(h, port(h, CENTER.north()).supplied(), 4096, "Signal removal restores export");
            h.assertTrue(router.status == 1, "Status recovers"); h.succeed();
        });
    }
    private static void legacyPorts(GameTestHelper h) {
        var router = setup(h, false);
        h.setBlock(CENTER.east(), StressRouterContent.PORT.get().defaultBlockState().setValue(BlockStateProperties.AXIS, Direction.Axis.X));
        ((StressPortBlockEntity) h.getBlockEntity(CENTER.east())).owner = h.absolutePos(CENTER);
        motor(h, CENTER.east(2), Direction.WEST);
        router.configure(2048, 0, 50, 16, 16);
        h.runAtTickTime(40, () -> {
            h.assertBlockPresent(AllBlocks.SHAFT.get(), CENTER.east());
            near(h, port(h, CENTER.north()).supplied(), 2048, "Legacy migration retains working connection"); h.succeed();
        });
    }
    private static void menuSnapshot(GameTestHelper h) {
        var router = setup(h, true);
        router.configure(12345678, 0, 73, -256, 129);
        var buf = new net.minecraft.network.RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(), h.getLevel().registryAccess());
        try {
            router.writeMenuData(buf);
            var menu = new StressRouterMenu(7, h.makeMockPlayer(net.minecraft.world.level.GameType.SURVIVAL).getInventory(), buf);
            h.assertTrue(menu.value(0) == 12345678 && menu.value(2) == 73 && menu.value(3) == -256 && menu.value(4) == 129 && menu.value(5) == 1, "Correct state before first screen frame");
        } finally { buf.release(); }
        h.succeed();
    }
    private static void reload(GameTestHelper h) {
        var router = setup(h, true);
        motor(h, CENTER.north(), Direction.SOUTH);
        router.configure(8192, 0, 25, 64, -128);
        h.runAtTickTime(30, () -> {
            var saved = router.saveWithoutMetadata(h.getLevel().registryAccess());
            router.onChunkUnloaded();
            h.getLevel().removeBlockEntity(router.getBlockPos());
            var loaded = new StressRouterBlockEntity(router.getBlockPos(), router.getBlockState());
            loaded.loadWithComponents(saved, h.getLevel().registryAccess());
            h.getLevel().setBlockEntity(loaded);
        });
        h.runAtTickTime(65, () -> {
            near(h, port(h, CENTER.east()).getOrCreateNetwork().calculateCapacity(), 2048, "Reloaded left capacity");
            near(h, port(h, CENTER.west()).getOrCreateNetwork().calculateCapacity(), 6144, "Reloaded right capacity");
            near(h, port(h, CENTER.north()).getOrCreateNetwork().calculateStress(), 8192, "Reloaded input load");
            h.succeed();
        });
    }
    private static void creativeTab(GameTestHelper h) {
        var tab = com.tajlpawa.ageofexpansionmodpackfix.AgeofExpansionModpackFix.MOD_TAB.get();
        tab.buildContents(new net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters(
                h.getLevel().enabledFeatures(), true, h.getLevel().registryAccess()));
        var entries = tab.getDisplayItems().stream().map(net.minecraft.world.item.ItemStack::getItem).collect(java.util.stream.Collectors.toSet());
        var registry = net.minecraft.core.registries.BuiltInRegistries.ITEM;
        registry.entrySet().stream().filter(e -> e.getKey().location().getNamespace().equals(StressRouterContent.MOD_ID))
                .forEach(e -> h.assertTrue(entries.contains(e.getValue()), "Missing creative item: " + e.getKey().location()));
        h.assertTrue(entries.size() == 6, "Five functional items and the emblem");
        h.assertTrue(!registry.containsKey(ResourceLocation.fromNamespaceAndPath(StressRouterContent.MOD_ID, "example_item"))
                && !registry.containsKey(ResourceLocation.fromNamespaceAndPath(StressRouterContent.MOD_ID, "example_block")), "Example items removed");
        h.assertTrue(tab.getIconItem().is(com.tajlpawa.ageofexpansionmodpackfix.AgeofExpansionModpackFix.TAB_ICON.get()), "Custom icon registered");
        h.succeed();
    }
    private static void recipes(GameTestHelper h) {
        for (boolean split : new boolean[]{false, true}) {
            var id = ResourceLocation.fromNamespaceAndPath(StressRouterContent.MOD_ID, "stress_network_" + (split ? "splitter" : "combiner"));
            var recipe = (net.minecraft.world.item.crafting.CraftingRecipe) h.getLevel().getRecipeManager().byKey(id).orElseThrow().value();
            var input = net.minecraft.world.item.crafting.CraftingInput.of(3, 3, java.util.List.of(
                    AllBlocks.BRASS_CASING.asStack(), AllBlocks.CLUTCH.asStack(), AllBlocks.BRASS_CASING.asStack(),
                    AllBlocks.SHAFT.asStack(), (split ? AllBlocks.ROTATION_SPEED_CONTROLLER.asStack() : AllBlocks.SEQUENCED_GEARSHIFT.asStack()), AllBlocks.SHAFT.asStack(),
                    AllBlocks.BRASS_CASING.asStack(), AllBlocks.SHAFT.asStack(), AllBlocks.BRASS_CASING.asStack()));
            h.assertTrue(recipe.matches(input, h.getLevel()), "Native pattern matches " + id);
            var result = recipe.assemble(input, h.getLevel().registryAccess());
            h.assertTrue(result.is((split ? StressRouterContent.SPLITTER : StressRouterContent.COMBINER).get().asItem()) && result.getCount() == 1, "Native result " + id);
        }
        h.succeed();
    }
}
