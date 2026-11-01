package pers.solid.mishang.uc.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;

@Mixin(BeaconBlockEntity.class)
public interface BeaconBlockEntityAccessor {
  @Accessor("checkingBeamSections")
  List<BeaconBlockEntity.BeaconBeamSection> getCheckingBeamSegments();

  @Mixin(BeaconBlockEntity.BeaconBeamSection.class)
  interface BeamSegmentAccessor {
    @Invoker
    void invokeIncreaseHeight();
  }
}
