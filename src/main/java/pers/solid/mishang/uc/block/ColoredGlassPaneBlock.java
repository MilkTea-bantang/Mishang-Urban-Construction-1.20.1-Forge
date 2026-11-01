package pers.solid.mishang.uc.block;

import net.minecraft.core.BlockPos;
import net.minecraft.data.models.model.*;
import net.minecraft.data.models.blockstates.*;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ModelProvider;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.blockstates.Condition;
import net.minecraft.data.models.blockstates.MultiPartGenerator;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.solid.mishang.uc.blockentity.SimpleColoredBlockEntity;
import pers.solid.mishang.uc.data.MishangucModels;

import java.util.List;

public class ColoredGlassPaneBlock extends IronBarsBlock implements ColoredBlock {
  private final ResourceLocation paneTexture;
  private final ResourceLocation edgeTexture;

  public ColoredGlassPaneBlock(ResourceLocation paneTexture, ResourceLocation edgeTexture, Properties settings) {
    super(settings);
    this.paneTexture = paneTexture;
    this.edgeTexture = edgeTexture;
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
    TextureMapping textures = TextureMapping.singleSlot(TextureSlot.PANE, paneTexture).put(TextureSlot.EDGE, edgeTexture);
    final ResourceLocation postId = MishangucModels.TEMPLATE_COLORED_GLASS_PANE_POST.create(this, textures, blockStateModelGenerator.modelOutput);
    final ResourceLocation sideId = MishangucModels.TEMPLATE_COLORED_GLASS_PANE_SIDE.create(this, textures, blockStateModelGenerator.modelOutput);
    final ResourceLocation SideAltId = MishangucModels.TEMPLATE_COLORED_GLASS_PANE_SIDE_ALT.create(this, textures, blockStateModelGenerator.modelOutput);
    final ResourceLocation nosideId = MishangucModels.TEMPLATE_COLORED_GLASS_PANE_NOSIDE.create(this, textures, blockStateModelGenerator.modelOutput);
    final ResourceLocation nosideAltId = MishangucModels.TEMPLATE_COLORED_GLASS_PANE_NOSIDE_ALT.create(this, textures, blockStateModelGenerator.modelOutput);

    blockStateModelGenerator.blockStateOutput.accept(createBlockStates(postId, sideId, SideAltId, nosideId, nosideAltId));
    ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(asItem()), TextureMapping.layer0(paneTexture), blockStateModelGenerator.modelOutput);
  }

  public @NotNull BlockStateGenerator createBlockStates(ResourceLocation postId, ResourceLocation sideId, ResourceLocation sideAltId, ResourceLocation nosideId, ResourceLocation nosideAltId) {
    return MultiPartGenerator.multiPart(this)
        .with(Variant.variant()
            .with(VariantProperties.MODEL, postId))
        .with(Condition.condition().term(BlockStateProperties.NORTH, true),
            Variant.variant()
                .with(VariantProperties.MODEL, sideId))
        .with(Condition.condition().term(BlockStateProperties.EAST, true),
            Variant.variant()
                .with(VariantProperties.MODEL, sideId)
                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
        .with(Condition.condition().term(BlockStateProperties.SOUTH, true),
            Variant.variant()
                .with(VariantProperties.MODEL, sideAltId))
        .with(Condition.condition().term(BlockStateProperties.WEST, true),
            Variant.variant()
                .with(VariantProperties.MODEL, sideAltId)
                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
        .with(Condition.condition().term(BlockStateProperties.NORTH, false),
            Variant.variant()
                .with(VariantProperties.MODEL, nosideId))
        .with(Condition.condition().term(BlockStateProperties.EAST, false),
            Variant.variant()
                .with(VariantProperties.MODEL, nosideAltId))
        .with(Condition.condition().term(BlockStateProperties.SOUTH, false),
            Variant.variant()
                .with(VariantProperties.MODEL, nosideAltId)
                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
        .with(Condition.condition().term(BlockStateProperties.WEST, false),
            Variant.variant()
                .with(VariantProperties.MODEL, nosideId)
                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270));
  }

  @Override
  public LootTable.@NotNull Builder getLootTable(BlockLootSubProvider blockLootTableGenerator) {
    return BlockLootSubProvider.createSilkTouchOnlyTable(this).apply(COPY_COLOR_LOOT_FUNCTION);
  }
}
