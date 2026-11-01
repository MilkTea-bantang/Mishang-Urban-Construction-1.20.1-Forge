package pers.solid.mishang.uc.shim.fabricmc.fabric.api.gamerule.v1.rule;

import com.mojang.brigadier.context.CommandContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;

/**
 * Forge compatibility shim for Fabric API's {@code EnumRule}.
 */
public final class EnumRule<E extends Enum<E>> extends GameRules.Value<EnumRule<E>> {
  private final Class<E> classType;
  private final List<E> supportedValues;
  private E value;

  public EnumRule(GameRules.Type<EnumRule<E>> type, E value, E[] supportedValues) {
    this(type, value, Arrays.asList(supportedValues));
  }

  public EnumRule(GameRules.Type<EnumRule<E>> type, E value, Collection<E> supportedValues) {
    super(type);
    this.classType = value.getDeclaringClass();
    this.value = value;
    this.supportedValues = new ArrayList<>(supportedValues);
    if (!this.supports(value)) {
      throw new IllegalArgumentException("Cannot set default value");
    }
  }

  @Override
  protected void updateFromArgument(CommandContext<CommandSourceStack> context, String name) {
    // The rule is exposed through a word argument; the literal must be a supported enum constant.
    final String raw = context.getArgument(name, String.class);
    final E parsed;
    try {
      parsed = Enum.valueOf(this.classType, raw);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Unknown value '" + raw + "' for rule of type " + this.classType.getSimpleName());
    }
    if (!this.supports(parsed)) {
      throw new IllegalArgumentException("Unsupported value: " + raw);
    }
    this.value = parsed;
  }

  @Override
  protected void deserialize(String value) {
    try {
      final E deserialized = Enum.valueOf(this.classType, value);
      if (!this.supports(deserialized)) {
        throw new IllegalArgumentException("Unsupported value: " + value);
      }
      // Assign directly: deserialization happens while loading the world, where firing the
      // change callback with a null server would break listeners.
      this.value = deserialized;
    } catch (IllegalArgumentException e) {
      // Keep the default value on invalid input.
    }
  }

  @Override
  public String serialize() {
    return this.value.name();
  }

  @Override
  public int getCommandResult() {
    return this.value.ordinal();
  }

  @Override
  protected EnumRule<E> getSelf() {
    return this;
  }

  @Override
  protected EnumRule<E> copy() {
    return new EnumRule<>(this.type, this.value, this.supportedValues);
  }

  @Override
  public void setFrom(EnumRule<E> rule, MinecraftServer server) {
    if (!this.supports(rule.value)) {
      throw new IllegalArgumentException("Rule does not support value: " + rule.value);
    }
    this.value = rule.value;
    this.onChanged(server);
  }

  public Class<E> getEnumClass() {
    return this.classType;
  }

  @Override
  public String toString() {
    return this.value.toString();
  }

  public E get() {
    return this.value;
  }

  public void cycle() {
    int index = this.supportedValues.indexOf(this.value);
    if (index < 0) {
      throw new IllegalArgumentException("Invalid value: " + this.value);
    }
    this.set(this.supportedValues.get((index + 1) % this.supportedValues.size()), null);
  }

  public boolean supports(E value) {
    return this.supportedValues.contains(value);
  }

  public void set(E value, MinecraftServer server) {
    if (value == null) {
      throw new NullPointerException("value");
    }
    if (!this.supports(value)) {
      throw new IllegalArgumentException("Tried to set an unsupported value: " + value);
    }
    this.value = value;
    this.onChanged(server);
  }
}
