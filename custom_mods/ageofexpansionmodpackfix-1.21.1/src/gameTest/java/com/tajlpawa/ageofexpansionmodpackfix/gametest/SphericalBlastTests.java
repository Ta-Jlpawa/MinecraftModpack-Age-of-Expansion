package com.tajlpawa.ageofexpansionmodpackfix.gametest;

import com.tajlpawa.ageofexpansionmodpackfix.explosion.SphericalBlasts;
import java.util.ArrayList;
import java.util.Collection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.gametest.GameTestHolder;

@GameTestHolder("ageofexpansionmodpackfix")
public final class SphericalBlastTests {
    @GameTestGenerator
    public static Collection<TestFunction> tests() {
        var tests = new ArrayList<TestFunction>();
        add(tests, "spherical_radius_and_bedrock", SphericalBlastTests::sphere);
        add(tests, "blast_radius_limits", SphericalBlastTests::limits);
        if (ModList.get().isLoaded("oritech")) add(tests, "black_hole_cannot_be_placed", SphericalBlastTests::blackHole);
        return tests;
    }

    private static void add(Collection<TestFunction> tests, String name, java.util.function.Consumer<GameTestHelper> run) {
        tests.add(new TestFunction("ageofexpansionmodpackfix:spherical_blast", "ageofexpansionmodpackfix:" + name,
                "ageofexpansionmodpackfix:router_empty", 60, 0, true, run));
    }

    private static void sphere(GameTestHelper helper) {
        BlockPos center = new BlockPos(3, 3, 3);
        BlockPos edge = center.offset(2, 0, 0);
        BlockPos outside = center.offset(3, 1, 0);
        BlockPos boundary = center.offset(2, 1, 0);
        BlockPos bedrock = center.above();
        helper.setBlock(center, Blocks.STONE);
        helper.setBlock(edge, Blocks.STONE);
        helper.setBlock(outside, Blocks.STONE);
        helper.setBlock(boundary, Blocks.STONE);
        helper.setBlock(bedrock, Blocks.BEDROCK);
        SphericalBlasts.start(helper.getLevel(), helper.absolutePos(center), 2, SphericalBlasts.Kind.ORITECH);
        helper.runAtTickTime(10, () -> {
            helper.assertTrue(helper.getBlockState(center).isAir(), "Center destroyed");
            helper.assertTrue(helper.getBlockState(edge).isAir(), "Radius-two edge destroyed");
            helper.assertTrue(helper.getBlockState(outside).is(Blocks.STONE), "Block outside sphere preserved");
            helper.assertTrue(!helper.getBlockState(boundary).isAir(), "Outer boundary remains present");
            helper.assertTrue(helper.getBlockState(bedrock).is(Blocks.BEDROCK), "Bedrock preserved");
            helper.succeed();
        });
    }

    private static void limits(GameTestHelper helper) {
        helper.assertTrue(SphericalBlasts.radiusForAssemblies(1) == 2, "One assembly gives radius two");
        helper.assertTrue(SphericalBlasts.radiusForAssemblies(64) == 128, "Radius reaches 128");
        helper.assertTrue(SphericalBlasts.radiusForAssemblies(1000) == 128, "Radius is capped at 128");
        for (int i = 0; i < 100; i++) {
            int small = SphericalBlasts.randomDiameterInChunks(helper.getLevel(), true);
            int large = SphericalBlasts.randomDiameterInChunks(helper.getLevel(), false);
            helper.assertTrue(small >= 4 && small <= 6 && large >= 7 && large <= 13, "Nuke diameters stay in range");
        }
        helper.succeed();
    }

    private static void blackHole(GameTestHelper helper) {
        Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse("oritech:black_hole_block"));
        helper.assertTrue(block != Blocks.AIR, "Oritech black hole registered");
        BlockPos pos = helper.absolutePos(new BlockPos(3, 3, 3));
        helper.getLevel().setBlock(pos, block.defaultBlockState(), 3);
        helper.assertTrue(helper.getLevel().getBlockState(pos).isAir(), "Direct placement removed immediately");
        helper.succeed();
    }
}
