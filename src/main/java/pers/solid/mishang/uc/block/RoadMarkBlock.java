package pers.solid.mishang.uc.block;

import pers.solid.mishang.uc.shim.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
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
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.solid.mishang.uc.MishangUtils;
import pers.solid.mishang.uc.blocks.RoadMarkBlocks;
import pers.solid.mishang.uc.data.MishangucModels;
import pers.solid.mishang.uc.util.EightHorizontalDirection;
import pers.solid.mishang.uc.util.FourHorizontalAxis;

@ApiStatus.AvailableSince("1.0.4")
public class RoadMarkBlock extends Block implements SimpleWaterloggedBlock, MishangucBlock {
  public static final VoxelShape SHAPE = box(0, 0, 0, 16, 1, 16);
  public static final VoxelShape SHAPE_X = box(0, 0, 2, 16, 1, 14);
  public static final VoxelShape SHAPE_Z = box(2, 0, 0, 14, 1, 16);
  public static final VoxelShape SHAPE_ON_SLAB = box(0, -8, 0, 16, -7, 16);
  public static final VoxelShape SHAPE_ON_SLAB_X = box(0, -8, 2, 16, -7, 14);
  public static final VoxelShape SHAPE_ON_SLAB_Z = box(2, -8, 0, 14, -7, 16);
  public static final BooleanProperty ON_SLAB = BooleanProperty.create("on_slab");
  protected final ResourceLocation texture;
  private static final VoxelShape SHAPE_TOP_MASK = box(0, 15.5, 0, 16, 16, 16);
  private static final VoxelShape SHAPE_SLAB_TOP_MASK = box(0, 7.5, 0, 16, 8, 16);

  public RoadMarkBlock(@NotNull ResourceLocation texture, Properties settings) {
    super(settings);
    this.texture = texture;
    registerDefaultState(defaultBlockState()
        .setValue(BlockStateProperties.WATERLOGGED, false)
        .setValue(ON_SLAB, false));
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder);
    builder.add(BlockStateProperties.WATERLOGGED, ON_SLAB);
  }

  @SuppressWarnings("deprecation")
  @Override
  public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
    final BlockPos downPos = pos.below();
    final BlockState downState = world.getBlockState(downPos);
    final VoxelShape downShape = downState.getBlockSupportShape(world, downPos);
    return !Shapes.joinIsNotEmpty(downShape, SHAPE_TOP_MASK, BooleanOp.ONLY_SECOND) || !Shapes.joinIsNotEmpty(downShape, SHAPE_SLAB_TOP_MASK, BooleanOp.ONLY_SECOND);
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext ctx) {
    BlockState state = super.getStateForPlacement(ctx);
    if (state != null) {
      final BlockPos blockPos = ctx.getClickedPos();
      final Level world = ctx.getLevel();
      state = state.setValue(BlockStateProperties.WATERLOGGED, world.getFluidState(blockPos).getType() == Fluids.WATER);
      final BlockPos downPos = blockPos.below();
      final BlockState downState = world.getBlockState(downPos);
      final VoxelShape downShape = downState.getBlockSupportShape(world, downPos);
      if (Shapes.joinIsNotEmpty(downShape, SHAPE_TOP_MASK, BooleanOp.ONLY_SECOND) && !Shapes.joinIsNotEmpty(downShape, SHAPE_SLAB_TOP_MASK, BooleanOp.ONLY_SECOND)) {
        state = state.setValue(ON_SLAB, true);
      }
    }
    return state;
  }

  @SuppressWarnings("deprecation")
  @Override
  public FluidState getFluidState(BlockState state) {
    return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
  }

  @SuppressWarnings("deprecation")
  @Override
  public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
    if (state.getValue(BlockStateProperties.WATERLOGGED)) {
      world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
    }
    if (direction == Direction.DOWN) {
      if (!this.canSurvive(state, world, pos)) {
        return Blocks.AIR.defaultBlockState();
      } else {
        return super.updateShape(state, direction, neighborState, world, pos, neighborPos)
            .setValue(ON_SLAB, Shapes.joinIsNotEmpty(world.getBlockState(neighborPos).getShape(world, neighborPos), SHAPE_TOP_MASK, BooleanOp.ONLY_SECOND));
      }
    }
    return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
  }

  @SuppressWarnings("deprecation")
  @Override
  public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
    return state.getValue(ON_SLAB) ? SHAPE_ON_SLAB : SHAPE;
  }

  public static RoadMarkBlock createAxisFacing(ResourceLocation texture, Properties settings) {
    return new AxisFacing(texture, settings);
  }

  public static RoadMarkBlock createDirectionalFacing(ResourceLocation texture, Properties settings) {
    return new DirectionalFacing(texture, settings);
  }

  @Override
  public RecipeBuilder getCraftingRecipe() {
    return SingleItemRecipeBuilder.stonecutting(Ingredient.of(ConventionalItemTags.WHITE_DYES), RecipeCategory.DECORATIONS, this)
        .unlockedBy("has_white_dye", RecipeProvider.has(ConventionalItemTags.WHITE_DYES));
  }

  @Override
  public void registerModels(ModelProvider modelProvider, BlockModelGenerators blockStateModelGenerator) {
    final TextureMapping textures = TextureMapping.cube(texture);
    final ResourceLocation modelId = MishangucModels.ROAD_MARK.create(this, textures, blockStateModelGenerator.modelOutput);
    final ResourceLocation onSlabModelId = MishangucModels.ROAD_MARK_ON_SLAB.create(this, textures, blockStateModelGenerator.modelOutput);
    blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(this)
        .with(PropertyDispatch.property(ON_SLAB)
            .select(false, new Variant().with(VariantProperties.MODEL, modelId))
            .select(true, new Variant().with(VariantProperties.MODEL, onSlabModelId))));
    ModelTemplates.FLAT_HANDHELD_ITEM.create(ModelLocationUtils.getModelLocation(asItem()), TextureMapping.layer0(texture), blockStateModelGenerator.modelOutput);
  }

  @Override
  public String customRecipeCategory() {
    return "road_marks";
  }

  protected static class AxisFacing extends RoadMarkBlock {
    public static final EnumProperty<FourHorizontalAxis> AXIS = EnumProperty.create("axis", FourHorizontalAxis.class);

    protected AxisFacing(ResourceLocation texture, Properties settings) {
      super(texture, settings);
      registerDefaultState(defaultBlockState().setValue(AXIS, FourHorizontalAxis.X));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(AXIS);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
      final BlockState state = super.getStateForPlacement(ctx);
      if (state != null) {
        return state.setValue(AXIS, EightHorizontalDirection.fromRotation(ctx.getRotation()).axis);
      }
      return null;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return switch (state.getValue(AXIS)) {
        case X -> state.getValue(ON_SLAB) ? SHAPE_ON_SLAB_X : SHAPE_X;
        case Z -> state.getValue(ON_SLAB) ? SHAPE_ON_SLAB_Z : SHAPE_Z;
        default -> super.getShape(state, world, pos, context);
      };
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
      return super.rotate(state, rotation).setValue(AXIS, state.getValue(AXIS).rotate(rotation));
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
      BlockState mirror1 = super.mirror(state, mirror);
      if (RoadMarkBlocks.LEFT_TO_RIGHT.containsKey(this)) {
        mirror1 = RoadMarkBlocks.LEFT_TO_RIGHT.get(this).withPropertiesOf(mirror1);
      } else if (RoadMarkBlocks.LEFT_TO_RIGHT.inverse().containsKey(this)) {
        mirror1 = RoadMarkBlocks.LEFT_TO_RIGHT.inverse().get(this).withPropertiesOf(mirror1);
      }
      return mirror1.setValue(AXIS, state.getValue(AXIS).mirror());
    }

    @Override
    public void registerModels(ModelProvider modelProvider, BlockModelGenerators blockStateModelGenerator) {
      final TextureMapping textures = TextureMapping.cube(texture);
      final ResourceLocation modelId = MishangucModels.ROAD_MARK.create(this, textures, blockStateModelGenerator.modelOutput);
      final ResourceLocation rotatedModelId = MishangucModels.ROAD_MARK_ROTATED.create(this, textures, blockStateModelGenerator.modelOutput);
      final ResourceLocation onSlabModelId = MishangucModels.ROAD_MARK_ON_SLAB.create(this, textures, blockStateModelGenerator.modelOutput);
      final ResourceLocation onSlabRotatedModelId = MishangucModels.ROAD_MARK_ON_SLAB_ROTATED.create(this, textures, blockStateModelGenerator.modelOutput);
      final PropertyDispatch.C2<Boolean, FourHorizontalAxis> map = PropertyDispatch.properties(ON_SLAB, AXIS)
          .select(false, FourHorizontalAxis.X, Variant.variant().with(VariantProperties.MODEL, modelId).with(MishangUtils.INT_Y_VARIANT, 90))
          .select(false, FourHorizontalAxis.NW_SE, Variant.variant().with(VariantProperties.MODEL, rotatedModelId).with(MishangUtils.INT_Y_VARIANT, 90))
          .select(false, FourHorizontalAxis.Z, Variant.variant().with(VariantProperties.MODEL, modelId).with(MishangUtils.INT_Y_VARIANT, 0))
          .select(false, FourHorizontalAxis.NE_SW, Variant.variant().with(VariantProperties.MODEL, rotatedModelId).with(MishangUtils.INT_Y_VARIANT, 0))
          .select(true, FourHorizontalAxis.X, Variant.variant().with(VariantProperties.MODEL, onSlabModelId).with(MishangUtils.INT_Y_VARIANT, 90))
          .select(true, FourHorizontalAxis.NW_SE, Variant.variant().with(VariantProperties.MODEL, onSlabRotatedModelId).with(MishangUtils.INT_Y_VARIANT, 90))
          .select(true, FourHorizontalAxis.Z, Variant.variant().with(VariantProperties.MODEL, onSlabModelId).with(MishangUtils.INT_Y_VARIANT, 0))
          .select(true, FourHorizontalAxis.NE_SW, Variant.variant().with(VariantProperties.MODEL, onSlabRotatedModelId).with(MishangUtils.INT_Y_VARIANT, 0));
      blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(this).with(map));
      ModelTemplates.FLAT_HANDHELD_ITEM.create(ModelLocationUtils.getModelLocation(asItem()), TextureMapping.layer0(texture), blockStateModelGenerator.modelOutput);
    }
  }

  protected static class DirectionalFacing extends RoadMarkBlock {
    public static final EnumProperty<EightHorizontalDirection> FACING = EnumProperty.create("facing", EightHorizontalDirection.class);

    public DirectionalFacing(ResourceLocation texture, Properties settings) {
      super(texture, settings);
      registerDefaultState(defaultBlockState().setValue(FACING, EightHorizontalDirection.SOUTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(FACING);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
      final BlockState state = super.getStateForPlacement(ctx);
      if (state != null) {
        return state.setValue(FACING, EightHorizontalDirection.fromRotation(ctx.getRotation()));
      }
      return null;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return switch (state.getValue(FACING).axis) {
        case X -> state.getValue(ON_SLAB) ? SHAPE_ON_SLAB_X : SHAPE_X;
        case Z -> state.getValue(ON_SLAB) ? SHAPE_ON_SLAB_Z : SHAPE_Z;
        default -> super.getShape(state, world, pos, context);
      };
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
      return super.rotate(state, rotation).setValue(FACING, state.getValue(FACING).rotate(rotation));
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
      BlockState mirror1 = super.mirror(state, mirror);
      if (RoadMarkBlocks.LEFT_TO_RIGHT.containsKey(this)) {
        mirror1 = RoadMarkBlocks.LEFT_TO_RIGHT.get(this).withPropertiesOf(mirror1);
      } else if (RoadMarkBlocks.LEFT_TO_RIGHT.inverse().containsKey(this)) {
        mirror1 = RoadMarkBlocks.LEFT_TO_RIGHT.inverse().get(this).withPropertiesOf(mirror1);
      }
      return mirror1.setValue(FACING, state.getValue(FACING).mirror(mirror));
    }

    @Override
    public void registerModels(ModelProvider modelProvider, BlockModelGenerators blockStateModelGenerator) {
      final TextureMapping textures = TextureMapping.cube(texture);
      final ResourceLocation modelId = MishangucModels.ROAD_MARK.create(this, textures, blockStateModelGenerator.modelOutput);
      final ResourceLocation rotatedModelId = MishangucModels.ROAD_MARK_ROTATED.create(this, textures, blockStateModelGenerator.modelOutput);
      final ResourceLocation onSlabModelId = MishangucModels.ROAD_MARK_ON_SLAB.create(this, textures, blockStateModelGenerator.modelOutput);
      final ResourceLocation onSlabRotatedModelId = MishangucModels.ROAD_MARK_ON_SLAB_ROTATED.create(this, textures, blockStateModelGenerator.modelOutput);
      final PropertyDispatch.C2<Boolean, EightHorizontalDirection> map = PropertyDispatch.properties(ON_SLAB, FACING);
      for (EightHorizontalDirection direction : EightHorizontalDirection.VALUES) {
        int rotation = (int) direction.asRotation();
        boolean rotated = direction.right().isPresent();
        if (rotated) {
          rotation -= 45;
        }
        map.select(false, direction, Variant.variant().with(VariantProperties.MODEL, rotated ? rotatedModelId : modelId).with(MishangUtils.INT_Y_VARIANT, rotation));
        map.select(true, direction, Variant.variant().with(VariantProperties.MODEL, rotated ? onSlabRotatedModelId : onSlabModelId).with(MishangUtils.INT_Y_VARIANT, rotation));
      }
      blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(this).with(map));
      ModelTemplates.FLAT_HANDHELD_ITEM.create(ModelLocationUtils.getModelLocation(asItem()), TextureMapping.layer0(texture), blockStateModelGenerator.modelOutput);
    }
  }
}
