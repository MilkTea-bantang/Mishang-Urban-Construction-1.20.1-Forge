package pers.solid.mishang.uc.block;

import pers.solid.mishang.uc.shim.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ModelProvider;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.solid.mishang.uc.MishangUtils;
import pers.solid.mishang.uc.Mishanguc;
import pers.solid.mishang.uc.blocks.WallSignBlocks;
import pers.solid.mishang.uc.data.MishangucModels;
import pers.solid.mishang.uc.data.MishangucTextureKeys;
import pers.solid.mishang.uc.util.TextBridge;

public class GlowingWallSignBlock extends WallSignBlock {
  @ApiStatus.AvailableSince("0.1.7")
  protected static final ResourceLocation DEFAULT_GLOW_TEXTURE = Mishanguc.id("block/white_light");
  /**
   * 告示牌发光部分的纹理。默认为 {@link #DEFAULT_GLOW_TEXTURE}。
   */
  public @Nullable ResourceLocation glowTexture = DEFAULT_GLOW_TEXTURE;

  public GlowingWallSignBlock(@Nullable Block baseBlock, Properties settings) {
    super(baseBlock, settings);
  }

  public GlowingWallSignBlock(@NotNull Block baseBlock) {
    this(baseBlock, FabricBlockSettings.copyOf(baseBlock).luminance(15));
  }

  @Override
  public MutableComponent getName() {
    return TextBridge.translatable("block.mishanguc.glowing_wall_sign", baseBlock.getName());
  }

  private @Nullable String getRecipeGroup() {
    if (baseBlock instanceof ColoredBlock) return null;
    if (MishangUtils.isConcrete(baseBlock)) return "mishanguc:glowing_concrete_wall_sign";
    if (MishangUtils.isTerracotta(baseBlock)) return "mishanguc:glowing_terracotta_wall_sign";
    if (baseBlock == Blocks.BLUE_ICE || baseBlock == Blocks.PACKED_ICE) {
      return "mishanguc:glowing_ice_wall_sign";
    }
    return null;
  }

  @Override
  public @Nullable RecipeBuilder getCraftingRecipe() {
    if (baseBlock == null) return null;
    return ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, this, 6)
        .pattern("---")
        .pattern("###")
        .pattern("---")
        .define('#', baseBlock).define('-', WallSignBlocks.INVISIBLE_GLOWING_WALL_SIGN)
        .unlockedBy("has_base_block", RecipeProvider.has(baseBlock)).unlockedBy("has_sign", RecipeProvider.has(WallSignBlocks.INVISIBLE_GLOWING_WALL_SIGN))
        .group(getRecipeGroup());
  }

  @Override
  public void registerModels(ModelProvider modelProvider, BlockModelGenerators blockStateModelGenerator) {
    final TextureMapping textures = TextureMapping.defaultTexture(getBaseTexture()).put(MishangucTextureKeys.GLOW, glowTexture);
    final ResourceLocation modelId = MishangucModels.GLOWING_WALL_SIGN.create(this, textures, blockStateModelGenerator.modelOutput);
    blockStateModelGenerator.blockStateOutput.accept(createBlockStates(modelId));
    blockStateModelGenerator.delegateItemModel(this, modelId);
  }
}
