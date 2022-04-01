package com.ervelus.infrastructure;

import com.ervelus.infrastructure.annotations.PostConstruct;
import com.ervelus.infrastructure.configurators.ObjectConfigurator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ObjectFactory {
    private final ApplicationContext context;
    private final List<ObjectConfigurator> configurators =  new ArrayList<>();

    public ObjectFactory(ApplicationContext context) {
        this.context = context;
        for (Class<? extends ObjectConfigurator> aClass : context.getConfiguration().getScanner().getSubTypesOf(ObjectConfigurator.class)) {
            try {
                configurators.add(aClass.getDeclaredConstructor().newInstance());
            } catch (ReflectiveOperationException e) {
                System.err.println("Incorrect use of ObjectConfigurators: constructor should be public with no args");
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    public <T> T createObject(Class<T> implClass) throws ReflectiveOperationException{
        T t = create(implClass);
        configure(t);
        invokeInit(implClass, t);
        return t;
    }

    private <T> void configure(T t) {
        configurators.forEach(objectConfigurator -> objectConfigurator.configure(t,context));
    }

    private <T> T create(Class<T> implClass) throws ReflectiveOperationException {
        return implClass.getDeclaredConstructor().newInstance();
    }

    private <T> void invokeInit(Class<T> implClass, T t) throws ReflectiveOperationException {
        for (Method method : implClass.getMethods()) {
            if (method.isAnnotationPresent(PostConstruct.class)) {
                method.invoke(t);
            }
        }
    }
}
