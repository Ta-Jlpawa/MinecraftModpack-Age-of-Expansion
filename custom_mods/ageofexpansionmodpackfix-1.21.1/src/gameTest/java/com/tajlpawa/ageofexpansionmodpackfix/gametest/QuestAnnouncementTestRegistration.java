package com.tajlpawa.ageofexpansionmodpackfix.gametest;

import java.util.Collection;
import java.util.List;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.TestFunction;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.gametest.GameTestHolder;

@GameTestHolder("ageofexpansionmodpackfix")
public final class QuestAnnouncementTestRegistration {
    @GameTestGenerator public static Collection<TestFunction> tests() {
        return ModList.get().isLoaded("ftbquests")
                ? com.tajlpawa.ageofexpansionmodpackfix.ftbquests.QuestAnnouncementTests.tests() : List.of();
    }
}
