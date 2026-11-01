package pers.solid.mishang.uc.block;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.solid.mishang.uc.blockentity.SimpleColoredBlockEntity;
import pers.solid.mishang.uc.data.MishangucModels;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ModelProvider;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;

public class ColoredStairsBlock extends StairBlock implements ColoredBlock {
  public final @NotNull Block baseBlock;

  public ColoredStairsBlock(@NotNull Block baseBlock, Properties settings) {
    super(baseBlock.defaultBlockState(), settings);
    this.baseBlock = baseBlock;
  }

  public ColoredStairsBlock(@NotNull Block baseBlock) {
    this(baseBlock, Properties.copy(baseBlock));
  }

  @Override
  public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
    return getColoredPickStack(world, pos, state, super::getCloneItemStack);
  }

  @Override
  public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag options) {
    super.appendHoverText(stack, world, tooltip, options);
    ColoredBlock.appendColorTooltip(stack, tooltip);
  }

  @NotNull
  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new SimpleColoredBlockEntity(pos, state);
  }

  @Override
  public void registerModels(ModelProvider modelProvider, BlockModelGenerators blockStateModelGenerator) {
    final TextureMapping textureMap = baseBlock instanceof ColoredCubeBlock coloredCubeBlock ? coloredCubeBlock.textures : TextureMapping.cube(this);
    final ResourceLocation regularModelId = MishangucModels.COLORED_STAIRS.create(this, textureMap, blockStateModelGenerator.modelOutput);
    final ResourceLocation innerModelId = MishangucModels.COLORED_INNER_STAIRS.create(this, textureMap, blockStateModelGenerator.modelOutput);
    final ResourceLocation outerModelId = MishangucModels.COLORED_OUTER_STAIRS.create(this, textureMap, blockStateModelGenerator.modelOutput);
    blockStateModelGenerator.blockStateOutput.accept(BlockModelGenerators.createStairs(this, innerModelId, regularModelId, outerModelId));
    blockStateModelGenerator.delegateItemModel(this, regularModelId);
  }

  @Override
  public LootTable.Builder getLootTable(BlockLootSubProvider blockLootTableGenerator) {
    return blockLootTableGenerator.createSingleItemTable(this).apply(COPY_COLOR_LOOT_FUNCTION);
  }

  @Override
  public RecipeBuilder getCraftingRecipe() {
    return ((ShapedRecipeBuilder) RecipeProvider.stairBuilder(this, Ingredient.of(baseBlock)))
        .unlockedBy(RecipeProvider.getHasName(baseBlock), RecipeProvider.has(baseBlock));
  }
}
