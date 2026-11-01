package pers.solid.mishang.uc.data;

import pers.solid.mishang.uc.shim.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import pers.solid.mishang.uc.shim.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import pers.solid.mishang.uc.MishangUtils;
import pers.solid.mishang.uc.block.MishangucBlock;

public class MishangucBlockLootTableProvider extends FabricBlockLootTableProvider {
  protected MishangucBlockLootTableProvider(FabricDataOutput dataOutput) {
    super(dataOutput);
  }

  @Override
  public void generate() {
    for (Block block : MishangUtils.blocks()) {
      if (block instanceof MishangucBlock r) {
        final ResourceLocation lootTableId = block.getLootTable();
        if (BuiltInLootTables.EMPTY.equals(lootTableId)) {
          continue;
        }
        final LootTable.Builder lootTable = r.getLootTable(this);
        map.put(lootTableId, lootTable);
      } else {
        throw new IllegalStateException();
      }
    }
  }
}
