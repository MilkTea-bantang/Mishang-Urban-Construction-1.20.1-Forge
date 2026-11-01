package pers.solid.mishang.uc.shim.fabricmc.fabric.api.client.rendering.v1;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import pers.solid.mishang.uc.shim.fabricmc.fabric.api.event.Event;
import pers.solid.mishang.uc.shim.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;

/**
 * Compatibility shim for Fabric's {@code WorldRenderEvents}, exposing only the two events the ported
 * code relies on. Both are backed by Forge's {@link RenderHighlightEvent.Block}.
 *
 * <p>Fabric semantics: every subscriber is invoked, and if any of them returns {@code false}, the
 * vanilla block outline is suppressed. On Forge the equivalent is cancelling the highlight event.
 */
public final class WorldRenderEvents {
    private WorldRenderEvents() {
    }

    @FunctionalInterface
    public interface BlockOutline {
        boolean onBlockOutline(WorldRenderContext context, WorldRenderContext.BlockOutlineContext blockOutlineContext);
    }

    @FunctionalInterface
    public interface BeforeBlockOutline {
        boolean beforeBlockOutline(WorldRenderContext context, @Nullable HitResult hitResult);
    }

    public static final Event<BlockOutline> BLOCK_OUTLINE = EventFactory.createArrayBacked(
            BlockOutline.class,
            listeners -> (context, blockOutlineContext) -> {
                boolean result = true;
                for (final BlockOutline listener : listeners) {
                    if (!listener.onBlockOutline(context, blockOutlineContext)) {
                        result = false;
                    }
                }
                return result;
            });

    public static final Event<BeforeBlockOutline> BEFORE_BLOCK_OUTLINE = EventFactory.createArrayBacked(
            BeforeBlockOutline.class,
            listeners -> (context, hitResult) -> {
                boolean result = true;
                for (final BeforeBlockOutline listener : listeners) {
                    if (!listener.beforeBlockOutline(context, hitResult)) {
                        result = false;
                    }
                }
                return result;
            });

    static {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, RenderHighlightEvent.Block.class,
                WorldRenderEvents::onRenderHighlight);
    }

    private static void onRenderHighlight(RenderHighlightEvent.Block event) {
        final Minecraft client = Minecraft.getInstance();
        final ClientLevel world = client.level;
        if (world == null) {
            return;
        }
        final Camera camera = event.getCamera();
        final WorldRenderContext context = createContext(event, world);
        final HitResult target = event.getTarget();

        if (!BEFORE_BLOCK_OUTLINE.invoker().beforeBlockOutline(context, target)) {
            event.setCanceled(true);
            return;
        }
        if (!BLOCK_OUTLINE.invoker().onBlockOutline(context, createBlockOutlineContext(event, world, camera))) {
            event.setCanceled(true);
        }
    }

    private static WorldRenderContext createContext(RenderHighlightEvent.Block event, ClientLevel world) {
        final MultiBufferSource consumers = event.getMultiBufferSource();
        final PoseStack matrixStack = event.getPoseStack();
        final Camera camera = event.getCamera();
        return new WorldRenderContext() {
            @Override
            public MultiBufferSource consumers() {
                return consumers;
            }

            @Override
            public ClientLevel world() {
                return world;
            }

            @Override
            public PoseStack matrixStack() {
                return matrixStack;
            }

            @Override
            public Camera camera() {
                return camera;
            }
        };
    }

    private static WorldRenderContext.BlockOutlineContext createBlockOutlineContext(
            RenderHighlightEvent.Block event, ClientLevel world, Camera camera) {
        final BlockHitResult target = event.getTarget();
        final BlockPos blockPos = target.getBlockPos();
        final Vec3 cameraPos = camera.getPosition();
        return new WorldRenderContext.BlockOutlineContext() {
            @Override
            public net.minecraft.world.entity.Entity entity() {
                return camera.getEntity();
            }

            @Override
            public double cameraX() {
                return cameraPos.x;
            }

            @Override
            public double cameraY() {
                return cameraPos.y;
            }

            @Override
            public double cameraZ() {
                return cameraPos.z;
            }

            @Override
            public BlockPos blockPos() {
                return blockPos;
            }

            @Override
            public net.minecraft.world.level.block.state.BlockState blockState() {
                return world.getBlockState(blockPos);
            }
        };
    }
}
