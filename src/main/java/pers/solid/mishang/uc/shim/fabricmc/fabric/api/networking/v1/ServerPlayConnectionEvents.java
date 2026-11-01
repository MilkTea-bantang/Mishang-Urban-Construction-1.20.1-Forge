package pers.solid.mishang.uc.shim.fabricmc.fabric.api.networking.v1;

import pers.solid.mishang.uc.shim.fabricmc.fabric.api.event.Event;
import pers.solid.mishang.uc.shim.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Compatibility shim for Fabric's {@code ServerPlayConnectionEvents}.
 * Backed by Forge's player login/logout events.
 */
public final class ServerPlayConnectionEvents {
    public static final Event<Join> JOIN = EventFactory.createArrayBacked(Join.class,
            listeners -> (handler, sender, server) -> {
                for (Join listener : listeners) {
                    listener.onPlayReady(handler, sender, server);
                }
            });

    public static final Event<Disconnect> DISCONNECT = EventFactory.createArrayBacked(Disconnect.class,
            listeners -> (handler, server) -> {
                for (Disconnect listener : listeners) {
                    listener.onPlayDisconnect(handler, server);
                }
            });

    static {
        MinecraftForge.EVENT_BUS.register(ForgeBridge.class);
    }

    private ServerPlayConnectionEvents() {
    }

    @FunctionalInterface
    public interface Join {
        void onPlayReady(ServerGamePacketListenerImpl handler, PacketSender sender, MinecraftServer server);
    }

    @FunctionalInterface
    public interface Disconnect {
        void onPlayDisconnect(ServerGamePacketListenerImpl handler, MinecraftServer server);
    }

    public static final class ForgeBridge {
        private ForgeBridge() {
        }

        @SubscribeEvent
        public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
            if (event.getEntity() instanceof ServerPlayer player) {
                JOIN.invoker().onPlayReady(player.connection, PacketSender.EMPTY, player.getServer());
            }
        }

        @SubscribeEvent
        public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
            if (event.getEntity() instanceof ServerPlayer player) {
                DISCONNECT.invoker().onPlayDisconnect(player.connection, player.getServer());
            }
        }
    }
}
