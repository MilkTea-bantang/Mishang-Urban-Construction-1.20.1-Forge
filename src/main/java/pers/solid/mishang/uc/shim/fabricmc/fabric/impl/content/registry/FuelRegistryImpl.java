package pers.solid.mishang.uc.shim.fabricmc.fabric.impl.content.registry;

import java.util.IdentityHashMap;
import java.util.Map;
import pers.solid.mishang.uc.shim.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;

public final class FuelRegistryImpl implements FuelRegistry {
  private final Map<Item, Integer> itemCookTimes = new IdentityHashMap<>();

  public FuelRegistryImpl() {
    MinecraftForge.EVENT_BUS.addListener(this::onFuelBurnTime);
  }

  @Override
  public void add(ItemLike item, int cookTime) {
    this.itemCookTimes.put(item.asItem(), cookTime);
  }

  private void onFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
    final Integer cookTime = this.itemCookTimes.get(event.getItemStack().getItem());

    if (cookTime != null) {
      event.setBurnTime(cookTime);
    }
  }
}
