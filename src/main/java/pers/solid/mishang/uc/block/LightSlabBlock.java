package pers.solid.mishang.uc.block;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ModelProvider;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import pers.solid.mishang.uc.data.MishangucModels;

@ApiStatus.AvailableSince("1.1.0")
public class LightSlabBlock extends SlabBlock implements MishangucBlock {
  public final Block baseBlock;

  public LightSlabBlock(@NotNull Block baseBlock, Properties settings) {
    super(settings);
    this.baseBlock = baseBlock;
  }

  public LightSlabBlock(@NotNull Block baseBlock) {
    super(Properties.copy(baseBlock));
    this.baseBlock = baseBlock;
  }

  @Override
  public void registerModels(ModelProvider modelProvider, BlockModelGenerators blockStateModelGenerator) {
    final ResourceLocation bottomModelId = MishangucModels.LIGHT_SLAB.create(this, TextureMapping.cube(baseBlock), blockStateModelGenerator.modelOutput);
    final ResourceLocation topModelId = MishangucModels.LIGHT_SLAB_TOP.create(this, TextureMapping.cube(baseBlock), blockStateModelGenerator.modelOutput);
    blockStateModelGenerator.blockStateOutput.accept(BlockModelGenerators.createSlab(this, bottomModelId, topModelId, ModelLocationUtils.getModelLocation(baseBlock)));
    blockStateModelGenerator.delegateItemModel(this, bottomModelId);
  }

  @Override
  public RecipeBuilder getCraftingRecipe() {
    return ((ShapedRecipeBuilder) RecipeProvider.slabBuilder(RecipeCategory.BUILDING_BLOCKS, this, Ingredient.of(baseBlock)))
        .unlockedBy(RecipeProvider.getHasName(baseBlock), RecipeProvider.has(baseBlock));
  }

  @Override
  public boolean shouldWriteStonecuttingRecipe() {
    return true;
  }

  @Override
  public SingleItemRecipeBuilder getStonecuttingRecipe() {
    return SingleItemRecipeBuilder.stonecutting(Ingredient.of(baseBlock), RecipeCategory.DECORATIONS, this, 2)
        .unlockedBy(RecipeProvider.getHasName(baseBlock), RecipeProvider.has(baseBlock));
  }

  @Override
  public LootTable.Builder getLootTable(BlockLootSubProvider blockLootTableGenerator) {
    return blockLootTableGenerator.createSlabItemTable(this);
  }

  @Override
  public String customRecipeCategory() {
    return "light";
  }
}
