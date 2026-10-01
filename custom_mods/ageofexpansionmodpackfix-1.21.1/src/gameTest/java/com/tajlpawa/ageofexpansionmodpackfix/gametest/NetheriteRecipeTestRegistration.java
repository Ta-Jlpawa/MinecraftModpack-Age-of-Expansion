package com.tajlpawa.ageofexpansionmodpackfix.gametest;

import java.util.Collection;
import java.util.List;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.TestFunction;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.gametest.GameTestHolder;

@GameTestHolder("ageofexpansionmodpackfix")
public final class NetheriteRecipeTestRegistration {
    @GameTestGenerator
    public static Collection<TestFunction> tests() {
        return ModList.get().isLoaded("kubejs") && ModList.get().isLoaded("immersiveengineering")
                && ModList.get().isLoaded("createoreexcavation") && ModList.get().isLoaded("oritech")
                ? NetheriteRecipeTests.tests() : List.of();
    }
}
