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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.solid.mishang.uc.MishangUtils;
import pers.solid.mishang.uc.Mishanguc;
import pers.solid.mishang.uc.blocks.WallSignBlocks;
import pers.solid.mishang.uc.data.MishangucModels;
import pers.solid.mishang.uc.data.MishangucTextureKeys;
import pers.solid.mishang.uc.util.TextBridge;

/**
 * 发光的直立告示牌。
 */
public class GlowingStandingSignBlock extends StandingSignBlock {
  protected static final ResourceLocation DEFAULT_GLOW_TEXTURE = Mishanguc.id("block/white_light");
  public ResourceLocation glowTexture = DEFAULT_GLOW_TEXTURE;

  public GlowingStandingSignBlock(@Nullable Block baseBlock, Properties settings) {
    super(baseBlock, settings);
  }

  public GlowingStandingSignBlock(@NotNull Block baseBlock) {
    this(baseBlock, FabricBlockSettings.copyOf(baseBlock).luminance(15));
  }

  @Override
  public MutableComponent getName() {
    if (baseBlock != null) return TextBridge.translatable("block.mishanguc.glowing_standing_sign", baseBlock.getName());
    return super.getName();
  }

  @Override
  public void registerModels(ModelProvider modelProvider, BlockModelGenerators blockStateModelGenerator) {
    final TextureMapping textures = TextureMapping.defaultTexture(getBaseTexture()).put(MishangucTextureKeys.BAR, barTexture).put(MishangucTextureKeys.GLOW, glowTexture);
    final ResourceLocation modelId = MishangucModels.GLOWING_STANDING_SIGN.create(this, textures, blockStateModelGenerator.modelOutput);
    final ResourceLocation r1ModelId = MishangucModels.GLOWING_STANDING_SIGN_1.create(this, textures, blockStateModelGenerator.modelOutput);
    final ResourceLocation r2ModelId = MishangucModels.GLOWING_STANDING_SIGN_2.create(this, textures, blockStateModelGenerator.modelOutput);
    final ResourceLocation r3ModelId = MishangucModels.GLOWING_STANDING_SIGN_3.create(this, textures, blockStateModelGenerator.modelOutput);
    final ResourceLocation barredModelId = MishangucModels.GLOWING_STANDING_SIGN_BARRED.create(this, textures, blockStateModelGenerator.modelOutput);
    final ResourceLocation barredR1ModelId = MishangucModels.GLOWING_STANDING_SIGN_BARRED_1.create(this, textures, blockStateModelGenerator.modelOutput);
    final ResourceLocation barredR2ModelId = MishangucModels.GLOWING_STANDING_SIGN_BARRED_2.create(this, textures, blockStateModelGenerator.modelOutput);
    final ResourceLocation barredR3ModelId = MishangucModels.GLOWING_STANDING_SIGN_BARRED_3.create(this, textures, blockStateModelGenerator.modelOutput);
    blockStateModelGenerator.blockStateOutput.accept(createBlockStates(modelId, r1ModelId, r2ModelId, r3ModelId, barredModelId, barredR1ModelId, barredR2ModelId, barredR3ModelId));
    blockStateModelGenerator.delegateItemModel(this, barredModelId);
  }

  private @Nullable String getRecipeGroup() {
    if (baseBlock instanceof ColoredBlock) return null;
    if (MishangUtils.isConcrete(baseBlock)) return "mishanguc:glowing_concrete_standing_sign";
    if (MishangUtils.isTerracotta(baseBlock)) return "mishanguc:glowing_terracotta_standing_sign";
    if (baseBlock == Blocks.BLUE_ICE || baseBlock == Blocks.PACKED_ICE) {
      return "mishanguc:glowing_ice_standing_sign";
    }
    return null;
  }

  @Override
  public RecipeBuilder getCraftingRecipe() {
    if (baseBlock == null) return null;
    return ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, this, 4)
        .pattern("---")
        .pattern("###")
        .pattern(" | ")
        .define('#', baseBlock).define('-', WallSignBlocks.INVISIBLE_GLOWING_WALL_SIGN).define('|', Items.STICK)
        .unlockedBy("has_base_block", RecipeProvider.has(baseBlock))
        .unlockedBy("has_sign", RecipeProvider.has(WallSignBlocks.INVISIBLE_GLOWING_WALL_SIGN))
        .group(getRecipeGroup());
  }
}
