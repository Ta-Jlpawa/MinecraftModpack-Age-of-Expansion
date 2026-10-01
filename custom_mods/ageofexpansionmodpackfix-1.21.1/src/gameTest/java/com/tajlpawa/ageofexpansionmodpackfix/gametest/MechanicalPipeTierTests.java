package com.tajlpawa.ageofexpansionmodpackfix.gametest;

import java.util.List;
import mekanism.common.tier.PipeTier;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.neoforged.neoforge.gametest.GameTestHolder;

@GameTestHolder("ageofexpansionmodpackfix")
public final class MechanicalPipeTierTests {
    @GameTestGenerator
    public static List<TestFunction> tests() {
        if (!Boolean.getBoolean("ageofexpansion.pipeIntegration")) return List.of();
        return List.of(new TestFunction("ageofexpansionmodpackfix:mechanical_pipe", "ageofexpansionmodpackfix:doubled_pipe_tiers",
                "ageofexpansionmodpackfix:router_empty", 40, 0, true, MechanicalPipeTierTests::tiers));
    }

    private static void tiers(GameTestHelper helper) {
        check(helper, PipeTier.BASIC, 4000, 500);
        check(helper, PipeTier.ADVANCED, 16000, 2000);
        check(helper, PipeTier.ELITE, 64000, 16000);
        check(helper, PipeTier.ULTIMATE, 256000, 64000);
        helper.succeed();
    }

    private static void check(GameTestHelper helper, PipeTier tier, int capacity, int pull) {
        helper.assertTrue(tier.getPipeCapacity() == capacity && tier.getPipePullAmount() == pull,
                tier.name() + " capacity and pump rate doubled");
    }
}
