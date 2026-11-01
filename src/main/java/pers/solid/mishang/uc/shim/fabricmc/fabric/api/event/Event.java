package pers.solid.mishang.uc.shim.fabricmc.fabric.api.event;

import net.minecraft.resources.ResourceLocation;

/**
 * Compatibility shim for Fabric's {@code Event}.
 */
public interface Event<T> {
    T invoker();

    void register(T listener);

    void register(ResourceLocation id, T listener);
}
