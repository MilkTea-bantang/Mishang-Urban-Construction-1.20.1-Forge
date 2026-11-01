package pers.solid.mishang.uc.data;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import pers.solid.mishang.uc.shim.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import pers.solid.mishang.uc.shim.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import pers.solid.mishang.uc.shim.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.data.server.recipe.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;
import pers.solid.mishang.uc.MishangUtils;
import pers.solid.mishang.uc.Mishanguc;
import pers.solid.mishang.uc.block.AbstractRoadSlabBlock;
import pers.solid.mishang.uc.block.GlassHandrailBlock;
import pers.solid.mishang.uc.block.MishangucBlock;
import pers.solid.mishang.uc.blocks.*;
import pers.solid.mishang.uc.item.MishangucItem;

import java.util.List;
import java.util.function.Consumer;

/**
 * @since 0.1.7 本类应当在 onInitialize 的入口点中执行，而非 pregen 中。
 */
public class MishangucRecipeProvider extends FabricRecipeProvider {

  public MishangucRecipeProvider(FabricDataOutput output) {
    super(output);
  }

  @Override
  public void buildRecipes(Consumer<FinishedRecipe> exporter) {
    addRegularRecipes(exporter);
    addSpecialRecipes(exporter);
  }

  private static void addRegularRecipes(Consumer<FinishedRecipe> exporter) {
    for (Block block : MishangUtils.blocks()) {
      if (block instanceof MishangucBlock r) {
        r.writeRecipes(exporter);
      } else {
        throw new IllegalStateException();
      }
    }
    for (Item item : MishangUtils.items()) {
      if (item instanceof MishangucItem i) {
        final RecipeBuilder craftingRecipe = i.getCraftingRecipe();
        if (craftingRecipe != null) {
          craftingRecipe.save(exporter);
        }
      }
    }
  }


  /**
   * 生成模组的部分配方。
   */
  public static void addSpecialRecipes(Consumer<FinishedRecipe> exporter) {
    addGlassHandrailsRecipes(exporter);
    addRecipesForInvisibleSigns(exporter);
    addRoadPalingRecipes(exporter);
  }

  private static void addGlassHandrailsRecipes(Consumer<FinishedRecipe> exporter) {
    addRecipeForGlassHandrail(exporter, HandrailBlocks.COLORED_DECORATED_STONE_HANDRAIL, Items.STONE, ColoredBlocks.COLORED_CONCRETE, Items.STONE, 6, null);
    addRecipeForGlassHandrail(exporter, HandrailBlocks.COLORED_DECORATED_COBBLESTONE_HANDRAIL, Items.COBBLESTONE, ColoredBlocks.COLORED_CONCRETE, Items.COBBLESTONE, 6, null);
    addRecipeForGlassHandrail(exporter, HandrailBlocks.COLORED_DECORATED_MOSSY_COBBLESTONE_HANDRAIL, Items.MOSSY_COBBLESTONE, ColoredBlocks.COLORED_CONCRETE, Items.MOSSY_COBBLESTONE, 6, null);

    HandrailBlocks.DECORATED_IRON_HANDRAILS.forEach((dyeColor, glassHandrailBlock) -> {
      final TagKey<Item> dyeKey = TagKey.create(Registries.ITEM, new ResourceLocation("c", "dyes/" + dyeColor.getSerializedName()));
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, glassHandrailBlock, 4)
          .pattern("XXX")
          .pattern("oMo")
          .pattern("nnn")
          .define('X', ConventionalItemTags.IRON_INGOTS)
          .define('o', Items.GLASS_PANE)
          .define('M', dyeKey)
          .define('n', Items.IRON_NUGGET)
          .unlockedBy("has_iron_ingot", RecipeProvider.has(ConventionalItemTags.IRON_INGOTS))
          .unlockedBy(RecipeProvider.getHasName(Items.GLASS_PANE), RecipeProvider.has(Items.GLASS_PANE))
          .unlockedBy("has_dye", RecipeProvider.has(dyeKey))
          .unlockedBy(RecipeProvider.getHasName(Items.IRON_NUGGET), RecipeProvider.has(Items.IRON_NUGGET))
          .group("mishanguc:decorated_iron_handrail")
          .save(exporter);
    });

    addRecipeForGlassHandrail(exporter, HandrailBlocks.COLORED_DECORATED_IRON_HANDRAIL, ConventionalItemTags.IRON_INGOTS, "has_iron_ingot", ColoredBlocks.COLORED_CONCRETE, Items.IRON_NUGGET, 4);
    addRecipeForGlassHandrail(exporter, HandrailBlocks.COLORED_DECORATED_GOLD_HANDRAIL, ConventionalItemTags.GOLD_INGOTS, "has_gold_ingot", ColoredBlocks.COLORED_CONCRETE, Items.GOLD_NUGGET, 4);
    addRecipeForGlassHandrail(exporter, HandrailBlocks.COLORED_DECORATED_EMERALD_HANDRAIL, ConventionalItemTags.EMERALDS, "has_emerald", ColoredBlocks.COLORED_CONCRETE, 4);
    addRecipeForGlassHandrail(exporter, HandrailBlocks.COLORED_DECORATED_DIAMOND_HANDRAIL, ConventionalItemTags.DIAMONDS, "has_diamond", ColoredBlocks.COLORED_CONCRETE, 4);
    addRecipeForGlassHandrail(exporter, HandrailBlocks.COLORED_DECORATED_NETHERITE_HANDRAIL, ConventionalItemTags.NETHERITE_INGOTS, "has_netherite_ingot", ColoredBlocks.COLORED_CONCRETE, 4);
    addRecipeForGlassHandrail(exporter, HandrailBlocks.COLORED_DECORATED_LAPIS_HANDRAIL, ConventionalItemTags.LAPIS, "has_lapis", ColoredBlocks.COLORED_CONCRETE, 4);

    addRecipeForGlassHandrail(exporter, HandrailBlocks.GLOWING_COLORED_DECORATED_IRON_HANDRAIL, ConventionalItemTags.IRON_INGOTS, "has_iron_ingot", ColoredBlocks.COLORED_LIGHT, Items.IRON_NUGGET, 4);
    addRecipeForGlassHandrail(exporter, HandrailBlocks.GLOWING_COLORED_DECORATED_GOLD_HANDRAIL, ConventionalItemTags.GOLD_INGOTS, "has_gold_ingot", ColoredBlocks.COLORED_LIGHT, Items.GOLD_NUGGET, 4);
    addRecipeForGlassHandrail(exporter, HandrailBlocks.GLOWING_COLORED_DECORATED_EMERALD_HANDRAIL, ConventionalItemTags.EMERALDS, "has_emerald", ColoredBlocks.COLORED_LIGHT, 4);
    addRecipeForGlassHandrail(exporter, HandrailBlocks.GLOWING_COLORED_DECORATED_DIAMOND_HANDRAIL, ConventionalItemTags.DIAMONDS, "has_diamond", ColoredBlocks.COLORED_LIGHT, 4);
    addRecipeForGlassHandrail(exporter, HandrailBlocks.GLOWING_COLORED_DECORATED_NETHERITE_HANDRAIL, ConventionalItemTags.NETHERITE_INGOTS, "has_netherite_ingot", ColoredBlocks.COLORED_LIGHT, 4);
    addRecipeForGlassHandrail(exporter, HandrailBlocks.GLOWING_COLORED_DECORATED_LAPIS_HANDRAIL, ConventionalItemTags.LAPIS, "has_lapis", ColoredBlocks.COLORED_LIGHT, 4);

    addRecipeForGlassHandrail(exporter, HandrailBlocks.SNOW_DECORATED_PACKED_ICE_HANDRAIL, Items.PACKED_ICE, Blocks.SNOW_BLOCK, Items.PACKED_ICE, 6, null);
    addRecipeForGlassHandrail(exporter, HandrailBlocks.SNOW_DECORATED_BLUE_ICE_HANDRAIL, Items.PACKED_ICE, Blocks.SNOW_BLOCK, Items.BLUE_ICE, 6, null);
    addRecipeForGlassHandrail(exporter, HandrailBlocks.COLORED_DECORATED_PACKED_ICE_HANDRAIL, Items.PACKED_ICE, ColoredBlocks.COLORED_SNOW_BLOCK, Items.PACKED_ICE, 6, null);
    addRecipeForGlassHandrail(exporter, HandrailBlocks.COLORED_DECORATED_BLUE_ICE_HANDRAIL, Items.PACKED_ICE, ColoredBlocks.COLORED_SNOW_BLOCK, Items.BLUE_ICE, 6, null);

    for (GlassHandrailBlock output : List.of(
        HandrailBlocks.GLASS_OAK_HANDRAIL,
        HandrailBlocks.GLASS_SPRUCE_HANDRAIL,
        HandrailBlocks.GLASS_BIRCH_HANDRAIL,
        HandrailBlocks.GLASS_JUNGLE_HANDRAIL,
        HandrailBlocks.GLASS_ACACIA_HANDRAIL,
        HandrailBlocks.GLASS_CHERRY_HANDRAIL,
        HandrailBlocks.GLASS_DARK_OAK_HANDRAIL,
        HandrailBlocks.GLASS_MANGROVE_HANDRAIL,
        HandrailBlocks.GLASS_CRIMSON_HANDRAIL,
        HandrailBlocks.GLASS_WARPED_HANDRAIL)) {
      final Item wood = output.baseBlock().asItem();
      final ResourceLocation woodId = BuiltInRegistries.ITEM.getKey(wood);
      final Item planks = BuiltInRegistries.ITEM.get(woodId.withPath(woodId.getPath().replace("wood", "planks").replace("hyphae", "planks")));
      Preconditions.checkState(wood != planks);
      addRecipeForGlassHandrail(exporter, output, wood, planks, Items.STICK, 6, "glass_wooden_handrail");
    }
    for (GlassHandrailBlock output : ImmutableSet.of(
        HandrailBlocks.COLORED_DECORATED_OAK_HANDRAIL,
        HandrailBlocks.COLORED_DECORATED_SPRUCE_HANDRAIL,
        HandrailBlocks.COLORED_DECORATED_BIRCH_HANDRAIL,
        HandrailBlocks.COLORED_DECORATED_JUNGLE_HANDRAIL,
        HandrailBlocks.COLORED_DECORATED_ACACIA_HANDRAIL,
        HandrailBlocks.COLORED_DECORATED_CHERRY_HANDRAIL,
        HandrailBlocks.COLORED_DECORATED_DARK_OAK_HANDRAIL,
        HandrailBlocks.COLORED_DECORATED_MANGROVE_HANDRAIL,
        HandrailBlocks.COLORED_DECORATED_CRIMSON_HANDRAIL,
        HandrailBlocks.COLORED_DECORATED_WARPED_HANDRAIL,
        HandrailBlocks.COLORED_DECORATED_BAMBOO_HANDRAIL,
        HandrailBlocks.COLORED_DECORATED_STRIPPED_OAK_HANDRAIL,
        HandrailBlocks.COLORED_DECORATED_STRIPPED_SPRUCE_HANDRAIL,
        HandrailBlocks.COLORED_DECORATED_STRIPPED_BIRCH_HANDRAIL,
        HandrailBlocks.COLORED_DECORATED_STRIPPED_JUNGLE_HANDRAIL,
        HandrailBlocks.COLORED_DECORATED_STRIPPED_ACACIA_HANDRAIL,
        HandrailBlocks.COLORED_DECORATED_STRIPPED_CHERRY_HANDRAIL,
        HandrailBlocks.COLORED_DECORATED_STRIPPED_DARK_OAK_HANDRAIL,
        HandrailBlocks.COLORED_DECORATED_STRIPPED_MANGROVE_HANDRAIL,
        HandrailBlocks.COLORED_DECORATED_STRIPPED_CRIMSON_HANDRAIL,
        HandrailBlocks.COLORED_DECORATED_STRIPPED_WARPED_HANDRAIL,
        HandrailBlocks.COLORED_DECORATED_STRIPPED_BAMBOO_HANDRAIL
    )) {
      addRecipeForGlassHandrail(exporter, output, output.baseBlock(), ColoredBlocks.COLORED_PLANKS, Items.STICK, 6, "colored_decorated_wooden_handrail");
    }

    addRecipeForGlassHandrail(exporter, HandrailBlocks.GLASS_BAMBOO_HANDRAIL, Items.BAMBOO_BLOCK, Items.BAMBOO_PLANKS, Items.BAMBOO, 6, null);

    addRecipeForGlassHandrail(exporter, HandrailBlocks.NETHERRACK_DECORATED_OBSIDIAN_HANDRAIL, Items.OBSIDIAN, Items.NETHERRACK, Items.OBSIDIAN, 8, null);
    addRecipeForGlassHandrail(exporter, HandrailBlocks.NETHERRACK_DECORATED_CRYING_OBSIDIAN_HANDRAIL, Items.CRYING_OBSIDIAN, Items.NETHERRACK, Items.CRYING_OBSIDIAN, 8, null);
    addRecipeForGlassHandrail(exporter, HandrailBlocks.SOUL_SOIL_DECORATED_OBSIDIAN_HANDRAIL, Items.OBSIDIAN, Items.SOUL_SOIL, Items.OBSIDIAN, 8, null);
    addRecipeForGlassHandrail(exporter, HandrailBlocks.SOUL_SOIL_DECORATED_CRYING_OBSIDIAN_HANDRAIL, Items.CRYING_OBSIDIAN, Items.SOUL_SOIL, Items.CRYING_OBSIDIAN, 8, null);
    addRecipeForGlassHandrail(exporter, HandrailBlocks.MAGMA_DECORATED_OBSIDIAN_HANDRAIL, Items.OBSIDIAN, Items.MAGMA_BLOCK, Items.OBSIDIAN, 8, null);
    addRecipeForGlassHandrail(exporter, HandrailBlocks.MAGMA_DECORATED_CRYING_OBSIDIAN_HANDRAIL, Items.CRYING_OBSIDIAN, Items.MAGMA_BLOCK, Items.CRYING_OBSIDIAN, 8, null);
    addRecipeForGlassHandrail(exporter, HandrailBlocks.COLORED_DECORATED_OBSIDIAN_HANDRAIL, Items.OBSIDIAN, ColoredBlocks.COLORED_CONCRETE, Items.OBSIDIAN, 8, null);
    addRecipeForGlassHandrail(exporter, HandrailBlocks.COLORED_DECORATED_CRYING_OBSIDIAN_HANDRAIL, Items.CRYING_OBSIDIAN, ColoredBlocks.COLORED_CONCRETE, Items.CRYING_OBSIDIAN, 8, null);
  }

  private static void addRecipeForGlassHandrail(Consumer<FinishedRecipe> exporter, GlassHandrailBlock output, ItemLike frame, ItemLike decoration, ItemLike base, int outputCount, @Nullable String group) {
    final ShapedRecipeBuilder r = ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, output, outputCount)
        .pattern("XXX")
        .pattern("oMo")
        .pattern("nnn")
        .define('X', frame)
        .define('o', Items.GLASS_PANE)
        .define('M', decoration)
        .define('n', base)
        .unlockedBy(RecipeProvider.getHasName(frame), RecipeProvider.has(frame))
        .unlockedBy(RecipeProvider.getHasName(Items.GLASS_PANE), RecipeProvider.has(Items.GLASS_PANE))
        .unlockedBy(RecipeProvider.getHasName(decoration), RecipeProvider.has(decoration));
    if (frame != base) {
      r.unlockedBy(RecipeProvider.getHasName(base), RecipeProvider.has(base));
    }
    r.group(group)
        .save(exporter);
  }

  private static void addRecipeForGlassHandrail(Consumer<FinishedRecipe> exporter, GlassHandrailBlock output, TagKey<Item> frame, String frameCriterionName, ItemLike decoration, ItemLike base, int outputCount) {
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, output, outputCount)
        .pattern("XXX")
        .pattern("oMo")
        .pattern("nnn")
        .define('X', frame)
        .define('o', Items.GLASS_PANE)
        .define('M', decoration)
        .define('n', base)
        .unlockedBy(frameCriterionName, RecipeProvider.has(frame))
        .unlockedBy(RecipeProvider.getHasName(Items.GLASS_PANE), RecipeProvider.has(Items.GLASS_PANE))
        .unlockedBy(RecipeProvider.getHasName(decoration), RecipeProvider.has(decoration))
        .unlockedBy(RecipeProvider.getHasName(base), RecipeProvider.has(base))
        .save(exporter);
  }

  private static void addRecipeForGlassHandrail(Consumer<FinishedRecipe> exporter, GlassHandrailBlock output, TagKey<Item> frame, String frameCriterionName, ItemLike decoration, int outputCount) {
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, output, outputCount)
        .pattern("XXX")
        .pattern("oMo")
        .pattern("XXX")
        .define('X', frame)
        .define('o', Items.GLASS_PANE)
        .define('M', decoration)
        .unlockedBy(frameCriterionName, RecipeProvider.has(frame))
        .unlockedBy(RecipeProvider.getHasName(Items.GLASS_PANE), RecipeProvider.has(Items.GLASS_PANE))
        .unlockedBy(RecipeProvider.getHasName(decoration), RecipeProvider.has(decoration))
        .save(exporter);
  }

  private static void addRecipesForInvisibleSigns(Consumer<FinishedRecipe> exporter) {
    // 隐形告示牌是合成其他告示牌的基础。
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, WallSignBlocks.INVISIBLE_WALL_SIGN, 9)
        .pattern(".#.")
        .pattern("#o#")
        .pattern(".#.")
        .define('.', Items.IRON_NUGGET)
        .define('#', Items.FEATHER)
        .define('o', Items.GOLD_INGOT)
        .unlockedBy("has_iron_nugget", RecipeProvider.has(Items.IRON_NUGGET))
        .unlockedBy("has_feather", RecipeProvider.has(Items.FEATHER))
        .unlockedBy("has_gold_ingot", RecipeProvider.has(Items.GOLD_INGOT))
        .save(exporter);
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, WallSignBlocks.INVISIBLE_GLOWING_WALL_SIGN, 3)
        .pattern("---")
        .pattern("###")
        .define('-', Items.GLOWSTONE_DUST)
        .define('#', WallSignBlocks.INVISIBLE_WALL_SIGN)
        .unlockedBy("has_base_block", RecipeProvider.has(WallSignBlocks.INVISIBLE_WALL_SIGN))
        .save(exporter);
  }

  private static void addRoadPalingRecipes(Consumer<FinishedRecipe> exporter) {
    // 将带有标线的道路重置为不带标线的道路。
    final TagKey<Item> roadBlocks = TagKey.create(Registries.ITEM, Mishanguc.id("road_blocks"));
    SingleItemRecipeBuilder.stonecutting(Ingredient.of(roadBlocks), RecipeCategory.BUILDING_BLOCKS, RoadBlocks.ROAD_BLOCK)
        .unlockedBy("has_road_block", RecipeProvider.has(roadBlocks))
        .save(exporter, RecipeBuilder.getDefaultRecipeId(RoadBlocks.ROAD_BLOCK).withSuffix("_from_paling"));
    final TagKey<Item> roadSlabs = TagKey.create(Registries.ITEM, Mishanguc.id("road_slabs"));
    final AbstractRoadSlabBlock roadSlabBlock = RoadSlabBlocks.BLOCK_TO_SLABS.get(RoadBlocks.ROAD_BLOCK);
    SingleItemRecipeBuilder.stonecutting(Ingredient.of(roadSlabs), RecipeCategory.BUILDING_BLOCKS, roadSlabBlock)
        .unlockedBy("has_road_slab", RecipeProvider.has(roadSlabs))
        .save(exporter, RecipeBuilder.getDefaultRecipeId(roadSlabBlock).withSuffix("_from_paling"));
  }

  public static @Nullable String getCustomRecipeCategory(Item outputItem) {
    if (outputItem instanceof BlockItem blockItem && blockItem.getBlock() instanceof MishangucBlock mishangucBlock) {
      return mishangucBlock.customRecipeCategory();
    } else if (outputItem instanceof MishangucItem mishangucItem) {
      return mishangucItem.customRecipeCategory();
    }
    return null;
  }
}
