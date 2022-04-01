package com.ervelus.infrastructure;

import com.ervelus.infrastructure.annotations.Component;
import com.ervelus.infrastructure.configuration.Configuration;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationContext {
    @Setter
    private ObjectFactory factory;
    private final Map<Class, Object> beans = new ConcurrentHashMap<>();
    @Getter
    private final Configuration configuration;

    public ApplicationContext(Configuration configuration) {
        this.configuration = configuration;
    }

    public <T> T getObject(Class<T> type) throws ReflectiveOperationException {
        if (beans.containsKey(type)){
            return (T) beans.get(type);
        }
        Class<? extends T> implClass = type;
        if (type.isInterface()){
            implClass = configuration.getImplClass(type);
        }
        T t = factory.createObject(implClass);
        if (implClass.isAnnotationPresent(Component.class)){
            beans.put(type, t);
        }
        return t;
    }

    public <T> void putObject(T obj){
        if (!beans.containsKey(obj.getClass())){
            beans.put(obj.getClass(), obj);
        }
    }

}
