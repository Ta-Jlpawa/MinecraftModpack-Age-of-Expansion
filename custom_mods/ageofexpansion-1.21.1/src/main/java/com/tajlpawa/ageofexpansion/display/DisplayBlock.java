package com.tajlpawa.ageofexpansion.display;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.*;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;

// 仅接受墙面放置，支撑规则与原版墙上告示牌一致。
public final class DisplayBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final MapCodec<DisplayBlock> CODEC = com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(instance->instance.group(
        propertiesCodec(),com.mojang.serialization.Codec.intRange(0,2).optionalFieldOf("kind",0).forGetter(block->block.kind),
        com.mojang.serialization.Codec.BOOL.optionalFieldOf("holographic",false).forGetter(block->block.holographic),
        com.mojang.serialization.Codec.BOOL.optionalFieldOf("image",false).forGetter(block->block.imageScreen)
    ).apply(instance,DisplayBlock::new));
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final IntegerProperty CONNECTIONS = IntegerProperty.create("connections", 0, 15);
    public final int kind;
    public final boolean holographic;
    public final boolean imageScreen;
    public DisplayBlock(Properties properties) {
        this(properties, 0);
    }
    public DisplayBlock(Properties properties, int kind) {
        this(properties,kind,false);
    }
    public DisplayBlock(Properties properties,int kind,boolean holographic) {this(properties,kind,holographic,false);}
    public DisplayBlock(Properties properties,int kind,boolean holographic,boolean imageScreen) {
        super(properties);
        this.kind = kind;
        this.holographic=holographic;this.imageScreen=imageScreen;
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LIT, false).setValue(WATERLOGGED, false).setValue(CONNECTIONS, 0));
    }
    @Override protected MapCodec<? extends BaseEntityBlock> codec() { return CODEC; }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new DisplayBlockEntity(pos, state); }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(FACING, LIT, WATERLOGGED, CONNECTIONS); }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if(kind != 0) return switch(state.getValue(FACING)) {
            case NORTH -> box(0,0,14,16,16,16);
            case SOUTH -> box(0,0,0,16,16,2);
            case EAST -> box(0,0,0,2,16,16);
            default -> box(14,0,0,16,16,16);
        };
        return switch (state.getValue(FACING)) {
            case NORTH -> box(0, 4, 14, 16, 13.3333, 16);
            case SOUTH -> box(0, 4, 0, 16, 13.3333, 2);
            case EAST -> box(0, 4, 0, 2, 13.3333, 16);
            default -> box(14, 4, 0, 16, 13.3333, 16);
        };
    }
    @Override protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.relative(state.getValue(FACING).getOpposite())).isSolid();
    }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction face = context.getClickedFace();
        if (!face.getAxis().isHorizontal()) return null;
        BlockState state = defaultBlockState().setValue(FACING, face)
            .setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
        return state.canSurvive(context.getLevel(), context.getClickedPos()) ? state : null;
    }
    @Override protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }
    @Override protected BlockState updateShape(BlockState state, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        if (direction == state.getValue(FACING).getOpposite() && !state.canSurvive(level, pos)) return Blocks.AIR.defaultBlockState();
        return super.updateShape(state, direction, neighbor, level, pos, neighborPos);
    }
    @Override protected BlockState rotate(BlockState state, Rotation rotation) { return state.setValue(FACING, rotation.rotate(state.getValue(FACING))); }
    @Override protected BlockState mirror(BlockState state, Mirror mirror) { return state.rotate(mirror.getRotation(state.getValue(FACING))); }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (player instanceof ServerPlayer serverPlayer && level.getBlockEntity(pos) instanceof DisplayBlockEntity display) DisplayNetwork.open(serverPlayer, display);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
    @Override public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if(kind == 2 && level instanceof net.minecraft.server.level.ServerLevel server && level.getBlockEntity(pos) instanceof DisplayBlockEntity be) {
            ModuleScreens.get(server).register(server, be); ModuleScreens.get(server).tick(server);
        }
        if (placer instanceof ServerPlayer player && level.getBlockEntity(pos) instanceof DisplayBlockEntity display) DisplayNetwork.open(player, display);
    }
    @Override protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState next, boolean moving) {
        if(kind == 2 && state.getBlock() != next.getBlock() && level instanceof net.minecraft.server.level.ServerLevel server)
            ModuleScreens.get(server).remove(server, pos);
        super.onRemove(state, level, pos, next, moving);
    }
}

