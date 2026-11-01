package pers.solid.mishang.uc.shim.fabricmc.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Compatibility shim for Fabric's {@code @EnvironmentInterface}.
 * Informational only on Forge.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface EnvironmentInterface {
    EnvType value();

    Class<?> itf();
}
