package com.tajlpawa.ageofexpansionmodpackfix.gametest;

import com.tajlpawa.ageofexpansionmodpackfix.champions.ChampionSpawnRules;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import top.theillusivec4.champions.api.ChampionsApi;
import top.theillusivec4.champions.common.config.ChampionsConfig;

@GameTestHolder("ageofexpansionmodpackfix")
public final class ChampionSpawnTests {
    @GameTestGenerator
    public static List<TestFunction> tests() {
        if (!Boolean.getBoolean("ageofexpansion.championIntegration")) return List.of();
        return List.of(new TestFunction("ageofexpansionmodpackfix:champion_spawn",
                "ageofexpansionmodpackfix:only_natural_vanilla_monsters_become_champions",
                "ageofexpansionmodpackfix:router_empty", 40, 0, true, ChampionSpawnTests::spawnEligibility));
    }

    private static void spawnEligibility(GameTestHelper helper) {
        float chance = ChampionsConfig.spawnChance;
        int protection = ChampionsConfig.beaconProtectionRange;
        ChampionsConfig.spawnChance = 1.0F;
        ChampionsConfig.beaconProtectionRange = 0;
        try {
            Mob bat = EntityType.BAT.create(helper.getLevel());
            Mob salmon = EntityType.SALMON.create(helper.getLevel());
            Mob summonedZombie = EntityType.ZOMBIE.create(helper.getLevel());
            Mob spawnerZombie = EntityType.ZOMBIE.create(helper.getLevel());
            Mob naturalZombie = EntityType.ZOMBIE.create(helper.getLevel());
            Mob naturalWarden = EntityType.WARDEN.create(helper.getLevel());
            Mob naturalPiglin = EntityType.PIGLIN.create(helper.getLevel());
            Mob wither = EntityType.WITHER.create(helper.getLevel());
            Mob dragon = EntityType.ENDER_DRAGON.create(helper.getLevel());
            helper.assertTrue(bat != null && salmon != null && summonedZombie != null
                    && spawnerZombie != null && naturalZombie != null && naturalWarden != null && naturalPiglin != null
                    && wither != null && dragon != null, "Test mobs created");
            spawn(helper, bat, new BlockPos(1, 2, 1));
            spawn(helper, salmon, new BlockPos(2, 2, 1));
            spawn(helper, summonedZombie, new BlockPos(3, 2, 1));
            spawnFinalized(helper, spawnerZombie, new BlockPos(4, 2, 1), MobSpawnType.SPAWNER);
            spawnFinalized(helper, naturalZombie, new BlockPos(5, 2, 1), MobSpawnType.NATURAL);
            spawnFinalized(helper, naturalWarden, new BlockPos(6, 2, 1), MobSpawnType.NATURAL);
            spawnFinalized(helper, naturalPiglin, new BlockPos(1, 2, 2), MobSpawnType.NATURAL);
            helper.assertTrue(!ChampionsApi.get().isChampion(bat), "Bat must not receive a champion tier");
            helper.assertTrue(!ChampionsApi.get().isChampion(salmon), "Salmon must not receive a champion tier");
            helper.assertTrue(!ChampionsApi.get().isChampion(summonedZombie), "Summoned zombie must not receive a champion tier");
            helper.assertTrue(!ChampionsApi.get().isChampion(spawnerZombie), "Spawner zombie must not receive a champion tier");
            helper.assertTrue(ChampionsApi.get().isChampion(naturalZombie), "Natural zombie must receive a champion tier");
            helper.assertTrue(!ChampionsApi.get().isChampion(naturalWarden), "Warden must not receive a champion tier");
            helper.assertTrue(!ChampionsApi.get().isChampion(naturalPiglin), "Mob without an archetype must not receive a champion tier");
            helper.assertTrue(!ChampionSpawnRules.isOrdinaryVanillaMonster(wither), "Wither is a boss");
            helper.assertTrue(!ChampionSpawnRules.isOrdinaryVanillaMonster(dragon), "Ender Dragon is a boss");
            helper.succeed();
        } finally {
            ChampionsConfig.spawnChance = chance;
            ChampionsConfig.beaconProtectionRange = protection;
        }
    }

    private static void spawn(GameTestHelper helper, Mob mob, BlockPos relativePos) {
        BlockPos position = helper.absolutePos(relativePos);
        mob.moveTo(position.getX() + 0.5, position.getY(), position.getZ() + 0.5, 0, 0);
        helper.getLevel().addFreshEntity(mob);
    }

    private static void spawnFinalized(GameTestHelper helper, Mob mob, BlockPos relativePos, MobSpawnType type) {
        BlockPos position = helper.absolutePos(relativePos);
        mob.moveTo(position.getX() + 0.5, position.getY(), position.getZ() + 0.5, 0, 0);
        EventHooks.finalizeMobSpawn(mob, helper.getLevel(), helper.getLevel().getCurrentDifficultyAt(position), type, null);
        helper.getLevel().addFreshEntity(mob);
    }
}
