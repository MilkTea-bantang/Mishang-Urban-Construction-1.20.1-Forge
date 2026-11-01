package pers.solid.mishang.uc.shim.fabricmc.fabric.api.tag.convention.v1;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.Tags;

/**
 * Forge shim for Fabric's conventional item tags. Each constant points at the equivalent Forge tag so
 * that recipes and tag providers written against the Fabric names keep working.
 */
public final class ConventionalItemTags {
  private ConventionalItemTags() {
  }

  public static final TagKey<Item> WHITE_DYES = Tags.Items.DYES_WHITE;
  public static final TagKey<Item> ORANGE_DYES = Tags.Items.DYES_ORANGE;
  public static final TagKey<Item> MAGENTA_DYES = Tags.Items.DYES_MAGENTA;
  public static final TagKey<Item> LIGHT_BLUE_DYES = Tags.Items.DYES_LIGHT_BLUE;
  public static final TagKey<Item> YELLOW_DYES = Tags.Items.DYES_YELLOW;
  public static final TagKey<Item> LIME_DYES = Tags.Items.DYES_LIME;
  public static final TagKey<Item> PINK_DYES = Tags.Items.DYES_PINK;
  public static final TagKey<Item> GRAY_DYES = Tags.Items.DYES_GRAY;
  public static final TagKey<Item> LIGHT_GRAY_DYES = Tags.Items.DYES_LIGHT_GRAY;
  public static final TagKey<Item> CYAN_DYES = Tags.Items.DYES_CYAN;
  public static final TagKey<Item> PURPLE_DYES = Tags.Items.DYES_PURPLE;
  public static final TagKey<Item> BLUE_DYES = Tags.Items.DYES_BLUE;
  public static final TagKey<Item> BROWN_DYES = Tags.Items.DYES_BROWN;
  public static final TagKey<Item> GREEN_DYES = Tags.Items.DYES_GREEN;
  public static final TagKey<Item> RED_DYES = Tags.Items.DYES_RED;
  public static final TagKey<Item> BLACK_DYES = Tags.Items.DYES_BLACK;

  public static final TagKey<Item> IRON_INGOTS = Tags.Items.INGOTS_IRON;
  public static final TagKey<Item> GOLD_INGOTS = Tags.Items.INGOTS_GOLD;
  public static final TagKey<Item> NETHERITE_INGOTS = Tags.Items.INGOTS_NETHERITE;

  public static final TagKey<Item> EMERALDS = Tags.Items.GEMS_EMERALD;
  public static final TagKey<Item> DIAMONDS = Tags.Items.GEMS_DIAMOND;
  public static final TagKey<Item> LAPIS = Tags.Items.GEMS_LAPIS;

  public static final TagKey<Item> GLASS_BLOCKS = Tags.Items.GLASS;
  public static final TagKey<Item> GLASS_PANES = Tags.Items.GLASS_PANES;
}
