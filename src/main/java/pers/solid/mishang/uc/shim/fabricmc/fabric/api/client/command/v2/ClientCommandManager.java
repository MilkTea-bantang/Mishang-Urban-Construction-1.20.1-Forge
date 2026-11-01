package pers.solid.mishang.uc.shim.fabricmc.fabric.api.client.command.v2;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;

/**
 * Compatibility shim for Fabric's {@code ClientCommandManager}.
 */
public final class ClientCommandManager {
    private ClientCommandManager() {
    }

    public static LiteralArgumentBuilder<FabricClientCommandSource> literal(String literal) {
        return LiteralArgumentBuilder.literal(literal);
    }

    public static <T> RequiredArgumentBuilder<FabricClientCommandSource, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }
}
