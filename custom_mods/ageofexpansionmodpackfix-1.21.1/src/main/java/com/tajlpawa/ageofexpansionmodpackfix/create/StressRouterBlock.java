package com.tajlpawa.ageofexpansionmodpackfix.create;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;

public class StressRouterBlock extends Block implements IBE<StressRouterBlockEntity>, com.simibubi.create.content.kinetics.base.IRotate {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public final boolean splitter;

    public StressRouterBlock(boolean splitter, Properties properties) {
        super(properties);
        this.splitter = splitter;
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(BlockStateProperties.POWERED, false));
    }

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(FACING, BlockStateProperties.POWERED); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(BlockStateProperties.POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos())); }
    @Override public Direction.Axis getRotationAxis(BlockState state) { return state.getValue(FACING).getAxis(); }
    @Override public boolean hasShaftTowards(net.minecraft.world.level.LevelReader level, BlockPos pos, BlockState state, Direction face) { return face.getAxis().isHorizontal() && face != state.getValue(FACING).getOpposite(); }
    @Override protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos sourcePos, boolean moving) {
        if (!level.isClientSide) withBlockEntityDo(level, pos, StressRouterBlockEntity::updateRedstone);
    }
    @Override public BlockState rotate(BlockState state, Rotation rotation) { return state.setValue(FACING, rotation.rotate(state.getValue(FACING))); }
    @Override public BlockState mirror(BlockState state, Mirror mirror) { return rotate(state, mirror.getRotation(state.getValue(FACING))); }
    @Override public Class<StressRouterBlockEntity> getBlockEntityClass() { return StressRouterBlockEntity.class; }
    @Override public BlockEntityType<StressRouterBlockEntity> getBlockEntityType() { return StressRouterContent.ROUTER_ENTITY.get(); }

    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (player instanceof ServerPlayer serverPlayer && level.getBlockEntity(pos) instanceof StressRouterBlockEntity router)
            serverPlayer.openMenu(router, router::writeMenuData);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        if (!context.getLevel().isClientSide) {
            withBlockEntityDo(context.getLevel(), context.getClickedPos(), StressRouterBlockEntity::releasePorts);
            context.getLevel().setBlock(context.getClickedPos(), rotate(state, Rotation.CLOCKWISE_90), UPDATE_ALL);
        }
        return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
    }

    @Override public void onRemove(BlockState state, Level level, BlockPos pos, BlockState next, boolean moving) {
        if (state.getBlock() != next.getBlock() && !level.isClientSide)
            withBlockEntityDo(level, pos, StressRouterBlockEntity::releasePorts);
        IBE.onRemove(state, level, pos, next);
    }
}
