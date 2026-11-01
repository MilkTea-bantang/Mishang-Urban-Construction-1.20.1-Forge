package pers.solid.mishang.uc.shim.fabricmc.fabric.api.tag.convention.v1;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;

/**
 * Forge shim for Fabric's conventional block tags. Each constant points at the equivalent Forge tag.
 */
public final class ConventionalBlockTags {
  private ConventionalBlockTags() {
  }

  public static final TagKey<Block> GLASS_BLOCKS = Tags.Blocks.GLASS;
  public static final TagKey<Block> GLASS_PANES = Tags.Blocks.GLASS_PANES;
}
