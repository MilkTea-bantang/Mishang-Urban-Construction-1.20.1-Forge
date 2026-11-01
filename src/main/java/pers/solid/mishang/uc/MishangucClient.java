package pers.solid.mishang.uc;

import com.google.common.base.Predicates;
import pers.solid.mishang.uc.shim.fabricmc.api.ClientModInitializer;
import pers.solid.mishang.uc.shim.fabricmc.api.EnvType;
import pers.solid.mishang.uc.shim.fabricmc.api.Environment;
import pers.solid.mishang.uc.shim.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import pers.solid.mishang.uc.shim.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import pers.solid.mishang.uc.shim.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import pers.solid.mishang.uc.shim.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import pers.solid.mishang.uc.shim.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.Util;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;
import pers.solid.mishang.uc.block.AbstractRoadBlock;
import pers.solid.mishang.uc.block.ColoredBlock;
import pers.solid.mishang.uc.block.StandingSignBlock;
import pers.solid.mishang.uc.blockentity.*;
import pers.solid.mishang.uc.blocks.MishangucBlocks;
import pers.solid.mishang.uc.item.CarryingToolItem;
import pers.solid.mishang.uc.item.DataTagToolItem;
import pers.solid.mishang.uc.item.MishangucItems;
import pers.solid.mishang.uc.render.*;
import pers.solid.mishang.uc.screen.HungSignBlockEditScreen;
import pers.solid.mishang.uc.screen.SignPresets;
import pers.solid.mishang.uc.screen.StandingSignBlockEditScreen;
import pers.solid.mishang.uc.screen.WallSignBlockEditScreen;

import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;

@Environment(EnvType.CLIENT)
public class MishangucClient implements ClientModInitializer {
  /**
   * @see MishangucRules#FORCE_PLACING_TOOL_ACCESS
   */
  public static final AtomicReference<MishangucRules.ToolAccess> CLIENT_FORCE_PLACING_TOOL_ACCESS = new AtomicReference<>(MishangucRules.ToolAccess.CREATIVE_ONLY);
  /**
   * @see MishangucRules#CARRYING_TOOL_ACCESS
   */
  public static final AtomicReference<MishangucRules.ToolAccess> CLIENT_CARRYING_TOOL_ACCESS = new AtomicReference<>(MishangucRules.ToolAccess.ALL);

  @Override
  public void onInitializeClient() {
    registerBlockLayers();

    registerRenderEvents();

    registerBlockEntityRenderers();

    registerBlockColors();

    registerNetworking();

    registerModelPredicateProviders();
  }

  private static void registerModelPredicateProviders() {
    // 模型谓词提供器
    ItemProperties.register(MishangucItems.EXPLOSION_TOOL,
        Mishanguc.id("explosion_power"),
        new ClampedItemPropertyFunction() {
          @Override
          public float unclampedCall(ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
            return MishangucItems.EXPLOSION_TOOL.power(stack);
          }

          @SuppressWarnings("deprecation")
          @Override
          public float call(ItemStack itemStack, @Nullable ClientLevel clientWorld, @Nullable LivingEntity livingEntity, int i) {
            return unclampedCall(itemStack, clientWorld, livingEntity, i);
          }
        });

    SignPresets.loadAll();

    ClientCommandRegistrationCallback.EVENT.register(SignPresetCommand.INSTANCE);

    ItemProperties.register(MishangucItems.EXPLOSION_TOOL, Mishanguc.id("explosion_create_fire"), (stack, world, entity, seed) -> MishangucItems.EXPLOSION_TOOL.createFire(stack) ? 1 : 0);
    ItemProperties.register(MishangucItems.FAST_BUILDING_TOOL, Mishanguc.id("fast_building_range"), (stack, world, entity, seed) -> MishangucItems.FAST_BUILDING_TOOL.getRange(stack) / 64f);
    ItemProperties.register(MishangucItems.CARRYING_TOOL, Mishanguc.id("is_holding_block"), (stack, world, entity, seed) -> BooleanUtils.toInteger(CarryingToolItem.hasHoldingBlockState(stack)));
    ItemProperties.register(MishangucItems.CARRYING_TOOL, Mishanguc.id("is_holding_entity"), (stack, world, entity, seed) -> BooleanUtils.toInteger(CarryingToolItem.hasHoldingEntity(stack)));
  }

  private static void registerNetworking() {
    // 网络通信
    // 客户端收到服务器发来的编辑告示牌的数据包时，打开编辑界面，允许用户编辑。
    ClientPlayNetworking.registerGlobalReceiver(
        new ResourceLocation("mishanguc", "edit_sign"),
        (client, handler, buf, responseSender) -> {
          try {
            final BlockPos blockPos = buf.readBlockPos();
            final BlockEntity blockEntity =
                client.level != null ? client.level.getBlockEntity(blockPos) : null;
            if (blockEntity instanceof final HungSignBlockEntity hungSignBlockEntity) {
              final Direction direction = buf.readEnum(Direction.class);
              client.execute(() ->
                  client.setScreen(new HungSignBlockEditScreen(hungSignBlockEntity, direction, blockPos)));
            } else if (blockEntity instanceof final WallSignBlockEntity wallSignBlockEntity) {
              client.execute(() ->
                  client.setScreen(new WallSignBlockEditScreen(wallSignBlockEntity, blockPos)));
            } else if (blockEntity instanceof final StandingSignBlockEntity standingSignBlockEntity) {
              final BlockHitResult blockHitResult = buf.readBlockHitResult();
              final Boolean isFront = StandingSignBlock.getHitSide(blockEntity.getBlockState(), blockHitResult);
              if (isFront != null) {
                client.execute(() -> client.setScreen(new StandingSignBlockEditScreen(standingSignBlockEntity, blockPos, isFront)));
              }
            }
          } catch (NullPointerException | ClassCastException exception) {
            Mishanguc.MISHANG_LOGGER.error("Error when creating sign edit screen:", exception);
          }
        });
    ClientPlayNetworking.registerGlobalReceiver(new ResourceLocation("mishanguc", "get_block_data"), new DataTagToolItem.BlockDataReceiver());
    ClientPlayNetworking.registerGlobalReceiver(new ResourceLocation("mishanguc", "get_entity_data"), new DataTagToolItem.EntityDataReceiver());
    ClientPlayNetworking.registerGlobalReceiver(new ResourceLocation("mishanguc", "rule_changed"), MishangucRules::handle);
  }

  private static void registerBlockColors() {
    // 注册方块和颜色
    final Block[] coloredBlocks = MishangUtils.blocks().stream().filter(Predicates.instanceOf(ColoredBlock.class))
        .toArray(Block[]::new);
    ColorProviderRegistry.BLOCK.register(
        (state, world, pos, tintIndex) -> {
          if (world == null || pos == null) return -1;
          BlockEntity entity = world.getBlockEntity(pos);
          // 考虑到玩家掉落产生粒子时，坐标会向上偏离一格。
          if (entity == null) entity = world.getBlockEntity(pos.below());
          if (entity instanceof ColoredBlockEntity coloredBlockEntity) {
            return coloredBlockEntity.getColor();
          } else {
            // 考虑到坐标本身的位置没有方块颜色，因此根据附近坐标来推断方块颜色。
            // 受部分渲染器影响，方块颜色会与周围插值，故需确保有自定义颜色的方块周围也会带有相同的自定义颜色。
            int accumulatedNum = 0;
            int accumulatedRed = 0;
            int accumulatedGreen = 0;
            int accumulatedBlue = 0;
            for (BlockPos outPos : BlockPos.withinManhattan(pos, 1, 1, 1)) {
              if (outPos.equals(pos)) continue;
              if (world.getBlockEntity(outPos) instanceof ColoredBlockEntity coloredBlockEntity) {
                final int color = coloredBlockEntity.getColor();
                accumulatedNum += 1;
                accumulatedRed += color >> 16 & 255;
                accumulatedGreen += color >> 8 & 255;
                accumulatedBlue += color & 255;
              }
            }
            if (accumulatedNum > 0) {
              return (accumulatedRed / accumulatedNum << 16) + (accumulatedGreen / accumulatedNum << 8) + accumulatedBlue / accumulatedNum;
            } else {
              return -1;
            }
          }
        },
        coloredBlocks
    );
    ColorProviderRegistry.ITEM.register(
        (stack, tintIndex) -> {
          final CompoundTag nbt = stack.getTagElement("BlockEntityTag");
          if (nbt != null && nbt.contains("color", Tag.TAG_ANY_NUMERIC)) {
            return 0xff000000 | nbt.getInt("color");
          }
          return Color.HSBtoRGB(Util.getMillis() / 4096f + (stack.getItem().hashCode() >> 16) / 64f, 0.5f, 0.95f);
        },
        coloredBlocks
    );
  }

  private static void registerBlockEntityRenderers() {
    // 注册方块实体渲染器
    BlockEntityRenderers.register(MishangucBlockEntities.HUNG_SIGN_BLOCK_ENTITY, HungSignBlockEntityRenderer::new);
    BlockEntityRenderers.register(MishangucBlockEntities.COLORED_HUNG_SIGN_BLOCK_ENTITY, HungSignBlockEntityRenderer::new);
    BlockEntityRenderers.register(MishangucBlockEntities.WALL_SIGN_BLOCK_ENTITY, WallSignBlockEntityRenderer::new);
    BlockEntityRenderers.register(MishangucBlockEntities.COLORED_WALL_SIGN_BLOCK_ENTITY, WallSignBlockEntityRenderer::new);
    BlockEntityRenderers.register(MishangucBlockEntities.FULL_WALL_SIGN_BLOCK_ENTITY, WallSignBlockEntityRenderer<FullWallSignBlockEntity>::new);
    BlockEntityRenderers.register(MishangucBlockEntities.STANDING_SIGN_BLOCK_ENTITY, StandingSignBlockEntityRenderer::new);
    BlockEntityRenderers.register(MishangucBlockEntities.COLORED_STANDING_SIGN_BLOCK_ENTITY, StandingSignBlockEntityRenderer::new);
  }

  private static void registerRenderEvents() {
    // 注册方块外观描绘
    WorldRenderEvents.BLOCK_OUTLINE.register(RendersBlockOutline.RENDERER);
    WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register(RendersBeforeOutline.RENDERER);
  }

  private static void registerBlockLayers() {
    // 设置相应的 BlockLayer
    Validate.notEmpty(MishangucBlocks.translucentBlocks).forEach(block -> BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.translucent()));
    Validate.notEmpty(MishangucBlocks.cutoutBlocks).forEach(block -> {
      BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.cutout());
      if (block instanceof AbstractRoadBlock roadBlock && roadBlock.getRoadSlab() != null) {
        BlockRenderLayerMap.INSTANCE.putBlock(roadBlock.getRoadSlab(), RenderType.cutout());
      }
    });
    MishangucBlocks.translucentBlocks = null;
    MishangucBlocks.cutoutBlocks = null;
  }
}
