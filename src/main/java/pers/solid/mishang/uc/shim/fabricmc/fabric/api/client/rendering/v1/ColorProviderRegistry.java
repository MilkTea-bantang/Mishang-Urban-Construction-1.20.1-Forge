package pers.solid.mishang.uc.shim.fabricmc.fabric.api.client.rendering.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Compatibility shim for Fabric's {@code ColorProviderRegistry}.
 *
 * <p>Registrations are buffered and flushed when Forge fires {@link RegisterColorHandlersEvent}, which
 * happens on the mod event bus.
 */
public final class ColorProviderRegistry<Provider, T> {
    private static final List<Consumer<RegisterColorHandlersEvent.Block>> BLOCK_REGISTRATIONS = new ArrayList<>();
    private static final List<Consumer<RegisterColorHandlersEvent.Item>> ITEM_REGISTRATIONS = new ArrayList<>();

    public static final ColorProviderRegistry<BlockColor, Block> BLOCK = new ColorProviderRegistry<>(true);
    public static final ColorProviderRegistry<ItemColor, ItemLike> ITEM = new ColorProviderRegistry<>(false);

    private final boolean blockColors;

    private ColorProviderRegistry(boolean blockColors) {
        this.blockColors = blockColors;
    }

    @SuppressWarnings("unchecked")
    public void register(Provider provider, T... objects) {
        if (blockColors) {
            final BlockColor blockColor = (BlockColor) provider;
            final Block[] blocks = (Block[]) objects;
            BLOCK_REGISTRATIONS.add(event -> event.register(blockColor, blocks));
        } else {
            final ItemColor itemColor = (ItemColor) provider;
            final ItemLike[] items = (ItemLike[]) objects;
            ITEM_REGISTRATIONS.add(event -> event.register(itemColor, items));
        }
    }

    static {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(
                EventPriority.NORMAL, false, RegisterColorHandlersEvent.Block.class,
                event -> BLOCK_REGISTRATIONS.forEach(registration -> registration.accept(event)));
        FMLJavaModLoadingContext.get().getModEventBus().addListener(
                EventPriority.NORMAL, false, RegisterColorHandlersEvent.Item.class,
                event -> ITEM_REGISTRATIONS.forEach(registration -> registration.accept(event)));
    }
}
