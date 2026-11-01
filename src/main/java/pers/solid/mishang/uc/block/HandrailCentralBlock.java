package pers.solid.mishang.uc.block;

import pers.solid.mishang.uc.shim.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.models.model.*;
import net.minecraft.data.models.blockstates.*;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.blockstates.Condition;
import net.minecraft.data.models.blockstates.MultiPartGenerator;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.solid.mishang.uc.MishangUtils;

import java.util.Map;

/**
 * <p>类似于 {@link HandrailBlock}，但是方块对准的是正中间，而不是方块边缘的位置。</p>
 * <p>{@code HandrailBlock} 有一个抽象方法 {@link HandrailBlock#central() central()}，返回的就是这个示例。</p>
 *
 * @param <T> 其基础栏杆方块的类型。
 */
public abstract class HandrailCentralBlock<T extends HandrailBlock> extends CrossCollisionBlock implements MishangucBlock, Handrails {
  /**
   * 该方块的基础的栏杆方块。
   */
  public final @NotNull T baseHandrail;

  protected HandrailCentralBlock(@NotNull T baseBlock, float radius1, float radius2, float boundingHeight1, float boundingHeight2, float collisionHeight, Properties settings) {
    super(radius1, radius2, boundingHeight1, boundingHeight2, collisionHeight, settings);
    this.registerDefaultState(defaultBlockState()
        .setValue(WEST, true).setValue(EAST, true)
        .setValue(NORTH, false).setValue(SOUTH, false)
        .setValue(WATERLOGGED, false));
    this.baseHandrail = baseBlock;
  }

  @SuppressWarnings("deprecation")
  @Override
  public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
    if (state.getValue(WATERLOGGED)) {
      world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
    }
    BlockState stateForNeighborUpdate = super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    stateForNeighborUpdate = updateSideStates(stateForNeighborUpdate, world, pos);
    return stateForNeighborUpdate;
  }

  public static boolean connectsTo(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
    if (isExceptionForConnection(neighborState)) {
      return false;
    } else if (neighborState.isFaceSturdy(world, neighborPos, direction.getOpposite())) {
      return true;
    } else if (connectsHandrailTo(direction, neighborState)) {
      return true;
    } else {
      return neighborState.is(BlockTags.FENCES) || (neighborState.getBlock() instanceof FenceGateBlock && FenceGateBlock.connectsToDirection(neighborState, direction)) || neighborState.is(BlockTags.WALLS) || neighborState.getBlock() instanceof IronBarsBlock;
    }
  }


  public static boolean connectsHandrailTo(Direction direction, BlockState neighborState) {
    return neighborState.getBlock() instanceof HandrailStairBlock && neighborState.getValue(HandrailStairBlock.POSITION) == HandrailStairBlock.Position.CENTER && neighborState.getValue(HandrailStairBlock.FACING).getAxis() == direction.getAxis() || neighborState.getBlock() instanceof HandrailCentralBlock;
  }

  @SuppressWarnings("deprecation")
  @Override
  public boolean skipRendering(BlockState state, BlockState stateFrom, Direction direction) {
    final Block block = stateFrom.getBlock();
    if (direction.getAxis().isHorizontal() && block instanceof final Handrails handrails) {
      return block.asItem() == asItem()
          && handrails.connectsIn(stateFrom, direction.getOpposite(), null);
    }
    return super.skipRendering(state, stateFrom, direction);
  }

  public HandrailCentralBlock(@NotNull T baseBlock, Properties settings) {
    this(baseBlock, 1f, 1f, 16f, 16f, 16f, settings);
  }

  public HandrailCentralBlock(@NotNull T baseBlock) {
    this(baseBlock, FabricBlockSettings.copyOf(baseBlock));
  }

  @Override
  public Item asItem() {
    return baseHandrail.asItem();
  }


  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder);
    builder.add(WEST, EAST, NORTH, SOUTH, WATERLOGGED);
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext ctx) {
    BlockState placementState = super.getStateForPlacement(ctx);

    if (placementState == null) return null;

    final Direction playerFacing = ctx.getHorizontalDirection();
    final Direction.Axis playerFacingAxis = playerFacing.getAxis();

    // 初始化设置该 placementState 为玩家水平横向的方向。
    for (Map.Entry<Direction, BooleanProperty> entry : PROPERTY_BY_DIRECTION.entrySet()) {
      final Direction direction = entry.getKey();
      final BooleanProperty property = entry.getValue();
      placementState = placementState.setValue(property, direction.getAxis() != playerFacingAxis);
    }
    final Level world = ctx.getLevel();
    final BlockPos blockPos = ctx.getClickedPos();
    final boolean waterlogged = world.getFluidState(blockPos).getType() == Fluids.WATER;
    placementState = updateSideStates(placementState, world, blockPos);
    for (final Direction direction : new Direction[]{Direction.NORTH, Direction.EAST}) {
      for (Map.Entry<Direction, BooleanProperty> entry : PROPERTY_BY_DIRECTION.entrySet()) {
        Direction facing = entry.getKey();
        BooleanProperty property = entry.getValue();
        if (placementState.getValue(property) != (facing.getAxis() == direction.getAxis())) continue;
        // 确保此时该方块有且只有单轴连接。

        final BlockState stateInCW = world.getBlockState(blockPos.relative(facing.getClockWise()));
        final boolean isStairsInCW = stateInCW.getBlock() instanceof StairBlock && stateInCW.hasProperty(StairBlock.FACING) && stateInCW.getValue(StairBlock.FACING) == facing.getClockWise() && stateInCW.hasProperty(StairBlock.HALF) && stateInCW.getValue(StairBlock.HALF) == Half.BOTTOM;
        final BlockState stateInCCW = world.getBlockState(blockPos.relative(facing.getCounterClockWise()));
        final boolean isStairsInCCW = stateInCCW.getBlock() instanceof StairBlock && stateInCCW.hasProperty(StairBlock.FACING) && stateInCCW.getValue(StairBlock.FACING) == facing.getCounterClockWise() && stateInCCW.hasProperty(StairBlock.HALF) && stateInCCW.getValue(StairBlock.HALF) == Half.BOTTOM;
        if (isStairsInCW != isStairsInCCW) {
          final BlockState stairState = baseHandrail.stair().defaultBlockState();
          return stairState
              .setValue(WATERLOGGED, waterlogged)
              .setValue(HandrailStairBlock.FACING, isStairsInCW ? facing.getClockWise() : facing.getCounterClockWise())
              .setValue(HandrailStairBlock.SHAPE, HandrailStairBlock.Shape.BOTTOM)
              .setValue(HandrailStairBlock.POSITION, HandrailStairBlock.Position.CENTER);
        }
      }
    }
    return placementState.setValue(WATERLOGGED, waterlogged);
  }

  public static BlockState updateSideStates(BlockState state, LevelAccessor world, BlockPos blockPos) {
    Direction mayBeOnlyInitialConnected = null;
    Direction mayBeOnlyConnected = null;
    int initialConnectedNumber = 0;
    int connectedNumber = 0;
    for (Map.Entry<Direction, BooleanProperty> entry : PROPERTY_BY_DIRECTION.entrySet()) {
      Direction facing = entry.getKey();
      BooleanProperty property = entry.getValue();
      if (state.getValue(property)) {
        mayBeOnlyInitialConnected = facing;
        initialConnectedNumber += 1;
      }
      final BlockPos neighborPos = blockPos.relative(facing);
      final boolean connectsTo = connectsTo(state, facing, world.getBlockState(neighborPos), world, blockPos, neighborPos);
      state = state.setValue(property, connectsTo);
      if (connectsTo) {
        mayBeOnlyConnected = facing;
        connectedNumber += 1;
      }
    }
    if (connectedNumber == 1) {
      state = state.setValue(PROPERTY_BY_DIRECTION.get(mayBeOnlyConnected.getOpposite()), true);
    } else if (connectedNumber == 0 && mayBeOnlyInitialConnected != null && initialConnectedNumber <= 2) {
      state = state
          .setValue(PROPERTY_BY_DIRECTION.get(mayBeOnlyInitialConnected), true)
          .setValue(PROPERTY_BY_DIRECTION.get(mayBeOnlyInitialConnected.getOpposite()), true);
    }
    return state;
  }

  public @NotNull BlockStateGenerator createBlockStates(ResourceLocation postId, ResourceLocation postSideId, ResourceLocation sideId) {
    final MultiPartGenerator blockStateSupplier = MultiPartGenerator.multiPart(this)
        .with(Variant.variant().with(VariantProperties.MODEL, postId));
    Direction.Plane.HORIZONTAL.forEach(facing -> {
      final BooleanProperty property = PROPERTY_BY_DIRECTION.get(facing);
      blockStateSupplier.with(Condition.condition().term(property, true), Variant.variant().with(VariantProperties.MODEL, sideId).with(MishangUtils.DIRECTION_Y_VARIANT, facing).with(VariantProperties.UV_LOCK, true));
      blockStateSupplier.with(Condition.condition().term(property, false), Variant.variant().with(VariantProperties.MODEL, postSideId).with(MishangUtils.DIRECTION_Y_VARIANT, facing).with(VariantProperties.UV_LOCK, true));
    });
    return blockStateSupplier;
  }

  @Override
  public @Nullable Block baseBlock() {
    return baseHandrail.baseBlock();
  }

  @Override
  public boolean connectsIn(@NotNull BlockState blockState, @NotNull Direction direction, @Nullable Direction offsetFacing) {
    return offsetFacing == null && direction.getAxis().isHorizontal() && blockState.getValue(PROPERTY_BY_DIRECTION.get(direction));
  }

  @Override
  public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
    return false;
  }
}
