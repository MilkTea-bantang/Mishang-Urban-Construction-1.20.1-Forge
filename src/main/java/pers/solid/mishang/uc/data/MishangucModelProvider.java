package pers.solid.mishang.uc.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import pers.solid.mishang.uc.shim.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import pers.solid.mishang.uc.shim.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.model.*;
import net.minecraft.data.models.blockstates.*;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.world.item.Item;
import pers.solid.mishang.uc.MishangUtils;
import pers.solid.mishang.uc.block.MishangucBlock;
import pers.solid.mishang.uc.blocks.WallSignBlocks;
import pers.solid.mishang.uc.item.MishangucItems;

public class MishangucModelProvider extends FabricModelProvider {
  public MishangucModelProvider(FabricDataOutput output) {
    super(output);
  }

  @Override
  public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
    MishangUtils.blocks().forEach(block -> {
      if (block instanceof MishangucBlock mishangucBlock) {
        mishangucBlock.registerModels(this, blockStateModelGenerator);
      }
    });
  }

  @Override
  public void generateItemModels(ItemModelGenerators itemModelGenerator) {
    itemModelGenerator.generateFlatItem(WallSignBlocks.INVISIBLE_WALL_SIGN.asItem(), ModelTemplates.FLAT_HANDHELD_ITEM);
    itemModelGenerator.generateFlatItem(WallSignBlocks.INVISIBLE_GLOWING_WALL_SIGN.asItem(), ModelTemplates.FLAT_HANDHELD_ITEM);

    itemModelGenerator.generateFlatItem(MishangucItems.BLOCK_STATE_TOOL, ModelTemplates.FLAT_HANDHELD_ITEM);
    itemModelGenerator.generateFlatItem(MishangucItems.FLUID_STATE_TOOL, ModelTemplates.FLAT_HANDHELD_ITEM);
    registerCarryingTool(itemModelGenerator, MishangucItems.CARRYING_TOOL);
    itemModelGenerator.generateFlatItem(MishangucItems.COLOR_TOOL, ModelTemplates.FLAT_HANDHELD_ITEM);
    itemModelGenerator.generateFlatItem(MishangucItems.COLUMN_BUILDING_TOOL, ModelTemplates.FLAT_HANDHELD_ITEM);
    itemModelGenerator.generateFlatItem(MishangucItems.DATA_TAG_TOOL, ModelTemplates.FLAT_HANDHELD_ITEM);
    registerExplosionToolVariants(itemModelGenerator, MishangucItems.EXPLOSION_TOOL);
    itemModelGenerator.generateFlatItem(MishangucItems.FORCE_PLACING_TOOL, ModelTemplates.FLAT_HANDHELD_ITEM);
    itemModelGenerator.generateFlatItem(MishangucItems.FLUID_FORCE_PLACING_TOOL, ModelTemplates.FLAT_HANDHELD_ITEM);
    itemModelGenerator.generateFlatItem(MishangucItems.GROWTH_TOOL, ModelTemplates.FLAT_HANDHELD_ITEM);
    itemModelGenerator.generateFlatItem(MishangucItems.ICE_SNOW_TOOL, ModelTemplates.FLAT_HANDHELD_ITEM);
    itemModelGenerator.generateFlatItem(MishangucItems.ID_CHECKER_TOOL, ModelTemplates.FLAT_HANDHELD_ITEM);
    itemModelGenerator.generateFlatItem(MishangucItems.FLUID_ID_CHECKER_TOOL, ModelTemplates.FLAT_HANDHELD_ITEM);
    itemModelGenerator.generateFlatItem(MishangucItems.MIRRORING_TOOL, ModelTemplates.FLAT_HANDHELD_ITEM);
    itemModelGenerator.generateFlatItem(MishangucItems.OMNIPOTENT_TOOL, ModelTemplates.FLAT_HANDHELD_ITEM);
    itemModelGenerator.generateFlatItem(MishangucItems.ROAD_CONNECTION_STATE_DEBUGGING_TOOL, ModelTemplates.FLAT_HANDHELD_ITEM);
    itemModelGenerator.generateFlatItem(MishangucItems.ROAD_TOOL, ModelTemplates.FLAT_HANDHELD_ITEM);
    itemModelGenerator.generateFlatItem(MishangucItems.ROTATING_TOOL, ModelTemplates.FLAT_HANDHELD_ITEM);
    itemModelGenerator.generateFlatItem(MishangucItems.SLAB_TOOL, ModelTemplates.FLAT_HANDHELD_ITEM);
    itemModelGenerator.generateFlatItem(MishangucItems.TEXT_COPY_TOOL, ModelTemplates.FLAT_HANDHELD_ITEM);
    itemModelGenerator.generateFlatItem(MishangucItems.TP_TOOL, ModelTemplates.FLAT_HANDHELD_ITEM);
  }

  private void registerCarryingTool(ItemModelGenerators itemModelGenerator, Item item) {
    ModelTemplates.FLAT_HANDHELD_ITEM.create(ModelLocationUtils.getModelLocation(item), TextureMapping.layer0(item), itemModelGenerator.output, (id, textures) -> {
      final JsonObject json = ModelTemplates.FLAT_HANDHELD_ITEM.createBaseTemplate(id, textures);
      final JsonArray overrides = new JsonArray();
      json.add("overrides", overrides);
      final JsonObject withBlock = new JsonObject();
      overrides.add(withBlock);
      final JsonObject withBlockPredicate = new JsonObject();
      withBlock.add("predicate", withBlockPredicate);
      withBlockPredicate.addProperty("mishanguc:is_holding_block", 1f);
      withBlock.addProperty("model", ModelLocationUtils.getModelLocation(item, "_with_block").toString());
      final JsonObject withEntity = new JsonObject();
      overrides.add(withEntity);
      final JsonObject withEntityPredicate = new JsonObject();
      withEntity.add("predicate", withEntityPredicate);
      withEntityPredicate.addProperty("mishanguc:is_holding_entity", 1f);
      withEntity.addProperty("model", ModelLocationUtils.getModelLocation(item, "_with_entity").toString());
      return json;
    });
    itemModelGenerator.generateFlatItem(item, "_with_block", ModelTemplates.FLAT_HANDHELD_ITEM);
    itemModelGenerator.generateFlatItem(item, "_with_entity", ModelTemplates.FLAT_HANDHELD_ITEM);
  }

  private void registerExplosionToolVariants(ItemModelGenerators itemModelGenerator, Item item) {
    for (final String name : new String[]{
        "_fire",
        "_4", "_4_fire",
        "_8", "_8_fire",
        "_16", "_16_fire",
        "_32", "_32_fire",
        "_64", "_64_fire",
        "_128", "_128_fire",
    }) {
      itemModelGenerator.generateFlatItem(item, name, ModelTemplates.FLAT_HANDHELD_ITEM);
    }
  }
}
