package pers.solid.mishang.uc.block;

import pers.solid.mishang.uc.shim.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.data.models.model.*;
import net.minecraft.data.models.blockstates.*;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ModelProvider;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.solid.mishang.uc.Mishanguc;
import pers.solid.mishang.uc.data.MishangucModels;
import pers.solid.mishang.uc.util.TextBridge;

import java.util.function.Function;

@ApiStatus.AvailableSince("0.2.4")
public class GlassHandrailBlock extends HandrailBlock {
  public final ResourceLocation decorationTexture;
  private final CentralBlock central;
  private final CornerBlock corner;
  private final StairBlock stair;
  private final OuterBlock outer;
  private final Block baseBlock;
  private final ResourceLocation frameTexture;

  public GlassHandrailBlock(Block baseBlock, Properties settings, String frameTexture, String decorationTexture) {
    super(settings);
    this.baseBlock = baseBlock;
    this.frameTexture = new ResourceLocation(frameTexture);
    this.decorationTexture = new ResourceLocation(decorationTexture);
    this.central = new CentralBlock(this);
    this.corner = new CornerBlock(this);
    this.stair = new StairBlock(this);
    this.outer = new OuterBlock(this);
  }

  protected GlassHandrailBlock(Block baseBlock, Properties settings, String frameTexture, String decorationTexture, Function<GlassHandrailBlock, CentralBlock> centralProvider, Function<GlassHandrailBlock, CornerBlock> cornerProvider, Function<GlassHandrailBlock, StairBlock> stairProvider, Function<GlassHandrailBlock, OuterBlock> outerProvider) {
    super(settings.noOcclusion());
    this.baseBlock = baseBlock;
    this.frameTexture = new ResourceLocation(frameTexture);
    this.decorationTexture = new ResourceLocation(decorationTexture);
    central = centralProvider.apply(this);
    corner = cornerProvider.apply(this);
    stair = stairProvider.apply(this);
    outer = outerProvider.apply(this);
  }

  @Override
  public void registerModels(ModelProvider modelProvider, BlockModelGenerators blockStateModelGenerator) {
    final TextureMapping textures = getTextures();
    final ResourceLocation modelId = MishangucModels.GLASS_HANDRAIL.create(this, textures, blockStateModelGenerator.modelOutput);
    MishangucModels.GLASS_HANDRAIL_INVENTORY.create(ModelLocationUtils.getModelLocation(asItem()), textures, blockStateModelGenerator.modelOutput);
    blockStateModelGenerator.blockStateOutput.accept(createBlockStates(modelId));
  }

  public static final TextureSlot FRAME = TextureSlot.create("frame");
  public static final TextureSlot GLASS = TextureSlot.create("glass");
  public static final TextureSlot DECORATION = TextureSlot.create("decoration");

  @Override
  public @NotNull TextureMapping getTextures() {
    return new TextureMapping().put(FRAME, frameTexture).put(GLASS, Mishanguc.id("block/glass_unframed")).put(DECORATION, decorationTexture);
  }

  @Override
  public HandrailCentralBlock<? extends HandrailBlock> central() {
    return central;
  }

  @Override
  public HandrailCornerBlock<? extends HandrailBlock> corner() {
    return corner;
  }

  @Override
  public HandrailStairBlock<? extends HandrailBlock> stair() {
    return stair;
  }

  @Override
  public HandrailOuterBlock<? extends HandrailBlock> outer() {
    return outer;
  }

  @Override
  public @Nullable Block baseBlock() {
    return baseBlock;
  }

  public static class CentralBlock extends HandrailCentralBlock<GlassHandrailBlock> {

    @Override
    public MutableComponent getName() {
      return TextBridge.translatable("block.mishanguc.handrail_central", baseHandrail.getName());
    }

    protected CentralBlock(@NotNull GlassHandrailBlock baseRail) {
      super(baseRail, FabricBlockSettings.copyOf(baseRail).noOcclusion());
    }

    @Override
    public void registerModels(ModelProvider modelProvider, BlockModelGenerators blockStateModelGenerator) {
      final TextureMapping textures = baseHandrail.getTextures();
      final ResourceLocation postModelId = MishangucModels.GLASS_HANDRAIL_POST.create(this, textures, blockStateModelGenerator.modelOutput);
      final ResourceLocation sideModelId = MishangucModels.GLASS_HANDRAIL_SIDE.create(this, textures, blockStateModelGenerator.modelOutput);
      final ResourceLocation postSideModelId = MishangucModels.GLASS_HANDRAIL_POST_SIDE.create(this, textures, blockStateModelGenerator.modelOutput);
      blockStateModelGenerator.blockStateOutput.accept(createBlockStates(postModelId, postSideModelId, sideModelId));
    }
  }

  public static class CornerBlock extends HandrailCornerBlock<GlassHandrailBlock> {

    @Override
    public MutableComponent getName() {
      return TextBridge.translatable("block.mishanguc.handrail_corner", baseHandrail.getName());
    }

    protected CornerBlock(@NotNull GlassHandrailBlock baseRail) {
      super(baseRail, FabricBlockSettings.copyOf(baseRail).noOcclusion());
    }

    @Override
    public void registerModels(ModelProvider modelProvider, BlockModelGenerators blockStateModelGenerator) {
      final ResourceLocation modelId = MishangucModels.GLASS_HANDRAIL_CORNER.create(this, baseHandrail.getTextures(), blockStateModelGenerator.modelOutput);
      blockStateModelGenerator.blockStateOutput.accept(createBlockStates(modelId));
    }
  }

  public static class StairBlock extends HandrailStairBlock<GlassHandrailBlock> {

    protected StairBlock(@NotNull GlassHandrailBlock baseRail) {
      super(baseRail, FabricBlockSettings.copyOf(baseRail).noOcclusion());
    }

    @Override
    public MutableComponent getName() {
      return TextBridge.translatable("block.mishanguc.handrail_stair", baseHandrail.getName());
    }

    @Override
    public void registerModels(ModelProvider modelProvider, BlockModelGenerators blockStateModelGenerator) {
      final TextureMapping textures = baseHandrail.getTextures();
      final ResourceLocation baseModelId = MishangucModels.createBlock("glass_handrail_stair_middle_center", FRAME, GLASS, DECORATION).create(this, textures, blockStateModelGenerator.modelOutput);
      for (Shape shape : Shape.values()) {
        for (Position position : Position.values()) {
          MishangucModels.createBlock(String.format("glass_handrail_stair_%s_%s", shape.getSerializedName(), position.getSerializedName()), "_" + shape.getSerializedName() + "_" + position.getSerializedName(), FRAME, GLASS, DECORATION).create(this, textures, blockStateModelGenerator.modelOutput);
        }
      }
      blockStateModelGenerator.blockStateOutput.accept(createBlockStates(baseModelId));
    }
  }

  public static class OuterBlock extends HandrailOuterBlock<GlassHandrailBlock> {

    protected OuterBlock(@NotNull GlassHandrailBlock baseRail) {
      super(baseRail, FabricBlockSettings.copyOf(baseRail).noOcclusion());
    }

    @Override
    public MutableComponent getName() {
      return TextBridge.translatable("block.mishanguc.handrail_outer", baseHandrail.getName());
    }

    @Override
    public void registerModels(ModelProvider modelProvider, BlockModelGenerators blockStateModelGenerator) {
      final ResourceLocation modelId = MishangucModels.GLASS_HANDRAIL_OUTER.create(this, baseHandrail.getTextures(), blockStateModelGenerator.modelOutput);
      blockStateModelGenerator.blockStateOutput.accept(createBlockStates(modelId));
    }
  }
}
