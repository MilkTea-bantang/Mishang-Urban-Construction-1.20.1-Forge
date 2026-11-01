package pers.solid.mishang.uc.shim.fabricmc.fabric.api.event.player;

import pers.solid.mishang.uc.shim.fabricmc.fabric.api.event.Event;
import pers.solid.mishang.uc.shim.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Compatibility shim for Fabric's {@code UseEntityCallback}.
 * Backed by Forge's {@link PlayerInteractEvent.EntityInteract}.
 */
@FunctionalInterface
public interface UseEntityCallback {
    Event<UseEntityCallback> EVENT = createEvent();

    static Event<UseEntityCallback> createEvent() {
        MinecraftForge.EVENT_BUS.register(ForgeBridge.class);
        return EventFactory.createArrayBacked(UseEntityCallback.class,
                (listeners) -> (player, world, hand, entity, hitResult) -> {
                    for (UseEntityCallback listener : listeners) {
                        InteractionResult result = listener.interact(player, world, hand, entity, hitResult);
                        if (result != InteractionResult.PASS) {
                            return result;
                        }
                    }
                    return InteractionResult.PASS;
                });
    }

    InteractionResult interact(Player player, Level world, InteractionHand hand, Entity entity, EntityHitResult hitResult);

    final class ForgeBridge {
        private ForgeBridge() {
        }

        @SubscribeEvent
        public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
            final InteractionResult result = EVENT.invoker()
                    .interact(event.getEntity(), event.getLevel(), event.getHand(), event.getTarget(), null);
            if (result != InteractionResult.PASS) {
                event.setCancellationResult(result);
                event.setCanceled(true);
            }
        }
    }
}
