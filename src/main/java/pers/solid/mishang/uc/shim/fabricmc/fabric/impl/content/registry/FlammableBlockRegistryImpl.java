package pers.solid.mishang.uc.shim.fabricmc.fabric.impl.content.registry;

import java.util.HashMap;
import java.util.Map;
import pers.solid.mishang.uc.shim.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;

public final class FlammableBlockRegistryImpl implements FlammableBlockRegistry {
  private static final Map<Block, FlammableBlockRegistryImpl> REGISTRIES = new HashMap<>();

  private FlammableBlockRegistryImpl() {
  }

  public static FlammableBlockRegistryImpl getInstance(Block block) {
    if (!(block instanceof FireBlock)) {
      throw new IllegalArgumentException("Not a fire block: " + block);
    }

    return REGISTRIES.computeIfAbsent(block, key -> new FlammableBlockRegistryImpl());
  }

  @Override
  public void add(Block block, FlammableBlockRegistry.Entry value) {
    ((FireBlock) Blocks.FIRE).setFlammable(block, value.getBurnChance(), value.getSpreadChance());
  }
}
