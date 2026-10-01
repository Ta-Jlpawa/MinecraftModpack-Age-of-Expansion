package com.tajlpawa.ageofexpansionmodpackfix.mixin.champions;

import com.tajlpawa.ageofexpansionmodpackfix.champions.ChampionSpawnRules;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// 在 Champions 计算等级与选择词缀前筛除非自然生成或非原版普通怪物。
@Pseudo
@Mixin(targets = "top.theillusivec4.champions.common.champion.ChampionSpawnHandler", remap = false)
public abstract class ChampionSpawnHandlerMixin {
    @Inject(method = "trySpawn", at = @At("HEAD"), cancellable = true, remap = false)
    private static void ageofexpansion$naturalVanillaOnly(LivingEntity entity, ServerLevel level, CallbackInfo ci) {
        if (!ChampionSpawnRules.consumeEligibleSpawn(entity)) ci.cancel();
    }
}
