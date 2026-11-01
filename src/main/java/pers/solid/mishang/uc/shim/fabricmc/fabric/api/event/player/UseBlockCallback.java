package pers.solid.mishang.uc.shim.fabricmc.fabric.api.event.player;

import pers.solid.mishang.uc.shim.fabricmc.fabric.api.event.Event;
import pers.solid.mishang.uc.shim.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Compatibility shim for Fabric's {@code UseBlockCallback}.
 * Backed by Forge's {@link PlayerInteractEvent.RightClickBlock}.
 */
@FunctionalInterface
public interface UseBlockCallback {
    Event<UseBlockCallback> EVENT = createEvent();

    static Event<UseBlockCallback> createEvent() {
        MinecraftForge.EVENT_BUS.register(ForgeBridge.class);
        return EventFactory.createArrayBacked(UseBlockCallback.class,
                (listeners) -> (player, world, hand, hitResult) -> {
                    for (UseBlockCallback listener : listeners) {
                        InteractionResult result = listener.interact(player, world, hand, hitResult);
                        if (result != InteractionResult.PASS) {
                            return result;
                        }
                    }
                    return InteractionResult.PASS;
                });
    }

    InteractionResult interact(Player player, Level world, InteractionHand hand, BlockHitResult hitResult);

    final class ForgeBridge {
        private ForgeBridge() {
        }

        @SubscribeEvent
        public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
            final BlockHitResult hitResult = event.getHitVec();
            final InteractionResult result = EVENT.invoker()
                    .interact(event.getEntity(), event.getLevel(), event.getHand(), hitResult);
            if (result != InteractionResult.PASS) {
                event.setCancellationResult(result);
                event.setCanceled(true);
            }
        }
    }
}
