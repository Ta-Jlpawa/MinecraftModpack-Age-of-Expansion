package com.tajlpawa.ageofexpansionmodpackfix.create;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.AbstractShaftBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

// A connected ordinary shaft becomes a separate Create network endpoint.
public class StressPortBlock extends AbstractShaftBlock {
    public StressPortBlock(Properties properties) { super(properties); }
    @Override public BlockEntityType<? extends KineticBlockEntity> getBlockEntityType() { return StressRouterContent.PORT_ENTITY.get(); }
    @Override public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return AllShapes.SIX_VOXEL_POLE.get(state.getValue(AXIS)); }
    @Override public PushReaction getPistonPushReaction(BlockState state) { return PushReaction.BLOCK; }
    @Override public RenderShape getRenderShape(BlockState state) { return RenderShape.ENTITYBLOCK_ANIMATED; }
    @Override public ItemStack getCloneItemStack(BlockState state, HitResult target, net.minecraft.world.level.LevelReader level, BlockPos pos, net.minecraft.world.entity.player.Player player) { return AllBlocks.SHAFT.asStack(); }
    @Override public InteractionResult onWrenched(BlockState state, UseOnContext context) { return InteractionResult.PASS; }
}
