package pers.solid.mishang.uc.item;

import pers.solid.mishang.uc.shim.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import org.jetbrains.annotations.ApiStatus;
import pers.solid.mishang.uc.Mishanguc;
import pers.solid.mishang.uc.annotations.CustomId;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class MishangucItems {
  public static final RoadConnectionStateDebuggingToolItem ROAD_CONNECTION_STATE_DEBUGGING_TOOL =
      new RoadConnectionStateDebuggingToolItem(
          new FabricItemSettings().stacksTo(1), false);

  public static final IdCheckerToolItem ID_CHECKER_TOOL =
      new IdCheckerToolItem(new FabricItemSettings().stacksTo(1), null);

  public static final IdCheckerToolItem FLUID_ID_CHECKER_TOOL =
      new IdCheckerToolItem(new FabricItemSettings().stacksTo(1), true);

  public static final FastBuildingToolItem FAST_BUILDING_TOOL =
      new FastBuildingToolItem(new FabricItemSettings().stacksTo(1), null);

  public static final ColumnBuildingTool COLUMN_BUILDING_TOOL = new ColumnBuildingTool(new FabricItemSettings().stacksTo(1), null);

  public static final RotatingToolItem ROTATING_TOOL =
      new RotatingToolItem(new FabricItemSettings().durability(512), null);

  public static final MirroringToolItem MIRRORING_TOOL =
      new MirroringToolItem(new FabricItemSettings().durability(512), null);

  public static final SlabToolItem SLAB_TOOL =
      new SlabToolItem(new FabricItemSettings().durability(1024));

  public static final ForcePlacingToolItem FORCE_PLACING_TOOL =
      new ForcePlacingToolItem(new FabricItemSettings().stacksTo(1), null);

  public static final ForcePlacingToolItem FLUID_FORCE_PLACING_TOOL =
      new ForcePlacingToolItem(new FabricItemSettings().stacksTo(1), true);

  public static final BlockStateToolItem BLOCK_STATE_TOOL =
      new BlockStateToolItem(new FabricItemSettings().stacksTo(1), null);

  public static final BlockStateToolItem FLUID_STATE_TOOL =
      new BlockStateToolItem(new FabricItemSettings().stacksTo(1), true);

  public static final DataTagToolItem DATA_TAG_TOOL =
      new DataTagToolItem(new FabricItemSettings().stacksTo(1), null);

  public static final TextCopyToolItem TEXT_COPY_TOOL = new TextCopyToolItem(new FabricItemSettings().durability(1024), null);

  public static final OmnipotentToolItem OMNIPOTENT_TOOL = new OmnipotentToolItem(new FabricItemSettings().fireResistant().rarity(Rarity.EPIC).stacksTo(1));

  public static final ExplosionToolItem EXPLOSION_TOOL = new ExplosionToolItem(new FabricItemSettings().durability(1024));

  @ApiStatus.AvailableSince("0.2.1")
  public static final ColorToolItem COLOR_TOOL = new ColorToolItem(new FabricItemSettings().durability(1024), null);

  @ApiStatus.AvailableSince("0.2.4")
  public static final RoadToolItem ROAD_TOOL = new RoadToolItem(new FabricItemSettings().durability(512));
  @ApiStatus.AvailableSince("0.2.4")
  public static final TpToolItem TP_TOOL = new TpToolItem(new FabricItemSettings().durability(2048));
  @ApiStatus.AvailableSince("0.2.4")
  public static final GrowthToolItem GROWTH_TOOL = new GrowthToolItem(new FabricItemSettings().durability(1024));
  @ApiStatus.AvailableSince("0.2.4")
  public static final CarryingToolItem CARRYING_TOOL = new CarryingToolItem(new FabricItemSettings().stacksTo(1), null);
  public static final IceSnowTool ICE_SNOW_TOOL = new IceSnowTool(new FabricItemSettings().durability(128));

  private MishangucItems() {
  }

  private static void registerAll() {
    for (Field field : MishangucItems.class.getFields()) {
      int modifier = field.getModifiers();
      if (Modifier.isFinal(modifier)
          && Modifier.isStatic(modifier)
          && Item.class.isAssignableFrom(field.getType())) {
        try {
          // 注册物品。
          Item value = (Item) field.get(null);
          {
            final CustomId annotation = field.getAnnotation(CustomId.class);
            String namespace, path;
            if (field.isAnnotationPresent(CustomId.class)) {
              namespace = annotation.nameSpace();
              path = annotation.path();
            } else {
              namespace = "mishanguc";
              path = field.getName().toLowerCase();
            }
            Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(namespace, path), value);
          }
        } catch (IllegalAccessException e) {
          Mishanguc.MISHANG_LOGGER.error("Error when registering items:", e);
        }
      }
    }
  }

  public static void init() {
    registerAll();
  }
}
