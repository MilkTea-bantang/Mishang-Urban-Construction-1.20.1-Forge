package pers.solid.mishang.uc.shim.fabricmc.fabric.api.networking.v1;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

/**
 * Compatibility shim for Fabric's {@code PacketSender}.
 */
@FunctionalInterface
public interface PacketSender {
    /**
     * A sender that silently discards packets. This mod never uses the response sender,
     * so this implementation is sufficient.
     */
    PacketSender EMPTY = (channel, buf) -> {
    };

    void sendPacket(ResourceLocation channel, FriendlyByteBuf buf);
}
