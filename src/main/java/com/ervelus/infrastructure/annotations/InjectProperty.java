package com.ervelus.infrastructure.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation for fields, used for injecting values from config files, by default - application.properties.
 * If name of the property is not specified, field name will be used
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectProperty {
    String  value() default "";
}
