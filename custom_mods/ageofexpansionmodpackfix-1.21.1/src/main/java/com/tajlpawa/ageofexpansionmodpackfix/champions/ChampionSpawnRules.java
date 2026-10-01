package com.tajlpawa.ageofexpansionmodpackfix.champions;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import top.theillusivec4.champions.api.ChampionsApi;
import top.theillusivec4.champions.common.api.ChampionsRegistries;

public final class ChampionSpawnRules {
    private static final TagKey<EntityType<?>> CHAMPION_ALLOWED = TagKey.create(Registries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath("champions", "allow_champions"));
    private static final Set<Mob> NATURAL_SPAWNS = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap<>()));

    private ChampionSpawnRules() {
    }

    // 在实体加入世界前记录其生成原因，只标记可成为精英的自然生成原版普通怪物。
    public static void onFinalizeSpawn(FinalizeSpawnEvent event) {
        Mob mob = event.getEntity();
        if (event.getSpawnType() == MobSpawnType.NATURAL && isOrdinaryVanillaMonster(mob)) {
            NATURAL_SPAWNS.add(mob);
        }
    }

    // Champions 的加入世界入口只消费一次自然生成标记，其他实体不进入其抽等级逻辑。
    public static boolean consumeEligibleSpawn(LivingEntity entity) {
        return entity instanceof Mob mob && NATURAL_SPAWNS.remove(mob) && isOrdinaryVanillaMonster(mob);
    }

    public static boolean isOrdinaryVanillaMonster(Mob mob) {
        EntityType<?> type = mob.getType();
        if (type.getCategory() != MobCategory.MONSTER
                || !type.is(CHAMPION_ALLOWED)
                || !"minecraft".equals(BuiltInRegistries.ENTITY_TYPE.getKey(type).getNamespace())) {
            return false;
        }
        if (type == EntityType.ENDER_DRAGON
                || type == EntityType.WITHER
                || type == EntityType.WARDEN
                || type == EntityType.ELDER_GUARDIAN) {
            return false;
        }
        var archetypes = ChampionsRegistries.archetypes().getAll();
        var tiers = ChampionsApi.get().getTiers();
        if (archetypes.isEmpty() || tiers.isEmpty()) return false;
        for (var tier : tiers) {
            boolean supported = false;
            for (var archetype : archetypes) {
                if (archetype.matches(mob, tier.level())) {
                    supported = true;
                    break;
                }
            }
            if (!supported) return false;
        }
        return true;
    }
}
