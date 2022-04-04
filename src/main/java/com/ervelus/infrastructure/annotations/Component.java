package com.ervelus.infrastructure.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation for classes, marks that this class is singleton and its instance should be saved in context.
 * Components will be instantiated on creating dependent object.
 * To inject a component into class, use @InjectByType
 * @see InjectByType
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {
}
