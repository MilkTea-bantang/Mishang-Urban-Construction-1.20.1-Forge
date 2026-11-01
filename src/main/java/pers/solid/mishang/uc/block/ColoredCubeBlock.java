package pers.solid.mishang.uc.block;

import net.minecraft.core.BlockPos;
import net.minecraft.data.models.model.*;
import net.minecraft.data.models.blockstates.*;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ModelProvider;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import pers.solid.mishang.uc.blockentity.SimpleColoredBlockEntity;
import pers.solid.mishang.uc.blocks.ColoredBlocks;
import pers.solid.mishang.uc.data.MishangucModels;

import java.util.List;

public class ColoredCubeBlock extends Block implements ColoredBlock {
  protected final ModelTemplate model;
  protected final TextureMapping textures;
  public static final ModelTemplate COLORED_CUBE_ALL = MishangucModels.createBlock("colored_cube_all", TextureSlot.ALL);
  public static final ModelTemplate COLORED_CUBE_BOTTOM_TOP = MishangucModels.createBlock("colored_cube_bottom_top", TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.SIDE);
  public static final ModelTemplate COLORED_CUBE_MIRRORED_ALL = MishangucModels.createBlock("colored_cube_mirrored_all", "_mirrored", TextureSlot.ALL);
  public static final ModelTemplate COLORED_CUBE_ALL_WITHOUT_SHADE = MishangucModels.createBlock("colored_cube_all_without_shade", TextureSlot.ALL);

  @ApiStatus.Internal
  public ColoredCubeBlock(Properties settings, ModelTemplate model, TextureMapping textures) {
    super(settings);
    this.model = model;
    this.textures = textures;
  }

  public static ColoredCubeBlock cubeAll(Properties settings, String allTexture) {
    return new ColoredCubeBlock(settings, COLORED_CUBE_ALL, TextureMapping.cube(new ResourceLocation(allTexture)));
  }

  public static ColoredCubeBlock cubeBottomTop(Properties settings, String topTexture, String sideTexture, String bottomTexture) {
    return new ColoredCubeBlock(settings, COLORED_CUBE_BOTTOM_TOP, TextureMapping.singleSlot(TextureSlot.TOP, new ResourceLocation(topTexture)).put(TextureSlot.SIDE, new ResourceLocation(sideTexture)).put(TextureSlot.BOTTOM, new ResourceLocation(bottomTexture)));
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

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new SimpleColoredBlockEntity(pos, state);
  }

  @Override
  public LootTable.Builder getLootTable(BlockLootSubProvider blockLootTableGenerator) {
    if (this == ColoredBlocks.COLORED_PACKED_ICE) {
      return BlockLootSubProvider.createSilkTouchOnlyTable(this).apply(COPY_COLOR_LOOT_FUNCTION);
    } else if (this == ColoredBlocks.COLORED_STONE) {
      return blockLootTableGenerator.createSingleItemTableWithSilkTouch(this, ColoredBlocks.COLORED_COBBLESTONE).apply(COPY_COLOR_LOOT_FUNCTION);
    }
    return blockLootTableGenerator.createSingleItemTable(this).apply(COPY_COLOR_LOOT_FUNCTION);
  }

  @Override
  public void registerModels(ModelProvider modelProvider, BlockModelGenerators blockStateModelGenerator) {
    final ResourceLocation modelId;
    if (this == ColoredBlocks.COLORED_STONE) {
      modelId = ColoredCubeBlock.COLORED_CUBE_ALL.create(this, textures, blockStateModelGenerator.modelOutput);
      final ResourceLocation mirroredModelId = ColoredCubeBlock.COLORED_CUBE_MIRRORED_ALL.create(this, textures, blockStateModelGenerator.modelOutput);

      blockStateModelGenerator.blockStateOutput.accept(BlockModelGenerators.createRotatedVariant(this, modelId, mirroredModelId));
    } else {
      modelId = model.create(this, textures, blockStateModelGenerator.modelOutput);
      blockStateModelGenerator.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(this, modelId));
    }
    blockStateModelGenerator.delegateItemModel(this, modelId);
  }

  @Override
  public ResourceLocation getTexture(TextureSlot key) {
    return textures.get(key);
  }
}
