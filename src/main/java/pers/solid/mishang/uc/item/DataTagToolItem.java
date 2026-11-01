package pers.solid.mishang.uc.item;

import pers.solid.mishang.uc.shim.fabricmc.api.EnvType;
import pers.solid.mishang.uc.shim.fabricmc.api.Environment;
import pers.solid.mishang.uc.shim.fabricmc.api.EnvironmentInterface;
import pers.solid.mishang.uc.shim.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import pers.solid.mishang.uc.shim.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import pers.solid.mishang.uc.shim.fabricmc.fabric.api.networking.v1.PacketSender;
import pers.solid.mishang.uc.shim.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.data.BlockDataAccessor;
import net.minecraft.server.commands.data.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.solid.mishang.uc.render.RendersBeforeOutline;
import pers.solid.mishang.uc.util.NbtPrettyPrinter;
import pers.solid.mishang.uc.util.TextBridge;

import java.util.List;

@EnvironmentInterface(value = EnvType.CLIENT, itf = RendersBeforeOutline.class)
public class DataTagToolItem extends BlockToolItemWithEntity implements InteractsWithEntity, RendersBeforeOutline {
  public DataTagToolItem(Properties settings, @Nullable Boolean includesFluid) {
    super(settings, includesFluid);
  }

  @Override
  public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
    super.appendHoverText(stack, world, tooltip, context);
    tooltip.add(TextBridge.translatable("item.mishanguc.data_tag_tool.tooltip").withStyle(ChatFormatting.GRAY));
  }

  @Override
  public InteractionResult useOnBlock(ItemStack stack, Player player, Level world, BlockHitResult blockHitResult, InteractionHand hand, boolean fluidIncluded) {
    if (!world.isClientSide) {
      return getBlockDataOf((ServerPlayer) player, (ServerLevel) world, blockHitResult.getBlockPos());
    } else {
      return InteractionResult.SUCCESS;
    }
  }

  @Override
  public InteractionResult beginAttackBlock(ItemStack stack, Player player, Level world, InteractionHand hand, BlockPos pos, Direction direction, boolean fluidIncluded) {
    if (!world.isClientSide) return getBlockDataOf((ServerPlayer) player, (ServerLevel) world, pos);
    else return InteractionResult.SUCCESS;
  }

  public InteractionResult getBlockDataOf(ServerPlayer player, ServerLevel world, BlockPos blockPos) {
    final @Nullable BlockEntity blockEntity = world.getBlockEntity(blockPos);
    final FriendlyByteBuf buf = PacketByteBufs.create();
    buf.writeResourceLocation(BuiltInRegistries.BLOCK.getKey(world.getBlockState(blockPos).getBlock()));
    buf.writeBlockPos(blockPos);
    if (blockEntity == null) {
      buf.writeBoolean(false);
      ServerPlayNetworking.send(player, new ResourceLocation("mishanguc", "get_block_data"), buf);
    } else {
      final BlockDataAccessor blockDataObject = new BlockDataAccessor(world.getBlockEntity(blockPos), blockPos);
      buf.writeBoolean(true);
      buf.writeNbt(blockDataObject.getData());
      ServerPlayNetworking.send(player, new ResourceLocation("mishanguc", "get_block_data"), buf);
    }
    return InteractionResult.SUCCESS;
  }

  public InteractionResult getEntityDataOf(ServerPlayer player, Entity entity) {
    final EntityDataAccessor entityDataObject = new EntityDataAccessor(entity);
    final CompoundTag nbt = entityDataObject.getData();
    final FriendlyByteBuf buf = PacketByteBufs.create();
    buf.writeComponent(entity.getName());
    buf.writeBlockPos(entity.blockPosition());
    buf.writeNbt(nbt);
    ServerPlayNetworking.send(player, new ResourceLocation("mishanguc", "get_entity_data"), buf);
    return InteractionResult.SUCCESS;
  }

  @Override
  public @NotNull InteractionResult attackEntityCallback(Player player, Level world, InteractionHand hand, Entity entity, @Nullable EntityHitResult hitResult) {
    if (player.isSpectator()) return InteractionResult.PASS;
    else if (!world.isClientSide) return getEntityDataOf((ServerPlayer) player, entity);
    else return InteractionResult.SUCCESS;
  }

  @Override
  public @NotNull InteractionResult useEntityCallback(Player player, Level world, InteractionHand hand, Entity entity, @Nullable EntityHitResult hitResult) {
    if (!world.isClientSide && !player.isSpectator()) return getEntityDataOf((ServerPlayer) player, entity);
    else return InteractionResult.SUCCESS;
  }

  /**
   * 用于接收服务器的 {@code mishanguc:get_block_data} 的数据包。用户使用该工具点击方块后，服务器获取其数据并传给客户端，客户端收到数据后，将消息反馈至聊天框。
   */
  @Environment(EnvType.CLIENT)
  @ApiStatus.AvailableSince("0.1.7")
  public static class BlockDataReceiver implements ClientPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
      final ResourceLocation blockId = buf.readResourceLocation();
      final BlockPos blockPos = buf.readBlockPos();
      final boolean hasData = buf.readBoolean();
      final Block block = BuiltInRegistries.BLOCK.get(blockId);
      if (hasData) {
        // 由于此处仅限客户端执行，因此可以放心调用 Block#getName。
        final CompoundTag blockData = buf.readNbt();
        client.execute(() -> {
          client.gui.getChat().addMessage(TextBridge.translatable("debug.mishanguc.dataTag.block.header", String.format("%s %s %s", blockPos.getX(), blockPos.getY(), blockPos.getZ()), block.getName().withStyle(ChatFormatting.BOLD)).withStyle(ChatFormatting.YELLOW));
          client.gui.getChat().addMessage(NbtPrettyPrinter.serialize(blockData));
        });
      } else {
        // 此时认为该方块没有数据。
        client.execute(() -> client.gui.getChat().addMessage(TextBridge.translatable("debug.mishanguc.dataTag.block.null", String.format("%s %s %s", blockPos.getX(), blockPos.getY(), blockPos.getZ()), block.getName().withStyle(ChatFormatting.BOLD)).withStyle(ChatFormatting.RED)));
      }
    }
  }

  /**
   * 用于接收服务器的 {@code mishanguc:get_entity_data} 数据包。用户使用该工具点击实体后，服务器获取其数据并传给客户端，客户端收到数据后，将消息反馈至聊天框。
   */
  @Environment(EnvType.CLIENT)
  @ApiStatus.AvailableSince("0.1.7")
  public static class EntityDataReceiver implements ClientPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
      final Component entityName = buf.readComponent();
      final BlockPos entityPos = buf.readBlockPos();
      final CompoundTag entityNbt = buf.readNbt();
      client.gui.getChat().addMessage(TextBridge.translatable("debug.mishanguc.dataTag.entity.entity", String.format("%s %s %s", entityPos.getX(), entityPos.getY(), entityPos.getZ()), TextBridge.literal("").append(entityName).withStyle(ChatFormatting.BOLD)).withStyle(ChatFormatting.YELLOW));
      client.gui.getChat().addMessage(NbtPrettyPrinter.serialize(entityNbt));
    }
  }
}
