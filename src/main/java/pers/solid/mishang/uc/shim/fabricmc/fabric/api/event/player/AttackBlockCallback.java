package pers.solid.mishang.uc.shim.fabricmc.fabric.api.event.player;

import pers.solid.mishang.uc.shim.fabricmc.fabric.api.event.Event;
import pers.solid.mishang.uc.shim.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Compatibility shim for Fabric's {@code AttackBlockCallback}.
 * Backed by Forge's {@link PlayerInteractEvent.LeftClickBlock}.
 */
@FunctionalInterface
public interface AttackBlockCallback {
    Event<AttackBlockCallback> EVENT = createEvent();

    static Event<AttackBlockCallback> createEvent() {
        MinecraftForge.EVENT_BUS.register(ForgeBridge.class);
        return EventFactory.createArrayBacked(AttackBlockCallback.class,
                (listeners) -> (player, world, hand, pos, direction) -> {
                    for (AttackBlockCallback listener : listeners) {
                        InteractionResult result = listener.interact(player, world, hand, pos, direction);
                        if (result != InteractionResult.PASS) {
                            return result;
                        }
                    }
                    return InteractionResult.PASS;
                });
    }

    InteractionResult interact(Player player, Level world, InteractionHand hand, BlockPos pos, Direction direction);

    final class ForgeBridge {
        private ForgeBridge() {
        }

        @SubscribeEvent
        public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
            final Direction face = event.getFace();
            if (face == null) {
                return;
            }
            final InteractionResult result = EVENT.invoker()
                    .interact(event.getEntity(), event.getLevel(), event.getHand(), event.getPos(), face);
            if (result != InteractionResult.PASS) {
                event.setCancellationResult(result);
                event.setCanceled(true);
            }
        }
    }
}
