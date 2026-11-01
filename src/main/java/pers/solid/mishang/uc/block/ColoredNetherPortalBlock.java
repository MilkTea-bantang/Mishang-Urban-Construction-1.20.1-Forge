package pers.solid.mishang.uc.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.models.model.*;
import net.minecraft.data.models.blockstates.*;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ModelProvider;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import pers.solid.mishang.uc.blockentity.SimpleColoredBlockEntity;

import java.util.List;

@ApiStatus.AvailableSince("1.0.2")
public class ColoredNetherPortalBlock extends NetherPortalBlock implements ColoredBlock {
  public ColoredNetherPortalBlock(Properties settings) {
    super(settings);
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext ctx) {
    final BlockState state = super.getStateForPlacement(ctx);
    return state == null ? null : state.setValue(AXIS, ctx.getHorizontalDirection().getClockWise().getAxis());
  }

  @Override
  public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
    return getColoredPickStack(world, pos, state, (blockView, pos1, state1) -> new ItemStack(this));
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
    final ResourceLocation ewId = ModelLocationUtils.getModelLocation(this, "_ew");
    final ResourceLocation nsId = ModelLocationUtils.getModelLocation(this, "_ns");
    blockStateModelGenerator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(this)
        .with(
            PropertyDispatch.property(BlockStateProperties.HORIZONTAL_AXIS)
                .select(Direction.Axis.X, Variant.variant().with(VariantProperties.MODEL, nsId))
                .select(Direction.Axis.Z, Variant.variant().with(VariantProperties.MODEL, ewId))));
    blockStateModelGenerator.delegateItemModel(this, nsId);
  }

  @Override
  public LootTable.Builder getLootTable(BlockLootSubProvider blockLootTableGenerator) {
    return LootTable.lootTable();
  }
}
