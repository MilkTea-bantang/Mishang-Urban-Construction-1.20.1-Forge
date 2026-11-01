package pers.solid.mishang.uc.shim.fabricmc.fabric.api.gamerule.v1.rule;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;

/**
 * Forge compatibility shim for Fabric API's {@code DoubleRule}.
 */
public final class DoubleRule extends GameRules.Value<DoubleRule> {
  private final double minimumValue;
  private final double maximumValue;
  private double value;

  public DoubleRule(GameRules.Type<DoubleRule> type, double value, double minimumValue, double maximumValue) {
    super(type);
    this.value = value;
    this.minimumValue = minimumValue;
    this.maximumValue = maximumValue;
  }

  @Override
  protected void updateFromArgument(CommandContext<CommandSourceStack> context, String name) {
    this.value = context.getArgument(name, Double.class);
  }

  @Override
  protected void deserialize(String value) {
    if (!value.isEmpty()) {
      try {
        final double parsed = Double.parseDouble(value);
        if (this.inBounds(parsed)) {
          this.value = parsed;
        }
      } catch (NumberFormatException ignored) {
      }
    }
  }

  @Override
  public String serialize() {
    return Double.toString(this.value);
  }

  @Override
  public int getCommandResult() {
    return Double.compare(this.value, 0.0D);
  }

  @Override
  protected DoubleRule getSelf() {
    return this;
  }

  @Override
  protected DoubleRule copy() {
    return new DoubleRule(this.type, this.value, this.minimumValue, this.maximumValue);
  }

  @Override
  public void setFrom(DoubleRule rule, MinecraftServer server) {
    if (!this.inBounds(rule.value)) {
      throw new IllegalArgumentException("Value out of bounds: " + rule.value);
    }
    this.value = rule.value;
    this.onChanged(server);
  }

  public double get() {
    return this.value;
  }

  private boolean inBounds(double value) {
    return value >= this.minimumValue && value <= this.maximumValue;
  }
}
