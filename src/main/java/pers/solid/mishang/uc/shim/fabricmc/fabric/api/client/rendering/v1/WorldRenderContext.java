package pers.solid.mishang.uc.shim.fabricmc.fabric.api.client.rendering.v1;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Compatibility shim for Fabric's {@code WorldRenderContext}, exposing only the members the
 * ported code relies on.
 */
public interface WorldRenderContext {
    MultiBufferSource consumers();

    ClientLevel world();

    PoseStack matrixStack();

    Camera camera();

    /**
     * Compatibility shim for Fabric's {@code WorldRenderContext.BlockOutlineContext}.
     */
    interface BlockOutlineContext {
        Entity entity();

        double cameraX();

        double cameraY();

        double cameraZ();

        BlockPos blockPos();

        BlockState blockState();
    }
}
