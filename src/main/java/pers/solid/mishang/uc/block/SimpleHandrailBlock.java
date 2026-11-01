package pers.solid.mishang.uc.block;

import pers.solid.mishang.uc.shim.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.data.models.model.*;
import net.minecraft.data.models.blockstates.*;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ModelProvider;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.solid.mishang.uc.MishangUtils;
import pers.solid.mishang.uc.data.MishangucModels;
import pers.solid.mishang.uc.util.TextBridge;

/**
 * 简单的栏杆方块。基本上都是采用相同的纹理，如有使用也可以采用不同的纹理。其形状都是最基本的图形。
 */
public class SimpleHandrailBlock extends HandrailBlock {
  /**
   * 该栏杆的基础方块。
   */
  public final @Nullable Block baseBlock;

  /**
   * 该方块对应的中间位置版本。
   */
  public final CentralBlock central;
  /**
   * 该方块对应的角落位置版本。
   */
  public final CornerBlock corner;
  /**
   * 该方块对应的楼梯扶手方块。
   */
  public final StairBlock stair;
  /**
   * 该方块对应的外角方块。
   */
  public final OuterBlock outer;

  /**
   * 栏杆的纹理。若为 {@code null}，则默认根据 {@link #baseBlock} 推断纹理。
   */
  public @Nullable ResourceLocation texture;
  /**
   * 栏杆顶部部分的纹理。
   */
  public @Nullable ResourceLocation top;
  /**
   * 栏杆底部部分的纹理。
   */
  public @Nullable ResourceLocation bottom;

  public SimpleHandrailBlock(@Nullable Block baseBlock, Properties settings) {
    super(settings.noOcclusion());
    this.baseBlock = baseBlock;
    this.central = new CentralBlock(this);
    this.corner = new CornerBlock(this);
    this.stair = new StairBlock(this);
    this.outer = new OuterBlock(this);
  }

  public SimpleHandrailBlock(@NotNull Block baseBlock) {
    this(baseBlock, FabricBlockSettings.copyOf(baseBlock));
  }

  @Override
  public void registerModels(ModelProvider modelProvider, BlockModelGenerators blockStateModelGenerator) {
    final TextureMapping textures = getTextures();
    final ResourceLocation modelId = MishangucModels.SIMPLE_HANDRAIL.create(this, textures, blockStateModelGenerator.modelOutput);
    MishangucModels.SIMPLE_HANDRAIL_INVENTORY.create(ModelLocationUtils.getModelLocation(asItem()), textures, blockStateModelGenerator.modelOutput);
    blockStateModelGenerator.blockStateOutput.accept(createBlockStates(modelId));
  }

  @Override
  public @NotNull TextureMapping getTextures() {
    return TextureMapping.cube(getTexture()).put(TextureSlot.TOP, top).put(TextureSlot.BOTTOM, bottom);
  }

  @Override
  public final HandrailCentralBlock<SimpleHandrailBlock> central() {
    return central;
  }

  @Override
  public HandrailCornerBlock<? extends HandrailBlock> corner() {
    return corner;
  }

  @Override
  public HandrailStairBlock<? extends HandrailBlock> stair() {
    return stair;
  }

  @Override
  public HandrailOuterBlock<? extends HandrailBlock> outer() {
    return outer;
  }

  @Override
  public @Nullable Block baseBlock() {
    return baseBlock;
  }

  /**
   * @return 该方块的基础纹理变量。
   */
  protected ResourceLocation getTexture() {
    return texture == null ? TextureMapping.getBlockTexture(baseBlock) : texture;
  }

  @Override
  public MutableComponent getName() {
    if (baseBlock != null) {
      return TextBridge.translatable("block.mishanguc.simple_handrail", baseBlock.getName());
    } else return super.getName();
  }

  public static class CentralBlock extends HandrailCentralBlock<SimpleHandrailBlock> {
    public CentralBlock(@NotNull SimpleHandrailBlock baseBlock) {
      super(baseBlock, FabricBlockSettings.copyOf(baseBlock).noOcclusion());
    }

    @Override
    public void registerModels(ModelProvider modelProvider, BlockModelGenerators blockStateModelGenerator) {
      final ResourceLocation postModelId = MishangucModels.SIMPLE_HANDRAIL_POST.create(this, baseHandrail.getTextures(), blockStateModelGenerator.modelOutput);
      final ResourceLocation sideModelId = MishangucModels.SIMPLE_HANDRAIL_SIDE.create(this, baseHandrail.getTextures(), blockStateModelGenerator.modelOutput);
      final ResourceLocation postSideModelId = MishangucModels.SIMPLE_HANDRAIL_POST_SIDE.create(this, baseHandrail.getTextures(), blockStateModelGenerator.modelOutput);
      blockStateModelGenerator.blockStateOutput.accept(createBlockStates(postModelId, postSideModelId, sideModelId));
    }

    @Override
    public MutableComponent getName() {
      final Block block = baseBlock();
      return block == null ? super.getName() : TextBridge.translatable("block.mishanguc.simple_handrail_central", block.getName());
    }
  }

  public static class CornerBlock extends HandrailCornerBlock<SimpleHandrailBlock> {
    public CornerBlock(@NotNull SimpleHandrailBlock baseHandrail) {
      super(baseHandrail, FabricBlockSettings.copyOf(baseHandrail).noOcclusion());
    }

    @Override
    public void registerModels(ModelProvider modelProvider, BlockModelGenerators blockStateModelGenerator) {
      final ResourceLocation modelId = MishangucModels.SIMPLE_HANDRAIL_CORNER.create(this, baseHandrail.getTextures(), blockStateModelGenerator.modelOutput);
      blockStateModelGenerator.blockStateOutput.accept(createBlockStates(modelId));
    }

    @Override
    public MutableComponent getName() {
      final Block block = baseBlock();
      return block == null ? super.getName() : TextBridge.translatable("block.mishanguc.simple_handrail_corner", block.getName());
    }
  }

  public static class StairBlock extends HandrailStairBlock<SimpleHandrailBlock> {
    public StairBlock(@NotNull SimpleHandrailBlock baseRail) {
      super(baseRail, FabricBlockSettings.copyOf(baseRail).noOcclusion());
    }

    @Override
    public void registerModels(ModelProvider modelProvider, BlockModelGenerators blockStateModelGenerator) {
      final TextureMapping textures = baseHandrail.getTextures();
      final ResourceLocation baseModelId = MishangucModels.createBlock("simple_handrail_stair_middle_center", TextureSlot.TEXTURE, TextureSlot.TOP, TextureSlot.BOTTOM).create(this, textures, blockStateModelGenerator.modelOutput);
      for (Shape shape : Shape.values()) {
        for (Position position : Position.values()) {
          MishangucModels.createBlock(String.format("simple_handrail_stair_%s_%s", shape.getSerializedName(), position.getSerializedName()), "_" + shape.getSerializedName() + "_" + position.getSerializedName(), TextureSlot.TEXTURE, TextureSlot.TOP, TextureSlot.BOTTOM).create(this, textures, blockStateModelGenerator.modelOutput);
        }
      }
      blockStateModelGenerator.blockStateOutput.accept(createBlockStates(baseModelId));
    }

    @Override
    public MutableComponent getName() {
      final Block block = baseBlock();
      return block == null ? super.getName() : TextBridge.translatable("block.mishanguc.simple_handrail_stair", block.getName());
    }
  }

  public static class OuterBlock extends HandrailOuterBlock<SimpleHandrailBlock> {
    public OuterBlock(@NotNull SimpleHandrailBlock baseRail) {
      super(baseRail, FabricBlockSettings.copyOf(baseRail).noOcclusion());
    }

    @Override
    public void registerModels(ModelProvider modelProvider, BlockModelGenerators blockStateModelGenerator) {
      final ResourceLocation modelId = MishangucModels.SIMPLE_HANDRAIL_OUTER.create(this, baseHandrail.getTextures(), blockStateModelGenerator.modelOutput);
      blockStateModelGenerator.blockStateOutput.accept(createBlockStates(modelId));
    }

    @Override
    public MutableComponent getName() {
      final Block block = baseBlock();
      return block == null ? super.getName() : TextBridge.translatable("block.mishanguc.simple_handrail_outer", block.getName());
    }
  }

  private String getRecipeGroup() {
    if (baseBlock instanceof ColoredBlock) return null;
    if (MishangUtils.isConcrete(baseBlock)) return "mishanguc:simple_concrete_handrail";
    if (MishangUtils.isTerracotta(baseBlock)) return "mishanguc:simple_terracotta_handrail";
    if (MishangUtils.isStained_glass(baseBlock)) return "mishanguc:simple_stained_glass_handrail";
    if (MishangUtils.isWood(baseBlock)) return "mishanguc:simple_wood_handrail";
    if (MishangUtils.isPlanks(baseBlock)) return "mishanguc:simple_plank_handrail";
    if (baseBlock == Blocks.ICE || baseBlock == Blocks.PACKED_ICE || baseBlock == Blocks.BLUE_ICE) {
      return "mishanguc:simple_ice_handrail";
    }
    return null;
  }

  @Override
  public RecipeBuilder getCraftingRecipe() {
    return SingleItemRecipeBuilder.stonecutting(Ingredient.of(baseBlock), RecipeCategory.DECORATIONS, this, 5)
        .unlockedBy(RecipeProvider.getHasName(baseBlock), RecipeProvider.has(baseBlock))
        .group(getRecipeGroup());
  }
}
