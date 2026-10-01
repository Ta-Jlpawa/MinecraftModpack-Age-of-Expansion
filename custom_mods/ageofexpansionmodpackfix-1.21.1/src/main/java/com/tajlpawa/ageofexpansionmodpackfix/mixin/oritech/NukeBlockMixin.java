package com.tajlpawa.ageofexpansionmodpackfix.mixin.oritech;

import com.tajlpawa.ageofexpansionmodpackfix.explosion.SphericalBlasts;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// 两种核爆装置共用原有引爆入口，改用受每刻预算约束的球形爆炸。
@Pseudo
@Mixin(targets = "rearth.oritech.block.blocks.reactor.NukeBlock", remap = false)
public abstract class NukeBlockMixin {
    @Shadow private boolean small;

    @Inject(method = "primeTnt", at = @At("HEAD"), cancellable = true, remap = false)
    private void ageofexpansion$sphericalNuke(Level level, BlockPos pos, CallbackInfo ci) {
        if (!(level instanceof ServerLevel serverLevel)) {
            ci.cancel();
            return;
        }
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        level.playSound(null, pos, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1, 1);
        int diameter = SphericalBlasts.randomDiameterInChunks(serverLevel, small);
        SphericalBlasts.start(serverLevel, pos, diameter * 8, SphericalBlasts.Kind.ORITECH);
        ci.cancel();
    }
}
