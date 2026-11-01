package pers.solid.mishang.uc.shim.fabricmc.fabric.api.item.v1;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

/**
 * Forge shim for Fabric API's {@code FabricItemSettings}. Only the chain methods used by this mod
 * are overridden to keep the fluent type.
 */
public class FabricItemSettings extends Item.Properties {
	public FabricItemSettings() {
		super();
	}

	@Override
	public FabricItemSettings stacksTo(int maxStackSize) {
		super.stacksTo(maxStackSize);
		return this;
	}

	@Override
	public FabricItemSettings durability(int maxDamage) {
		super.durability(maxDamage);
		return this;
	}

	@Override
	public FabricItemSettings fireResistant() {
		super.fireResistant();
		return this;
	}

	@Override
	public FabricItemSettings rarity(Rarity rarity) {
		super.rarity(rarity);
		return this;
	}
}
