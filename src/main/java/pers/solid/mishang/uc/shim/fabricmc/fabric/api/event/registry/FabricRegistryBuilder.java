package pers.solid.mishang.uc.shim.fabricmc.fabric.api.event.registry;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

/**
 * Forge port shim of Fabric's {@code FabricRegistryBuilder}.
 *
 * <p>The mod only uses this to build its own, self-contained dynamic registry
 * ({@code mishanguc:special_drawable_type}). Lookups against that registry go through the returned
 * {@link MappedRegistry} directly, so there is no need to hook it into
 * {@code BuiltInRegistries.REGISTRY}; keeping it local avoids the freeze/sync problems that
 * global registration would introduce on Forge.
 */
public final class FabricRegistryBuilder {
  private FabricRegistryBuilder() {
  }

  public static <T> SimpleBuilder<T> createSimple(ResourceKey<Registry<T>> key) {
    return new SimpleBuilder<>(key);
  }

  public static final class SimpleBuilder<T> {
    private final ResourceKey<Registry<T>> key;

    private SimpleBuilder(ResourceKey<Registry<T>> key) {
      this.key = key;
    }

    public MappedRegistry<T> build() {
      return new MappedRegistry<>(this.key, Lifecycle.stable());
    }

    public MappedRegistry<T> buildAndRegister() {
      return this.build();
    }
  }
}
