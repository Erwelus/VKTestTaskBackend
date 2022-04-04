package com.ervelus.infrastructure;

import com.ervelus.infrastructure.annotations.Component;
import com.ervelus.infrastructure.configuration.Config;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class, used for object access
 */
public class ApplicationContext {
    /**
     * Main factory of the project, used when object is not present in storage
     */
    @Setter
    private ObjectFactory factory;
    /**
     * Bean storage
     */
    private final Map<Class, Object> beans = new ConcurrentHashMap<>();
    /**
     * Initial config of the application
     */
    @Getter
    private final Config config;

    public ApplicationContext(Config config) {
        this.config = config;
    }

    /**
     * Method for objects access
     * @param type Type of the object you need
     * @return Required object
     */
    public <T> T getObject(Class<T> type) throws ReflectiveOperationException {
        if (beans.containsKey(type)){
            return (T) beans.get(type);
        }
        Class<? extends T> implClass = type;
        if (type.isInterface()){
            implClass = config.getImplClass(type);
        }
        T t = factory.createObject(implClass);
        if (implClass.isAnnotationPresent(Component.class)){
            beans.put(type, t);
        }
        return t;
    }

    /**
     * Method for saving objects into context
     * Should <b>only</b> be used by Object Configurators
     * @param type Type of object you need
     * @param obj instance of type
     */
    public <T> void putObject(Class type, T obj){
        if (!beans.containsKey(type)){
            beans.put(type, obj);
        }
    }

}
