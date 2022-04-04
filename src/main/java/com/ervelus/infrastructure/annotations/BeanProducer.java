package com.ervelus.infrastructure.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation for methods, that are used to instantiate a bean not from the project
 * Bean producers should be located in Configurations in order to make sure they are instantiated before the dependent object
 * To inject a bean into class, use @InjectByType
 * @see Configuration
 * @see InjectByType
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface BeanProducer {
}
