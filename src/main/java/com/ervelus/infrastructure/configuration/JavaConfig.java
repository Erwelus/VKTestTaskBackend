package com.ervelus.infrastructure.configuration;

import lombok.Getter;
import org.reflections.Reflections;

import java.util.Map;
import java.util.Set;

/**
 * Config implementation using java tools
 * Initial config is stored in map Interface to Implementation
 */
public class JavaConfig implements Config {
    /**
     * Reflections object used for looking for the implementation in given packages
     */
    @Getter
    private final Reflections scanner;
    /**
     * Configuration storage
     */
    private final Map<Class, Class> ifcToImplClass;

    /**
     * @param packageToScan path to the location where to look for implementations
     * @param ifcToImplClass initial config
     */
    public JavaConfig(String packageToScan, Map<Class, Class> ifcToImplClass){
        this.ifcToImplClass=ifcToImplClass;
        this.scanner = new Reflections(packageToScan);
    }

    /**
     * If implementation is not specified in initial config, will look for it in given package
     * If you have 0 implementations - throws Exception, you need implementations for injection
     * If you have more than 1 implementation - throws exception, you need to specify implementation in initial config
     * @param ifc Interface which implementation to look for
     * @return Implementation of given interface
     */
    @Override
    public <T> Class<? extends T> getImplClass(Class<T> ifc) {
        return ifcToImplClass.computeIfAbsent(ifc, aClass -> {
            Set<Class<? extends T>> classes = scanner.getSubTypesOf(ifc);
            if (classes.size() != 1) throw new RuntimeException("0 or more than 1 implementation found, please update your config");
            return classes.iterator().next();
        });
    }

}
