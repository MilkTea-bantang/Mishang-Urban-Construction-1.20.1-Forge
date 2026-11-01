package pers.solid.mishang.uc.shim.fabricmc.fabric.api.networking.v1;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Compatibility shim for Fabric's {@code PacketByteBufs}.
 */
public final class PacketByteBufs {
    private PacketByteBufs() {
    }

    public static FriendlyByteBuf create() {
        return new FriendlyByteBuf(Unpooled.buffer());
    }

    public static FriendlyByteBuf empty() {
        return new FriendlyByteBuf(Unpooled.EMPTY_BUFFER);
    }
}
