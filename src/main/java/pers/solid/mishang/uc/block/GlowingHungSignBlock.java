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

public class GlowingHungSignBlock extends HungSignBlock {
  @ApiStatus.AvailableSince("0.1.7")
  protected static final ResourceLocation DEFAULT_GLOW_TEXTURE = Mishanguc.id("block/white_light");
  public ResourceLocation glowTexture;

  public GlowingHungSignBlock(@Nullable Block baseBlock, FabricBlockSettings settings) {
    super(baseBlock, settings.luminance(15));
    this.glowTexture = DEFAULT_GLOW_TEXTURE;
  }

  @ApiStatus.AvailableSince("0.1.7")
  public GlowingHungSignBlock(@NotNull Block baseBlock) {
    this(baseBlock, FabricBlockSettings.copyOf(baseBlock).luminance(15));
  }

  @Override
  public MutableComponent getName() {
    if (baseBlock != null) {
      return TextBridge.translatable("block.mishanguc.glowing_hung_sign", baseBlock.getName());
    }
    return super.getName();
  }

  @Override
  public void registerModels(ModelProvider modelProvider, BlockModelGenerators blockStateModelGenerator) {
    final ResourceLocation texture = getBaseTexture();
    final TextureMapping textures = TextureMapping.defaultTexture(texture);
    if (barTexture != null) textures.put(MishangucTextureKeys.BAR, barTexture);
    if (textureTop != null) textures.put(MishangucTextureKeys.TEXTURE_TOP, textureTop);
    textures.put(MishangucTextureKeys.GLOW, glowTexture);

    final ResourceLocation id = MishangucModels.GLOWING_HUNG_SIGN.create(this, textures, blockStateModelGenerator.modelOutput);
    final ResourceLocation bodyId = MishangucModels.GLOWING_HUNG_SIGN_BODY.create(this, textures, blockStateModelGenerator.modelOutput);
    final ResourceLocation topBarId = MishangucModels.HUNG_SIGN_TOP_BAR.create(this, textures, blockStateModelGenerator.modelOutput);
    final ResourceLocation topBarEdgeId = MishangucModels.HUNG_SIGN_TOP_BAR_EDGE.create(this, textures, blockStateModelGenerator.modelOutput);

    blockStateModelGenerator.blockStateOutput.accept(createBlockStates(bodyId, topBarId, topBarEdgeId));
    blockStateModelGenerator.delegateItemModel(this, id);
  }


  private @Nullable String getRecipeGroup() {
    if (baseBlock instanceof ColoredBlock) return null;
    if (MishangUtils.isConcrete(baseBlock)) return "mishanguc:glowing_concrete_hung_sign";
    if (MishangUtils.isTerracotta(baseBlock)) return "mishanguc:glowing_terracotta_hung_sign";
    if (baseBlock == Blocks.BLUE_ICE || baseBlock == Blocks.PACKED_ICE) {
      return "mishanguc:glowing_ice_hung_sign";
    }
    return null;
  }

  @Override
  public RecipeBuilder getCraftingRecipe() {
    if (baseBlock == null) return null;
    return ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, this, 6)
        .pattern("-#-")
        .pattern("-#-")
        .pattern("-#-")
        .define('#', baseBlock).define('-', WallSignBlocks.INVISIBLE_GLOWING_WALL_SIGN)
        .unlockedBy("has_base_block", RecipeProvider.has(baseBlock))
        .unlockedBy("has_sign", RecipeProvider.has(WallSignBlocks.INVISIBLE_GLOWING_WALL_SIGN))
        .group(getRecipeGroup());
  }
}
