package com.ervelus.infrastructure.configurators;

import com.ervelus.infrastructure.ApplicationContext;
import com.ervelus.infrastructure.annotations.InjectByType;

import java.lang.reflect.Field;

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
