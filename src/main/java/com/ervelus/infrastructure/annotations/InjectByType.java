package com.ervelus.infrastructure.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation for fields, used for dependency injection
 * Does not require setter or constructor to be injected
 * Proper object will be created using default constructor if not present,
 * if you need to configure it - consider creating it using @BeanProducer
 * @see BeanProducer
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectByType {
}
