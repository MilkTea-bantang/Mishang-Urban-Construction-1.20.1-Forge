package pers.solid.mishang.uc.shim.fabricmc.fabric.api.object.builder.v1.block.entity;

import com.mojang.datafixers.types.Type;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Minimal Forge shim for Fabric API's {@code FabricBlockEntityTypeBuilder}. It delegates directly to
 * {@link BlockEntityType.Builder}; the Fabric {@link Factory} functional interface has the same shape as
 * {@link BlockEntityType.BlockEntitySupplier}.
 */
public final class FabricBlockEntityTypeBuilder<T extends BlockEntity> {
	private final BlockEntityType.Builder<T> builder;

	private FabricBlockEntityTypeBuilder(BlockEntityType.Builder<T> builder) {
		this.builder = builder;
	}

	public static <T extends BlockEntity> FabricBlockEntityTypeBuilder<T> create(Factory<T> factory, Block... blocks) {
		return new FabricBlockEntityTypeBuilder<>(BlockEntityType.Builder.of(factory::create, blocks));
	}

	public BlockEntityType<T> build(Type<?> type) {
		return builder.build(type);
	}

	@FunctionalInterface
	public interface Factory<T extends BlockEntity> {
		T create(BlockPos pos, BlockState state);
	}
}
