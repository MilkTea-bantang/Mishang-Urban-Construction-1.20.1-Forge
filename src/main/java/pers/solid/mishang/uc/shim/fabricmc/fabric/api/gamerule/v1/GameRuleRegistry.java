package pers.solid.mishang.uc.shim.fabricmc.fabric.api.gamerule.v1;

import net.minecraft.world.level.GameRules;

/**
 * Forge compatibility shim for Fabric API's {@code GameRuleRegistry}.
 */
public final class GameRuleRegistry {
  private GameRuleRegistry() {
  }

  public static <T extends GameRules.Value<T>> GameRules.Key<T> register(String name, GameRules.Category category, GameRules.Type<T> type) {
    return GameRules.register(name, category, type);
  }
}
