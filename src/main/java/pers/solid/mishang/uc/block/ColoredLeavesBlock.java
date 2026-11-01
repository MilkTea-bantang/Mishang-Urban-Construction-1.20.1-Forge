package pers.solid.mishang.uc.block;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import pers.solid.mishang.uc.blockentity.SimpleColoredBlockEntity;

import java.util.List;
import java.util.function.BiFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ModelProvider;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;

@ApiStatus.AvailableSince("0.2.4")
public class ColoredLeavesBlock extends LeavesBlock implements ColoredBlock {
  private final BiFunction<Block, BlockLootSubProvider, LootTable.Builder> lootBuilder;
  private final ResourceLocation texture;

  public ColoredLeavesBlock(Properties settings, @Nullable BiFunction<Block, BlockLootSubProvider, LootTable.Builder> lootBuilder, ResourceLocation texture) {
    super(settings);
    this.lootBuilder = lootBuilder;
    this.texture = texture;
  }

  public ColoredLeavesBlock(Properties settings, @Nullable BiFunction<Block, BlockLootSubProvider, LootTable.Builder> lootBuilder, String texture) {
    this(settings, lootBuilder, new ResourceLocation(texture));
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
  public void registerModels(ModelProvider modelProvider, BlockModelGenerators blockStateModelGenerator) {
    final ResourceLocation modelId = ModelTemplates.LEAVES.create(this, TextureMapping.cube(texture), blockStateModelGenerator.modelOutput);
    blockStateModelGenerator.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(this, modelId));
    blockStateModelGenerator.delegateItemModel(this, modelId);
  }


  @Override
  public LootTable.Builder getLootTable(BlockLootSubProvider blockLootTableGenerator) {
    if (lootBuilder == null) return null;
    return (lootBuilder.apply(this, blockLootTableGenerator).apply(COPY_COLOR_LOOT_FUNCTION));
  }
}
