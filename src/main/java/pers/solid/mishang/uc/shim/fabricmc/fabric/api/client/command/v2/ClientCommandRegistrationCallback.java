package pers.solid.mishang.uc.shim.fabricmc.fabric.api.client.command.v2;

import com.mojang.brigadier.CommandDispatcher;
import pers.solid.mishang.uc.shim.fabricmc.fabric.api.event.Event;
import net.minecraft.commands.CommandBuildContext;

/**
 * Compatibility shim for Fabric's {@code ClientCommandRegistrationCallback}.
 */
@FunctionalInterface
public interface ClientCommandRegistrationCallback {
    Event<ClientCommandRegistrationCallback> EVENT = ClientCommandBridge.create();

    void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext buildContext);
}
