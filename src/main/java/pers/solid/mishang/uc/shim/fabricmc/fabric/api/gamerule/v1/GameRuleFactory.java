package pers.solid.mishang.uc.shim.fabricmc.fabric.api.gamerule.v1;

import com.mojang.brigadier.arguments.StringArgumentType;
import java.util.function.BiConsumer;
import pers.solid.mishang.uc.shim.fabricmc.fabric.api.gamerule.v1.rule.EnumRule;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;

/**
 * Forge compatibility shim for Fabric API's {@code GameRuleFactory}.
 *
 * <p>Only the enum rule factory is provided: it is the sole factory used by this mod. Vanilla
 * boolean / integer rules are created through {@link GameRules.BooleanValue#create} and
 * {@link GameRules.IntegerValue#create} directly.
 */
public final class GameRuleFactory {
  private GameRuleFactory() {
  }

  public static <E extends Enum<E>> GameRules.Type<EnumRule<E>> createEnumRule(E defaultValue) {
    return createEnumRule(defaultValue, (server, rule) -> {
    });
  }

  public static <E extends Enum<E>> GameRules.Type<EnumRule<E>> createEnumRule(E defaultValue, BiConsumer<MinecraftServer, EnumRule<E>> changedCallback) {
    return createEnumRule(defaultValue, defaultValue.getDeclaringClass().getEnumConstants(), changedCallback);
  }

  public static <E extends Enum<E>> GameRules.Type<EnumRule<E>> createEnumRule(E defaultValue, E[] supportedValues, BiConsumer<MinecraftServer, EnumRule<E>> changedCallback) {
    if (defaultValue == null) {
      throw new NullPointerException("Default rule value cannot be null");
    }
    if (supportedValues == null || supportedValues.length == 0) {
      throw new IllegalArgumentException("Cannot register an enum rule where no values are supported");
    }

    return new GameRules.Type<EnumRule<E>>(
        StringArgumentType::word,
        type -> new EnumRule<>(type, defaultValue, supportedValues),
        changedCallback,
        (visitor, key, type) -> {
        });
  }
}
