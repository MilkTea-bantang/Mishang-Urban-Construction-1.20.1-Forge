package pers.solid.mishang.uc.block;

import pers.solid.mishang.uc.MishangUtils;
import pers.solid.mishang.uc.data.FasterTextureMap;
import pers.solid.mishang.uc.data.MishangucTextureKeys;
import pers.solid.mishang.uc.util.*;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface RoadWithCrossLine extends Road {
  @Override
  default RoadConnectionState getConnectionStateOf(BlockState state, Direction direction) {
    return Road.super
        .getConnectionStateOf(state, direction)
        .or(new RoadConnectionState(RoadConnectionState.WhetherConnected.CONNECTED, getLineColor(state, direction), EightHorizontalDirection.of(direction), LineType.NORMAL));
  }

  class Impl extends AbstractRoadBlock implements RoadWithCrossLine {
    public Impl(Properties settings, LineColor lineColor) {
      super(settings, lineColor, LineType.NORMAL);
    }

    @Override
    public void appendDescriptionTooltip(List<Component> tooltip, TooltipFlag options) {
      tooltip.add(TextBridge.translatable("lineType.cross.composed", lineColor.getName(), lineType.getName()).withStyle(ChatFormatting.BLUE));
    }

    @Override
    protected <B extends Block & Road> void registerBaseOrSlabModels(B road, BlockModelGenerators blockStateModelGenerator) {
      final FasterTextureMap textures = new FasterTextureMap().base("asphalt")
          .lineSide(MishangUtils.composeStraightLineTexture(lineColor, LineType.NORMAL))
          .lineTop(lineColor.getSerializedName() + "_cross_line");
      final ResourceLocation modelId = road.uploadModel("_with_cross_line", textures, blockStateModelGenerator, MishangucTextureKeys.BASE, MishangucTextureKeys.LINE_SIDE, MishangucTextureKeys.LINE_TOP);
      blockStateModelGenerator.blockStateOutput.accept(road.composeState(BlockModelGenerators.createRotatedVariant(road, modelId)));
    }


    @Override
    public RecipeBuilder getPaintingRecipe(Block base, Block self) {
      return ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, self, 4)
          .pattern("*X*")
          .pattern("X*X")
          .pattern("*X*")
          .define('*', lineColor.getIngredient())
          .define('X', base)
          .unlockedBy("has_ingredient", RecipeProvider.has(lineColor.getIngredient()))
          .unlockedBy(RecipeProvider.getHasName(base), RecipeProvider.has(base));
    }
  }
}
