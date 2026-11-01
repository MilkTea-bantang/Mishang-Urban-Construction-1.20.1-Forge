package pers.solid.mishang.uc.block;

import pers.solid.mishang.uc.util.LineColor;
import pers.solid.mishang.uc.util.LineType;
import pers.solid.mishang.uc.util.RoadConnectionState;

import java.util.List;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.Direction;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;

public class RoadBlock extends AbstractRoadBlock {
  private final ResourceLocation texture;

  public RoadBlock(Properties settings, ResourceLocation texture, LineColor lineColor) {
    super(settings, lineColor, LineType.NORMAL);
    this.texture = texture;
  }

  @Override
  public RoadConnectionState getConnectionStateOf(BlockState state, Direction direction) {
    return RoadConnectionState.empty();
  }

  @Override
  protected <B extends Block & Road> void registerBaseOrSlabModels(B road, BlockModelGenerators blockStateModelGenerator) {
    final TextureMapping textures = TextureMapping.cube(texture);
    final ResourceLocation modelId;
    if (road instanceof SlabBlock) {
      modelId = ModelTemplates.SLAB_BOTTOM.create(road, textures, blockStateModelGenerator.modelOutput);
      ModelTemplates.SLAB_TOP.create(road, textures, blockStateModelGenerator.modelOutput);
    } else {
      modelId = ModelTemplates.CUBE_ALL.create(road, textures, blockStateModelGenerator.modelOutput);
    }
    blockStateModelGenerator.blockStateOutput.accept(road.composeState(BlockModelGenerators.createRotatedVariant(road, modelId)));
  }

  @Override
  public void appendDescriptionTooltip(List<Component> tooltip, TooltipFlag options) {

  }

  @Override
  public RecipeBuilder getCraftingRecipe() {
    if (lineColor != LineColor.NONE) return null;
    return ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, this, 9)
        .pattern("***")
        .pattern("|X|")
        .pattern("***")
        .define('*', ItemTags.COALS)
        .define('|', Items.FLINT)
        .define('X', Ingredient.of(Items.WHITE_CONCRETE, Items.GRAY_CONCRETE, Items.LIGHT_GRAY_CONCRETE, Items.BLACK_CONCRETE))
        .unlockedBy("has_coal", RecipeProvider.has(ItemTags.COALS))
        .unlockedBy(RecipeProvider.getHasName(Items.FLINT), RecipeProvider.has(Items.FLINT))
        .unlockedBy("has_proper_concrete", RecipeProvider.inventoryTrigger(ItemPredicate.Builder.item().of(Items.WHITE_CONCRETE, Items.GRAY_CONCRETE, Items.LIGHT_GRAY_CONCRETE, Items.BLACK_CONCRETE).build()));
  }

  @Override
  public RecipeBuilder getPaintingRecipe(Block base, Block self) {
    if (lineColor == LineColor.NONE) return null;
    return ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, self)
        .pattern("***")
        .pattern(" X ")
        .define('*', lineColor.getIngredient())
        .define('X', base)
        .unlockedBy("has_paint", RecipeProvider.has(lineColor.getIngredient()))
        .unlockedBy(RecipeProvider.getHasName(base), RecipeProvider.has(base));
  }
}
