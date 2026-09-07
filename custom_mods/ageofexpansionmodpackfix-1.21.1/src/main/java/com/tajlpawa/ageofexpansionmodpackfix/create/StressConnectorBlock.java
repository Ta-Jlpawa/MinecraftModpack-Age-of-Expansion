package com.tajlpawa.ageofexpansionmodpackfix.create;

import com.simibubi.create.content.kinetics.base.KineticBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

// Internal state for one directional kinetic node; never replaces a neighbouring block.
public final class StressConnectorBlock extends KineticBlock {
    public StressConnectorBlock(Properties properties) { super(properties); }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(StressRouterBlock.FACING); }
    @Override public Direction.Axis getRotationAxis(BlockState state) { return state.getValue(StressRouterBlock.FACING).getAxis(); }
    @Override public boolean hasShaftTowards(LevelReader level, BlockPos pos, BlockState state, Direction face) { return state.getValue(StressRouterBlock.FACING) == face; }
}
