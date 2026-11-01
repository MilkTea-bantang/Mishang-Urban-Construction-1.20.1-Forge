package pers.solid.mishang.uc.shim.fabricmc.loader.api;

import java.nio.file.Path;

import pers.solid.mishang.uc.shim.fabricmc.api.EnvType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;

/**
 * Compatibility shim for Fabric's {@code FabricLoader}.
 * Only the subset of the API actually used by this mod is implemented.
 */
public class FabricLoader {
    private static final FabricLoader INSTANCE = new FabricLoader();

    public static FabricLoader getInstance() {
        return INSTANCE;
    }

    public EnvType getEnvironmentType() {
        return FMLEnvironment.dist == Dist.CLIENT ? EnvType.CLIENT : EnvType.SERVER;
    }

    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    public Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }
}
