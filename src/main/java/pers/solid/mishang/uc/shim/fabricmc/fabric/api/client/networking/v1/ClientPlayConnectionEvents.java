package pers.solid.mishang.uc.shim.fabricmc.fabric.api.client.networking.v1;

import pers.solid.mishang.uc.shim.fabricmc.fabric.api.event.Event;
import pers.solid.mishang.uc.shim.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Compatibility shim for Fabric's {@code ClientPlayConnectionEvents}.
 */
public final class ClientPlayConnectionEvents {
    public static final Event<Disconnect> DISCONNECT = EventFactory.createArrayBacked(Disconnect.class,
            listeners -> (handler, client) -> {
                for (Disconnect listener : listeners) {
                    listener.onPlayDisconnect(handler, client);
                }
            });

    static {
        MinecraftForge.EVENT_BUS.register(ForgeBridge.class);
    }

    private ClientPlayConnectionEvents() {
    }

    @FunctionalInterface
    public interface Disconnect {
        void onPlayDisconnect(ClientPacketListener handler, Minecraft client);
    }

    public static final class ForgeBridge {
        private ForgeBridge() {
        }

        @SubscribeEvent
        public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
            final Minecraft client = Minecraft.getInstance();
            DISCONNECT.invoker().onPlayDisconnect(client.getConnection(), client);
        }
    }
}
