package com.tajlpawa.ageofexpansionmodpackfix.explosion;

import java.util.List;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;

@GameTestHolder("ageofexpansionmodpackfix")
public final class BlastBoundaryTests {
    @GameTestGenerator
    public static List<TestFunction> tests() {
        return List.of(new TestFunction("ageofexpansionmodpackfix:spherical_blast", "ageofexpansionmodpackfix:boundary_materials",
                "ageofexpansionmodpackfix:router_empty", 40, 0, true, BlastBoundaryTests::materials));
    }

    private static void materials(GameTestHelper helper) {
        helper.assertTrue(BlastBoundaryEffects.pick(Blocks.GRASS_BLOCK.defaultBlockState(), 0.2f, 0).is(Blocks.DIRT), "Grass can become dirt");
        helper.assertTrue(BlastBoundaryEffects.pick(Blocks.GRASS_BLOCK.defaultBlockState(), 0.5f, 0).is(Blocks.COARSE_DIRT), "Grass can become coarse dirt");
        helper.assertTrue(BlastBoundaryEffects.pick(Blocks.DIRT.defaultBlockState(), 0.4f, 0).is(Blocks.COARSE_DIRT), "Dirt can become coarse dirt");
        var stone = Blocks.STONE.defaultBlockState();
        helper.assertTrue(BlastBoundaryEffects.pick(stone, 0.4f, 0).is(Blocks.COBBLESTONE), "Stone can become cobblestone");
        helper.assertTrue(BlastBoundaryEffects.pick(stone, 0.4f, 1).is(Blocks.DEEPSLATE), "Stone can become deepslate");
        helper.assertTrue(BlastBoundaryEffects.pick(stone, 0.4f, 2).is(Blocks.TUFF), "Stone can become tuff");
        helper.assertTrue(BlastBoundaryEffects.pick(stone, 0.4f, 3).is(Blocks.BASALT), "Stone can become basalt");
        helper.assertTrue(BlastBoundaryEffects.pick(Blocks.OBSIDIAN.defaultBlockState(), 0.01f, 0).is(Blocks.MAGMA_BLOCK), "Any solid material can become magma");
        helper.assertTrue(BlastBoundaryEffects.pick(Blocks.BEDROCK.defaultBlockState(), 0.01f, 0).is(Blocks.BEDROCK), "Bedrock stays intact");
        helper.assertTrue(BlastBoundaryEffects.pick(Blocks.FIRE.defaultBlockState(), 0.01f, 0).is(Blocks.FIRE), "Generated fire remains fire");
        helper.succeed();
    }
}
