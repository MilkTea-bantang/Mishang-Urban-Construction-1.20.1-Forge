package pers.solid.mishang.uc.shim.fabricmc.fabric.api.client.command.v2;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import pers.solid.mishang.uc.shim.fabricmc.fabric.api.event.Event;
import pers.solid.mishang.uc.shim.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Bridges Fabric's client command registration onto Forge's {@link RegisterClientCommandsEvent}.
 *
 * <p>The mod builds a {@code CommandDispatcher<FabricClientCommandSource>} because brigadier's
 * generics are invariant and Forge hands us a {@code CommandDispatcher<CommandSourceStack>}.
 * The tree is therefore mirrored node-by-node, and every callback/suggestion/requirement is
 * wrapped so that the {@link CommandSourceStack} is adapted into a {@link FabricClientCommandSource}.
 */
final class ClientCommandBridge {
    private ClientCommandBridge() {
    }

    static Event<ClientCommandRegistrationCallback> create() {
        final Event<ClientCommandRegistrationCallback> event = EventFactory.createArrayBacked(
            ClientCommandRegistrationCallback.class,
            callbacks -> (dispatcher, buildContext) -> {
                for (final ClientCommandRegistrationCallback callback : callbacks) {
                    callback.register(dispatcher, buildContext);
                }
            });
        MinecraftForge.EVENT_BUS.addListener(ClientCommandBridge::onRegisterCommands);
        return event;
    }

    private static void onRegisterCommands(RegisterClientCommandsEvent event) {
        final CommandDispatcher<FabricClientCommandSource> fabricDispatcher = new CommandDispatcher<>();
        ClientCommandRegistrationCallback.EVENT.invoker().register(fabricDispatcher, event.getBuildContext());

        final CommandDispatcher<CommandSourceStack> forgeDispatcher = event.getDispatcher();
        for (final CommandNode<FabricClientCommandSource> child : fabricDispatcher.getRoot().getChildren()) {
            final CommandNode<CommandSourceStack> mirrored = mirror(child);
            if (mirrored == null) {
                continue;
            }
            try {
                forgeDispatcher.getRoot().addChild(mirrored);
            } catch (RuntimeException e) {
                // A node with the same name is already registered; keep the original.
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static CommandNode<CommandSourceStack> mirror(CommandNode<FabricClientCommandSource> node) {
        final CommandNode<CommandSourceStack> result;
        if (node instanceof LiteralCommandNode) {
            final LiteralCommandNode<FabricClientCommandSource> source = (LiteralCommandNode<FabricClientCommandSource>) node;
            final Command<CommandSourceStack> command = source.getCommand() == null ? null
                : context -> source.getCommand().run(retype(context));
            final Predicate<CommandSourceStack> requirement = stack -> source.getRequirement().test(adapt(stack));
            result = new LiteralCommandNode<>(source.getLiteral(), command, requirement, null, null, source.isFork());
        } else if (node instanceof ArgumentCommandNode) {
            final ArgumentCommandNode<FabricClientCommandSource, ?> source = (ArgumentCommandNode<FabricClientCommandSource, ?>) node;
            final Command<CommandSourceStack> command = source.getCommand() == null ? null
                : context -> source.getCommand().run(retype(context));
            final Predicate<CommandSourceStack> requirement = stack -> source.getRequirement().test(adapt(stack));
            final SuggestionProvider<CommandSourceStack> suggestions = source.getCustomSuggestions() == null ? null
                : (context, builder) -> source.getCustomSuggestions().getSuggestions(retype(context), builder);
            result = new ArgumentCommandNode<>(source.getName(), (ArgumentType<Object>) source.getType(),
                command, requirement, null, null, source.isFork(), suggestions);
        } else {
            return null;
        }

        for (final CommandNode<FabricClientCommandSource> child : node.getChildren()) {
            final CommandNode<CommandSourceStack> mirroredChild = mirror(child);
            if (mirroredChild != null) {
                result.addChild(mirroredChild);
            }
        }
        return result;
    }

    private static FabricClientCommandSource adapt(CommandSourceStack stack) {
        return new AdaptedCommandSource(stack);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static CommandContext<FabricClientCommandSource> retype(CommandContext<CommandSourceStack> context) {
        return ((CommandContext) context).copyFor(adapt(context.getSource()));
    }

    private static final class AdaptedCommandSource implements FabricClientCommandSource {
        private final CommandSourceStack delegate;

        private AdaptedCommandSource(CommandSourceStack delegate) {
            this.delegate = delegate;
        }

        @Override
        public void sendFeedback(Component message) {
            Minecraft.getInstance().gui.getChat().addMessage(message);
        }

        @Override
        public void sendError(Component message) {
            Minecraft.getInstance().gui.getChat().addMessage(message);
        }

        @Override
        public Minecraft getClient() {
            return Minecraft.getInstance();
        }

        @Override
        public LocalPlayer getPlayer() {
            return Minecraft.getInstance().player;
        }

        @Override
        public ClientLevel getWorld() {
            return Minecraft.getInstance().level;
        }

        @Override
        public Collection<String> getOnlinePlayerNames() {
            return delegate.getOnlinePlayerNames();
        }

        @Override
        public Collection<String> getAllTeams() {
            return delegate.getAllTeams();
        }

        @Override
        public Stream<ResourceLocation> getAvailableSounds() {
            return delegate.getAvailableSounds();
        }

        @Override
        public Stream<ResourceLocation> getRecipeNames() {
            return delegate.getRecipeNames();
        }

        @Override
        public CompletableFuture<Suggestions> customSuggestion(CommandContext<?> context) {
            return delegate.customSuggestion(context);
        }

        @Override
        public Set<ResourceKey<Level>> levels() {
            return delegate.levels();
        }

        @Override
        public RegistryAccess registryAccess() {
            return delegate.registryAccess();
        }

        @Override
        public FeatureFlagSet enabledFeatures() {
            return delegate.enabledFeatures();
        }

        @Override
        public CompletableFuture<Suggestions> suggestRegistryElements(ResourceKey<? extends Registry<?>> key,
                                                                      SharedSuggestionProvider.ElementSuggestionType type,
                                                                      SuggestionsBuilder builder,
                                                                      CommandContext<?> context) {
            return delegate.suggestRegistryElements(key, type, builder, context);
        }

        @Override
        public boolean hasPermission(int level) {
            return delegate.hasPermission(level);
        }
    }
}
