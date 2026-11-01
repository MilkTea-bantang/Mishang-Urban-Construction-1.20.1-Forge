package pers.solid.mishang.uc.shim.fabricmc.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Compatibility shim: marks a type/member as belonging to a specific environment.
 * On Forge this is purely informational (the mod splits client/server code by class loading).
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR})
public @interface Environment {
    EnvType value();
}
