package pers.solid.mishang.uc.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import pers.solid.mishang.uc.shim.fabricmc.api.EnvType;
import pers.solid.mishang.uc.shim.fabricmc.api.Environment;
import pers.solid.mishang.uc.shim.fabricmc.api.EnvironmentInterface;
import pers.solid.mishang.uc.shim.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import org.jetbrains.annotations.Nullable;
import pers.solid.mishang.uc.mixin.WorldRendererInvoker;
import pers.solid.mishang.uc.render.RendersBeforeOutline;

@EnvironmentInterface(itf = RendersBeforeOutline.class, value = EnvType.CLIENT)
public abstract class BlockToolItemWithEntity extends BlockToolItem implements RendersBeforeOutline {
  public BlockToolItemWithEntity(Properties settings, @Nullable Boolean includesFluid) {
    super(settings, includesFluid);
  }

  @Environment(EnvType.CLIENT)
  @Override
  public void renderBeforeOutline(WorldRenderContext context, HitResult hitResult, LocalPlayer player, InteractionHand hand) {
    if (hitResult instanceof EntityHitResult entityHitResult && !player.isSpectator()) {
      final Entity entity = entityHitResult.getEntity();
      final PoseStack matrices = context.matrixStack();
      final MultiBufferSource consumers = context.consumers();
      if (matrices == null || consumers == null) return;
      final VertexConsumer vertexConsumer = consumers.getBuffer(RenderType.lines());
      final Vec3 cameraPos = context.camera().getPosition();
      final float tickProgress = Minecraft.getInstance().getFrameTime();
      WorldRendererInvoker.drawCuboidShapeOutline(matrices, vertexConsumer, Shapes.create(entity.getBoundingBox().move(entity.getPosition(tickProgress).subtract(entity.position()))), -cameraPos.x, -cameraPos.y, -cameraPos.z, 0f, 1f, 0f, 0.8f);
    }
  }
}
