package pers.solid.mishang.uc.shim.fabricmc.fabric.api.event;

import java.util.function.Function;

/**
 * Compatibility shim for Fabric's {@code EventFactory}.
 */
public final class EventFactory {
    private EventFactory() {
    }

    public static <T> Event<T> createArrayBacked(Class<T> type, Function<T[], T> factory) {
        return new ArrayBackedEvent<>(type, factory);
    }
}
