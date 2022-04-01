package com.ervelus.infrastructure.configuration;

import lombok.Getter;
import org.reflections.Reflections;

import java.util.Map;
import java.util.Set;

public class JavaConfig implements Configuration{
    @Getter
    private final Reflections scanner;
    private final Map<Class, Class> ifcToImplClass;

    public JavaConfig(String packageToScan, Map<Class, Class> ifcToImplClass){
        this.ifcToImplClass=ifcToImplClass;
        this.scanner = new Reflections(packageToScan);
    }

    @Override
    public <T> Class<? extends T> getImplClass(Class<T> ifc) {
        return ifcToImplClass.computeIfAbsent(ifc, aClass -> {
            Set<Class<? extends T>> classes = scanner.getSubTypesOf(ifc);
            if (classes.size() != 1) throw new RuntimeException("0 or more than 1 implementation found, please update your config");
            return classes.iterator().next();
        });
    }

}
