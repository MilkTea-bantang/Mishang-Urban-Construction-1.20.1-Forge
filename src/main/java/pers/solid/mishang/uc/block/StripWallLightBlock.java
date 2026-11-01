package pers.solid.mishang.uc.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.models.model.*;
import net.minecraft.data.models.blockstates.*;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ModelProvider;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import pers.solid.mishang.uc.MishangUtils;

import java.util.Map;

/**
 * 类似于墙上的灯方块，但是是条状的，因此具有多一个属性。
 */
public class StripWallLightBlock extends WallLightBlock implements LightConnectable {
  protected static final EnumProperty<StripType> STRIP_TYPE =
      EnumProperty.create("strip_type", StripType.class);
  private static final Map<Direction, VoxelShape> SHAPE_PER_DIRECTION_WHEN_HORIZONTAL =
      MishangUtils.createDirectionToShape(0, 0, 4, 16, 2, 12);
  private static final Map<Direction, VoxelShape> SHAPE_PER_DIRECTION_WHEN_VERTICAL =
      MishangUtils.createDirectionToShape(4, 0, 0, 12, 2, 16);

  public StripWallLightBlock(String lightColor, Properties settings) {
    super(lightColor, settings, false);
  }

  @Override
  public boolean isConnectedIn(BlockState blockState, Direction facing, Direction direction) {
    final StripType stripType = blockState.getValue(STRIP_TYPE);
    if (facing != blockState.getValue(FACING) || direction.getAxis() == facing.getAxis()) {
      return false;
    }
    return switch (stripType) {
      case VERTICAL -> facing.getAxis() == Direction.Axis.Y
          ? direction.getAxis() == Direction.Axis.Z
          : direction.getAxis() == Direction.Axis.Y;
      case HORIZONTAL -> facing.getAxis() == Direction.Axis.Y
          ? direction.getAxis() == Direction.Axis.X
          : direction.getAxis() != Direction.Axis.Y;
    };
  }

  @Override
  public BlockState rotate(BlockState state, Rotation rotation) {
    final BlockState rotate = super.rotate(state, rotation);
    if (rotate.getValue(FACING).getAxis().isVertical() && (rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.COUNTERCLOCKWISE_90)) {
      return rotate.setValue(STRIP_TYPE, rotate.getValue(STRIP_TYPE).another());
    }
    return rotate;
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder);
    builder.add(STRIP_TYPE);
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext ctx) {
    final BlockState placementState = super.getStateForPlacement(ctx);
    if (placementState == null) {
      return null;
    }
    final Player player = ctx.getPlayer();
    return placementState.setValue(
        STRIP_TYPE,
        ctx.getClickedFace().getAxis() == Direction.Axis.Y
            ? (ctx.getHorizontalDirection().getAxis() == Direction.Axis.X
            ? StripType.HORIZONTAL
            : StripType.VERTICAL)
            : (player != null && player.isShiftKeyDown() ? StripType.VERTICAL : StripType.HORIZONTAL));
  }

  @Override
  public VoxelShape getShape(
      BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
    return (state.getValue(STRIP_TYPE) == StripType.VERTICAL
        ? SHAPE_PER_DIRECTION_WHEN_VERTICAL
        : SHAPE_PER_DIRECTION_WHEN_HORIZONTAL)
        .get(state.getValue(FACING));
  }

  public enum StripType implements StringRepresentable {
    /**
     * 水平的，对于天花板上或地上的表示为东西方向。
     */
    HORIZONTAL,
    /**
     * 垂直的，对于天花板上或地上的表示为南北方向。
     */
    VERTICAL;

    @Override
    public String getSerializedName() {
      return switch (this) {
        case HORIZONTAL -> "horizontal";
        case VERTICAL -> "vertical";
      };
    }

    public StripType another() {
      return switch (this) {
        case HORIZONTAL -> VERTICAL;
        case VERTICAL -> HORIZONTAL;
      };
    }
  }

  @SuppressWarnings("deprecation")
  @Override
  public boolean skipRendering(BlockState state, BlockState stateFrom, Direction direction) {
    return stateFrom.is(this) && ((LightConnectable) stateFrom.getBlock()).isConnectedIn(stateFrom, state.getValue(FACING), direction.getOpposite()) || super.skipRendering(state, stateFrom, direction);
  }


  @Override
  public void registerModels(ModelProvider modelProvider, BlockModelGenerators blockStateModelGenerator) {
    final PropertyDispatch.C2<Direction, StripType> map = PropertyDispatch.properties(FACING, STRIP_TYPE);

    final TextureMapping textureMap = getTextureMap();
    final ResourceLocation id = getModelType().create(this, textureMap, blockStateModelGenerator.modelOutput);
    final ResourceLocation idVertical = getModelType("_vertical").create(this, textureMap, blockStateModelGenerator.modelOutput);

    map.select(Direction.UP, StripType.HORIZONTAL, Variant.variant().with(VariantProperties.MODEL, id));
    map.select(Direction.UP, StripType.VERTICAL, Variant.variant().with(VariantProperties.MODEL, idVertical));
    map.select(Direction.DOWN, StripType.HORIZONTAL, Variant.variant().with(VariantProperties.MODEL, id).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180));
    map.select(Direction.DOWN, StripType.VERTICAL, Variant.variant().with(VariantProperties.MODEL, idVertical).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180));
    for (Direction direction : Direction.Plane.HORIZONTAL) {
      map.select(direction, StripType.HORIZONTAL, Variant.variant().with(VariantProperties.MODEL, id).with(VariantProperties.X_ROT, VariantProperties.Rotation.R270).with(MishangUtils.DIRECTION_Y_VARIANT, direction));
      map.select(direction, StripType.VERTICAL, Variant.variant().with(VariantProperties.MODEL, idVertical).with(VariantProperties.X_ROT, VariantProperties.Rotation.R270).with(MishangUtils.DIRECTION_Y_VARIANT, direction));
    }
    blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(this, Variant.variant().with(VariantProperties.UV_LOCK, true)).with(map));
  }
}
