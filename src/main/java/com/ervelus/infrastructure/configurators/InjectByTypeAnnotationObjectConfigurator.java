package com.ervelus.infrastructure.configurators;

import com.ervelus.infrastructure.ApplicationContext;
import com.ervelus.infrastructure.annotations.InjectByType;

import java.lang.reflect.Field;

/**
 * Object Configurer for Dependency injection.
 * Injects a bean of given interface/class into the given field
 * If bean is not present in context - will be created using default constructor
 * Requires @InjectByType to be called
 * @see InjectByType
 */
public class InjectByTypeAnnotationObjectConfigurator implements ObjectConfigurator{
    @Override
    public void configure(Object t, ApplicationContext context) {
        for (Field field : t.getClass().getDeclaredFields()) {
            InjectByType annotation = field.getAnnotation(InjectByType.class);
            if (annotation != null) {
                try{
                    Object value = context.getObject(field.getType());
                    field.setAccessible(true);
                    field.set(t,value);
                } catch (ReflectiveOperationException e) {
                    System.err.println("Failed to inject by type");
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }
    }
}
