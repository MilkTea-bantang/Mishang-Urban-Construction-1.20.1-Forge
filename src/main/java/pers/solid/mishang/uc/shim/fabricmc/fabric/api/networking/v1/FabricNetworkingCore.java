package pers.solid.mishang.uc.shim.fabricmc.fabric.api.networking.v1;

import java.util.HashMap;
import java.util.Map;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * Bridges Fabric's logical channels onto a single Forge {@link SimpleChannel}.
 * Every Fabric packet is wrapped into a {@link FabricPayload} carrying the logical
 * channel id and the raw payload bytes, then dispatched to the handler registered
 * for that id.
 */
public final class FabricNetworkingCore {
    private static final String VERSION = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("mishanguc", "fabric_compat"),
            () -> VERSION,
            NetworkRegistry.acceptMissingOr(VERSION),
            NetworkRegistry.acceptMissingOr(VERSION));

    private static final Map<ResourceLocation, ServerPlayNetworking.PlayChannelHandler> SERVER_RECEIVERS = new HashMap<>();
    private static final Map<ResourceLocation, ClientPayloadHandler> CLIENT_RECEIVERS = new HashMap<>();

    static {
        CHANNEL.registerMessage(0, FabricPayload.class, FabricPayload::write, FabricPayload::read,
                (payload, contextSupplier) -> {
                    final NetworkEvent.Context context = contextSupplier.get();
                    context.enqueueWork(() -> dispatch(payload, context));
                    context.setPacketHandled(true);
                });
    }

    private FabricNetworkingCore() {
    }

    public static void registerServerReceiver(ResourceLocation channel, ServerPlayNetworking.PlayChannelHandler handler) {
        SERVER_RECEIVERS.put(channel, handler);
    }

    public static void registerClientReceiver(ResourceLocation channel, ClientPayloadHandler handler) {
        CLIENT_RECEIVERS.put(channel, handler);
    }

    public static void sendToServer(ResourceLocation channel, FriendlyByteBuf buf) {
        CHANNEL.sendToServer(new FabricPayload(channel, toBytes(buf)));
    }

    public static void sendToPlayer(ServerPlayer player, ResourceLocation channel, FriendlyByteBuf buf) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new FabricPayload(channel, toBytes(buf)));
    }

    private static byte[] toBytes(FriendlyByteBuf buf) {
        final byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        return data;
    }

    private static void dispatch(FabricPayload payload, NetworkEvent.Context context) {
        if (context.getDirection().getReceptionSide() == LogicalSide.SERVER) {
            final ServerPlayer player = context.getSender();
            if (player == null) return;
            final ServerPlayNetworking.PlayChannelHandler handler = SERVER_RECEIVERS.get(payload.channel);
            if (handler == null) return;
            final MinecraftServer server = player.getServer();
            if (server == null) return;
            handler.receive(server, player, player.connection,
                    new FriendlyByteBuf(Unpooled.wrappedBuffer(payload.data)), PacketSender.EMPTY);
        } else {
            final ClientPayloadHandler handler = CLIENT_RECEIVERS.get(payload.channel);
            if (handler != null) handler.handle(payload.data);
        }
    }

    /**
     * Client side counterpart of a {@code PlayChannelHandler}; only used through
     * {@code ClientPlayNetworking} so that no client-only class is referenced here.
     */
    @FunctionalInterface
    public interface ClientPayloadHandler {
        void handle(byte[] data);
    }

    public static final class FabricPayload {
        public final ResourceLocation channel;
        public final byte[] data;

        public FabricPayload(ResourceLocation channel, byte[] data) {
            this.channel = channel;
            this.data = data;
        }

        public void write(FriendlyByteBuf buf) {
            buf.writeResourceLocation(channel);
            buf.writeByteArray(data);
        }

        public static FabricPayload read(FriendlyByteBuf buf) {
            return new FabricPayload(buf.readResourceLocation(), buf.readByteArray());
        }
    }
}
