package pers.solid.mishang.uc.shim.fabricmc.fabric.api.event;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import net.minecraft.resources.ResourceLocation;

/**
 * Minimal array-backed {@link Event} implementation mirroring Fabric's semantics:
 * listeners are invoked in registration order through a generated invoker.
 */
final class ArrayBackedEvent<T> implements Event<T> {
    private final Class<T> type;
    private final Function<T[], T> factory;
    private final List<T> listeners = new ArrayList<>();
    private volatile T invoker;

    ArrayBackedEvent(Class<T> type, Function<T[], T> factory) {
        this.type = type;
        this.factory = factory;
        rebuild();
    }

    private void rebuild() {
        @SuppressWarnings("unchecked")
        T[] array = (T[]) Array.newInstance(type, listeners.size());
        listeners.toArray(array);
        this.invoker = factory.apply(array);
    }

    @Override
    public synchronized void register(T listener) {
        Objects.requireNonNull(listener, "listener");
        listeners.add(listener);
        rebuild();
    }

    @Override
    public void register(ResourceLocation id, T listener) {
        register(listener);
    }

    @Override
    public T invoker() {
        return invoker;
    }
}
