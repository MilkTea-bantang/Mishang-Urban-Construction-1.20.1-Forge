package pers.solid.mishang.uc.data;

import pers.solid.mishang.uc.shim.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import pers.solid.mishang.uc.shim.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class MishangucDataGeneration implements DataGeneratorEntrypoint {
  @Override
  public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
    final FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
    pack.addProvider(MishangucBlockLootTableProvider::new);
    pack.addProvider(MishangucRecipeProvider::new);
    pack.addProvider(MishangucModelProvider::new);
    final MishangucBlockTagProvider mishangucBlockTagProvider = pack.addProvider(MishangucBlockTagProvider::new);
    pack.addProvider((output, registriesFuture) -> mishangucBlockTagProvider.affiliate);
  }
}
