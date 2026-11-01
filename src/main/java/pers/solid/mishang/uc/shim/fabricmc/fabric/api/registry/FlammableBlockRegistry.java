package pers.solid.mishang.uc.shim.fabricmc.fabric.api.registry;

import pers.solid.mishang.uc.shim.fabricmc.fabric.impl.content.registry.FlammableBlockRegistryImpl;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/**
 * Forge shim for Fabric API's {@code FlammableBlockRegistry}.
 *
 * <p>Fabric stores the entries in its own map and mixes them into {@code FireBlock}. On Forge the
 * vanilla {@code FireBlock.setFlammable} method is opened up through an access transformer, so the
 * entries can be written straight into the vanilla odds maps and no mixin is needed.
 */
public interface FlammableBlockRegistry {
  static FlammableBlockRegistry getDefaultInstance() {
    return getInstance(Blocks.FIRE);
  }

  static FlammableBlockRegistry getInstance(Block block) {
    return FlammableBlockRegistryImpl.getInstance(block);
  }

  void add(Block block, Entry value);

  default void add(Block block, int burnChance, int spreadChance) {
    this.add(block, new Entry(burnChance, spreadChance));
  }

  final class Entry {
    private final int burnChance;
    private final int spreadChance;

    public Entry(int burnChance, int spreadChance) {
      this.burnChance = burnChance;
      this.spreadChance = spreadChance;
    }

    public int getBurnChance() {
      return this.burnChance;
    }

    public int getSpreadChance() {
      return this.spreadChance;
    }

    @Override
    public boolean equals(Object object) {
      if (this == object) {
        return true;
      }
      if (!(object instanceof Entry entry)) {
        return false;
      }
      return this.burnChance == entry.burnChance && this.spreadChance == entry.spreadChance;
    }

    @Override
    public int hashCode() {
      return this.burnChance * 11 + this.spreadChance;
    }

    @Override
    public String toString() {
      return "Entry{burnChance=" + this.burnChance + ", spreadChance=" + this.spreadChance + '}';
    }
  }
}
