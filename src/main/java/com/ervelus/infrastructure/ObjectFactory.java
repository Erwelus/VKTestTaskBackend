package com.ervelus.infrastructure;

import com.ervelus.infrastructure.annotations.PostConstruct;
import com.ervelus.infrastructure.configurators.ObjectConfigurator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Main factory of the project
 */
public class ObjectFactory {
    /**
     * Context to store the beans
     */
    private final ApplicationContext context;
    /**
     * List of configurators used for configuring the object after instantiating
     * Filled automatically by all implementations of ObjectConfigurator
     * @see ObjectConfigurator
     */
    private final List<ObjectConfigurator> configurators =  new ArrayList<>();

    public ObjectFactory(ApplicationContext context) {
        this.context = context;
        for (Class<? extends ObjectConfigurator> aClass : context.getConfig().getScanner().getSubTypesOf(ObjectConfigurator.class)) {
            try {
                configurators.add(aClass.getDeclaredConstructor().newInstance());
            } catch (ReflectiveOperationException e) {
                System.err.println("Incorrect use of ObjectConfigurators: constructor should be public with no args");
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    /**
     * Main method used for creating objects.
     * First we instantiate it with default constructor.
     * Then we configure it using Object Configurators.
     * Finally, invoke PostConstruct methods for init logic that should be performed after injections
     * @param implClass type of the object
     * @return instance of the object
     */
    public <T> T createObject(Class<T> implClass) throws ReflectiveOperationException{
        T t = create(implClass);
        configure(t);
        invokeInit(implClass, t);
        return t;
    }

    /**
     * Invocation of all Object Configurators
     */
    private <T> void configure(T t) {
        configurators.forEach(objectConfigurator -> objectConfigurator.configure(t,context));
    }

    /**
     * Instantiation of the object
     */
    private <T> T create(Class<T> implClass) throws ReflectiveOperationException {
        return implClass.getDeclaredConstructor().newInstance();
    }

    /**
     * Invocation of PostConstruct methods
     */
    private <T> void invokeInit(Class<T> implClass, T t) throws ReflectiveOperationException {
        for (Method method : implClass.getMethods()) {
            if (method.isAnnotationPresent(PostConstruct.class)) {
                method.invoke(t);
            }
        }
    }
}
