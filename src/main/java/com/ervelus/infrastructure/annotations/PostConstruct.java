package com.ervelus.infrastructure.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation for methods, that includes init logic but should be called after the object is created and configured.
 * Method, annotated with @PostConstruct, will be invoked automatically after all Object Configurators
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface PostConstruct {
}
