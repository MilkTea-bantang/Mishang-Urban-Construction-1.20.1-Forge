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
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;

public class ColoredSlabBlock extends SlabBlock implements ColoredBlock {
  public final Block baseBlock;

  public ColoredSlabBlock(@Nullable Block baseBlock, Properties settings) {
    super(settings);
    this.baseBlock = baseBlock;
  }

  public ColoredSlabBlock(@NotNull Block baseBlock) {
    super(Properties.copy(baseBlock));
    this.baseBlock = baseBlock;
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
    final TextureMapping textures = baseBlock instanceof ColoredCubeBlock coloredCubeBlock ? coloredCubeBlock.textures : TextureMapping.cube(this);
    final ResourceLocation bottomModelId = MishangucModels.COLORED_SLAB.create(this, textures, blockStateModelGenerator.modelOutput);
    final ResourceLocation topModelId = MishangucModels.COLORED_SLAB_TOP.create(this, textures, blockStateModelGenerator.modelOutput);
    final ResourceLocation fullModelId;
    if (baseBlock == null) {
      fullModelId = MishangucModels.COLORED_CUBE_BOTTOM_UP.create(this, textures, blockStateModelGenerator.modelOutput);
    } else {
      fullModelId = ModelLocationUtils.getModelLocation(baseBlock);
    }

    blockStateModelGenerator.blockStateOutput.accept(BlockModelGenerators.createSlab(this, bottomModelId, topModelId, fullModelId));
    blockStateModelGenerator.delegateItemModel(this, bottomModelId);
  }

  @Override
  public LootTable.Builder getLootTable(BlockLootSubProvider blockLootTableGenerator) {
    return blockLootTableGenerator.createSlabItemTable(this).apply(COPY_COLOR_LOOT_FUNCTION);
  }

  @Override
  public RecipeBuilder getCraftingRecipe() {
    return ((ShapedRecipeBuilder) RecipeProvider.slabBuilder(RecipeCategory.BUILDING_BLOCKS, this, Ingredient.of(baseBlock)))
        .unlockedBy(RecipeProvider.getHasName(this.baseBlock), RecipeProvider.has(this.baseBlock));
  }
}
