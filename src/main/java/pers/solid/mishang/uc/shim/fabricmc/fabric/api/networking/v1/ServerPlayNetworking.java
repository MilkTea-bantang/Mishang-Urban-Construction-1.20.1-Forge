package pers.solid.mishang.uc.shim.fabricmc.fabric.api.networking.v1;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

/**
 * Compatibility shim for Fabric's {@code ServerPlayNetworking}.
 */
public final class ServerPlayNetworking {
    private ServerPlayNetworking() {
    }

    public static void registerGlobalReceiver(ResourceLocation channel, PlayChannelHandler handler) {
        FabricNetworkingCore.registerServerReceiver(channel, handler);
    }

    public static void send(ServerPlayer player, ResourceLocation channel, FriendlyByteBuf buf) {
        FabricNetworkingCore.sendToPlayer(player, channel, buf);
    }

    @FunctionalInterface
    public interface PlayChannelHandler {
        void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler,
                     FriendlyByteBuf buf, PacketSender responseSender);
    }
}
