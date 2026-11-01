package pers.solid.mishang.uc.shim.fabricmc.fabric.api.registry;

import pers.solid.mishang.uc.shim.fabricmc.fabric.impl.content.registry.FuelRegistryImpl;
import net.minecraft.world.level.ItemLike;

/**
 * Forge shim for Fabric API's {@code FuelRegistry}.
 *
 * <p>Forge's {@code AbstractFurnaceBlockEntity.getFuel()} builds a fresh map on every call, so the
 * fuel times cannot be injected there. Instead {@link FuelRegistryImpl} listens to
 * {@code FurnaceFuelBurnTimeEvent}, which is the hook Forge actually consults when a furnace asks
 * how long an item burns.
 */
public interface FuelRegistry {
  FuelRegistry INSTANCE = new FuelRegistryImpl();

  void add(ItemLike item, int cookTime);
}
