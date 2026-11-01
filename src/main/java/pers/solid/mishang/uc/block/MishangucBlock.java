package pers.solid.mishang.uc.block;

import java.util.function.Consumer;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ModelProvider;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

public interface MishangucBlock {
  default LootTable.Builder getLootTable(BlockLootSubProvider blockLootTableGenerator) {
    return blockLootTableGenerator.createSingleItemTable((ItemLike) this);
  }

  default RecipeBuilder getCraftingRecipe() {
    return null;
  }

  default SingleItemRecipeBuilder getStonecuttingRecipe() {
    return null;
  }

  default ResourceLocation getStonecuttingRecipeId() {
    return RecipeBuilder.getDefaultRecipeId((ItemLike) this).withSuffix("_from_stonecutting");
  }

  default boolean shouldWriteStonecuttingRecipe() {
    return false;
  }

  default void writeRecipes(Consumer<FinishedRecipe> exporter) {
    final RecipeBuilder craftingRecipe = getCraftingRecipe();
    if (craftingRecipe != null) {
      craftingRecipe.save(exporter);
    }
    if (shouldWriteStonecuttingRecipe()) {
      final SingleItemRecipeBuilder stonecuttingRecipe = getStonecuttingRecipe();
      if (stonecuttingRecipe != null) {
        stonecuttingRecipe.save(exporter, getStonecuttingRecipeId());
      }
    }
  }

  void registerModels(ModelProvider modelProvider, BlockModelGenerators blockStateModelGenerator);


  default ResourceLocation getTexture(TextureSlot key) {
    return TextureMapping.getBlockTexture(((Block) this));
  }

  default String customRecipeCategory() {
    return null;
  }
}
