package com.ervelus.infrastructure.configurators;

import com.ervelus.infrastructure.ApplicationContext;
import com.ervelus.infrastructure.annotations.BeanProducer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BeanProducerAnnotationObjectConfigurator implements ObjectConfigurator{
    @Override
    public void configure(Object t, ApplicationContext context) {
        for (Method method : t.getClass().getDeclaredMethods()){
            BeanProducer annotation = method.getAnnotation(BeanProducer.class);
            if (annotation!=null){
                try {
                    context.putObject(method.invoke(t));
                } catch (ReflectiveOperationException e) {
                    System.err.println("Failed to invoke bean producer method");
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }
    }
}
