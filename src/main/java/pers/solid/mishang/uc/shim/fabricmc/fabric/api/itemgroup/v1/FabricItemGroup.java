package pers.solid.mishang.uc.shim.fabricmc.fabric.api.itemgroup.v1;

import net.minecraft.world.item.CreativeModeTab;

/**
 * Minimal Forge shim for Fabric API's {@code FabricItemGroup}. On Fabric, {@code builder()} returns a
 * {@code ItemGroup.Builder} (which extends {@link CreativeModeTab.Builder} with a default row/column).
 * The vanilla builder requires an explicit {@link CreativeModeTab.Row} and column, so we supply a default here.
 */
public final class FabricItemGroup {
	private FabricItemGroup() {
	}

	public static CreativeModeTab.Builder builder() {
		return new CreativeModeTab.Builder(CreativeModeTab.Row.TOP, 0);
	}
}
