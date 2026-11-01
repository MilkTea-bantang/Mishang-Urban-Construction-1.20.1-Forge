package pers.solid.mishang.uc.block;

import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ModelProvider;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import pers.solid.mishang.uc.data.MishangucModels;

public class FullLightBlock extends Block implements MishangucBlock {
  private final Item dyeIngredient;
  private final Item concreteIngredient;

  public FullLightBlock(Properties settings, Item dyeIngredient, Item concreteIngredient) {
    super(settings);
    this.dyeIngredient = dyeIngredient;
    this.concreteIngredient = concreteIngredient;
  }

  @Override
  public void registerModels(ModelProvider modelProvider, BlockModelGenerators blockStateModelGenerator) {
    final ResourceLocation modelId = MishangucModels.LIGHT.create(this, TextureMapping.cube(this), blockStateModelGenerator.modelOutput);
    blockStateModelGenerator.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(this, modelId));
    blockStateModelGenerator.delegateItemModel(this, modelId);
  }

  @Override
  public RecipeBuilder getCraftingRecipe() {
    return ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, this, 8)
        .pattern("*#*")
        .pattern("#C#")
        .pattern("*#*")
        .define('*', dyeIngredient)
        .define('#', Items.GLOWSTONE)
        .define('C', concreteIngredient)
        .unlockedBy(RecipeProvider.getHasName(dyeIngredient), RecipeProvider.has(dyeIngredient))
        .unlockedBy(RecipeProvider.getHasName(Items.GLOWSTONE), RecipeProvider.has(Items.GLOWSTONE))
        .unlockedBy(RecipeProvider.getHasName(concreteIngredient), RecipeProvider.has(concreteIngredient));
  }

  @Override
  public String customRecipeCategory() {
    return "light";
  }
}
