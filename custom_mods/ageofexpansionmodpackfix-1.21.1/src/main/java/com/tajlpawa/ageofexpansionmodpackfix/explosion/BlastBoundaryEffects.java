package com.tajlpawa.ageofexpansionmodpackfix.explosion;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

// 球形爆炸外缘按 Oritech 的焦土风格随机改变仍然存在的方块。
final class BlastBoundaryEffects {
    private BlastBoundaryEffects() {}

    static int apply(ServerLevel level, BlockPos pos, BlockState state) {
        if (state.isAir() || state.is(Blocks.BEDROCK) || state.is(Blocks.FIRE)) return 0;
        BlockState replacement = pick(state, level.random.nextFloat(), level.random.nextInt(4));
        int changed = replacement != state && level.setBlock(pos, replacement, 3) ? 1 : 0;
        BlockPos above = pos.above();
        if (level.random.nextFloat() < 0.03f && above.getY() < level.getMaxBuildHeight() && level.hasChunkAt(above)
                && level.getBlockState(above).canBeReplaced() && Blocks.FIRE.defaultBlockState().canSurvive(level, above)
                && level.setBlock(above, Blocks.FIRE.defaultBlockState(), 3)) {
            changed++;
        }
        return changed;
    }

    static BlockState pick(BlockState state, float roll, int stoneVariant) {
        if (state.is(Blocks.BEDROCK) || state.is(Blocks.FIRE) || state.isAir()) return state;
        if (roll < 0.03f) return Blocks.MAGMA_BLOCK.defaultBlockState();
        if (state.is(Blocks.GRASS_BLOCK)) {
            if (roll < 0.38f) return Blocks.DIRT.defaultBlockState();
            if (roll < 0.58f) return Blocks.COARSE_DIRT.defaultBlockState();
        } else if (state.is(Blocks.DIRT) && roll < 0.63f) {
            return Blocks.COARSE_DIRT.defaultBlockState();
        } else if (state.is(Blocks.STONE) && roll < 0.63f) {
            return switch (stoneVariant & 3) {
                case 0 -> Blocks.COBBLESTONE.defaultBlockState();
                case 1 -> Blocks.DEEPSLATE.defaultBlockState();
                case 2 -> Blocks.TUFF.defaultBlockState();
                default -> Blocks.BASALT.defaultBlockState();
            };
        }
        return state;
    }
}
