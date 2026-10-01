package com.tajlpawa.ageofexpansionmodpackfix.gametest;
import java.util.Collection;
import java.util.List;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.TestFunction;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.gametest.GameTestHolder;
@GameTestHolder("ageofexpansionmodpackfix")
public final class DrillTestRegistration {
    @GameTestGenerator public static Collection<TestFunction> tests() {
        return ModList.get().isLoaded("createoreexcavation") && ModList.get().isLoaded("immersivepetroleum")
                ? com.tajlpawa.ageofexpansionmodpackfix.oreexcavation.DrillDurabilityTests.tests() : List.of();
    }
}
