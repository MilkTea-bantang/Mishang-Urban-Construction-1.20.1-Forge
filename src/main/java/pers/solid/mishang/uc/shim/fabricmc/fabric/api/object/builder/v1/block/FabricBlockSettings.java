package pers.solid.mishang.uc.shim.fabricmc.fabric.api.object.builder.v1.block;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Function;
import java.util.function.ToIntFunction;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

/**
 * Forge shim for Fabric API's {@code FabricBlockSettings}. Extends the vanilla properties object so
 * that existing chains keep working, and returns {@code FabricBlockSettings} from the chain methods
 * used by this mod.
 */
public class FabricBlockSettings extends BlockBehaviour.Properties {
	protected FabricBlockSettings() {
		super();
	}

	public static FabricBlockSettings of() {
		return new FabricBlockSettings();
	}

	public static FabricBlockSettings copyOf(Block block) {
		return copyFrom(BlockBehaviour.Properties.copy(block));
	}

	public static FabricBlockSettings copyOf(BlockBehaviour.Properties properties) {
		return copyFrom(properties);
	}

	private static FabricBlockSettings copyFrom(BlockBehaviour.Properties source) {
		final FabricBlockSettings settings = new FabricBlockSettings();

		for (Class<?> type = BlockBehaviour.Properties.class; type != null && type != Object.class; type = type.getSuperclass()) {
			for (final Field field : type.getDeclaredFields()) {
				if (Modifier.isStatic(field.getModifiers())) {
					continue;
				}

				try {
					field.setAccessible(true);
					field.set(settings, field.get(source));
				} catch (final ReflectiveOperationException e) {
					throw new IllegalStateException("Failed to copy block settings field " + field.getName(), e);
				}
			}
		}

		return settings;
	}

	/** Fabric API's name for a constant light emission value. */
	public FabricBlockSettings luminance(int luminance) {
		return lightLevel(state -> luminance);
	}

	/** Fabric API's name for {@link #noCollission()}; {@code false} disables collision. */
	public FabricBlockSettings collidable(boolean collidable) {
		return collidable ? this : noCollission();
	}

	@Override
	public FabricBlockSettings mapColor(DyeColor color) {
		super.mapColor(color);
		return this;
	}

	@Override
	public FabricBlockSettings mapColor(MapColor color) {
		super.mapColor(color);
		return this;
	}

	@Override
	public FabricBlockSettings mapColor(Function<BlockState, MapColor> color) {
		super.mapColor(color);
		return this;
	}

	@Override
	public FabricBlockSettings strength(float destroyTime) {
		super.strength(destroyTime);
		return this;
	}

	@Override
	public FabricBlockSettings strength(float destroyTime, float explosionResistance) {
		super.strength(destroyTime, explosionResistance);
		return this;
	}

	@Override
	public FabricBlockSettings lightLevel(ToIntFunction<BlockState> lightEmission) {
		super.lightLevel(lightEmission);
		return this;
	}

	@Override
	public FabricBlockSettings noCollission() {
		super.noCollission();
		return this;
	}

	@Override
	public FabricBlockSettings noOcclusion() {
		super.noOcclusion();
		return this;
	}
}
