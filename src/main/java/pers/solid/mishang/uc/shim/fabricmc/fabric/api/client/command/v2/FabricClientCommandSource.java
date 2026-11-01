package pers.solid.mishang.uc.shim.fabricmc.fabric.api.client.command.v2;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * Compatibility shim for Fabric's {@code FabricClientCommandSource}.
 */
public interface FabricClientCommandSource extends SharedSuggestionProvider {
    void sendFeedback(Component message);

    void sendError(Component message);

    Minecraft getClient();

    @Nullable
    LocalPlayer getPlayer();

    default Entity getEntity() {
        return getPlayer();
    }

    default Vec3 getPosition() {
        return getEntity().position();
    }

    default Vec2 getRotation() {
        final Entity entity = getEntity();
        return new Vec2(entity.getXRot(), entity.getYRot());
    }

    @Nullable
    ClientLevel getWorld();

    default Object getMeta(String key) {
        return null;
    }
}
