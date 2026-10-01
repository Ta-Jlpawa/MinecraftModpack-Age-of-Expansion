package com.tajlpawa.ageofexpansionmodpackfix.mixin.oritech;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

// 命令或物品直接放置的黑洞方块也在生成时立即清除。
@Pseudo
@Mixin(targets = "rearth.oritech.block.blocks.accelerator.BlackHoleBlock", remap = false)
public abstract class BlackHoleBlockMixin extends Block {
    protected BlackHoleBlockMixin(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide && state.getBlock() == (Object) this) level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
    }
}
