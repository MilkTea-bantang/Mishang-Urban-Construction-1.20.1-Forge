package pers.solid.mishang.uc.shim.fabricmc.fabric.api.blockrenderlayer.v1;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;

/**
 * Compatibility shim for Fabric's {@code BlockRenderLayerMap}, backed by Forge's
 * {@link ItemBlockRenderTypes#setRenderLayer(Block, RenderType)}.
 */
public interface BlockRenderLayerMap {
    BlockRenderLayerMap INSTANCE = new BlockRenderLayerMap() {
        @Override
        public void putBlock(Block block, RenderType renderLayer) {
            ItemBlockRenderTypes.setRenderLayer(block, renderLayer);
        }

        @Override
        public void putBlocks(RenderType renderLayer, Block... blocks) {
            for (final Block block : blocks) {
                ItemBlockRenderTypes.setRenderLayer(block, renderLayer);
            }
        }
    };

    void putBlock(Block block, RenderType renderLayer);

    void putBlocks(RenderType renderLayer, Block... blocks);
}
