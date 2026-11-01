package pers.solid.mishang.uc.shim.fabricmc.fabric.api.client.networking.v1;

import io.netty.buffer.Unpooled;
import pers.solid.mishang.uc.shim.fabricmc.fabric.api.networking.v1.FabricNetworkingCore;
import pers.solid.mishang.uc.shim.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

/**
 * Compatibility shim for Fabric's {@code ClientPlayNetworking}.
 */
public final class ClientPlayNetworking {
    private ClientPlayNetworking() {
    }

    public static void registerGlobalReceiver(ResourceLocation channel, PlayChannelHandler handler) {
        FabricNetworkingCore.registerClientReceiver(channel, data -> {
            final Minecraft client = Minecraft.getInstance();
            final ClientPacketListener listener = client.getConnection();
            handler.receive(client, listener, new FriendlyByteBuf(Unpooled.wrappedBuffer(data)), PacketSender.EMPTY);
        });
    }

    public static void send(ResourceLocation channel, FriendlyByteBuf buf) {
        FabricNetworkingCore.sendToServer(channel, buf);
    }

    @FunctionalInterface
    public interface PlayChannelHandler {
        void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender);
    }
}
