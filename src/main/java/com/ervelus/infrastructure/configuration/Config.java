package com.ervelus.infrastructure.configuration;

import org.reflections.Reflections;

/**
 * Interface for any Configs of the project
 * Mostly used when you have several implementations of the interface, and you want to specify which one to use
 */
public interface Config {
    /**
     * Getter for implementation of the given interface,
     * used for Interface-based injection
     * @param ifc Interface which implementation to look for
     * @return Class, implementing given interface
     */
    <T> Class<? extends T> getImplClass(Class<T> ifc);

    /**
     * Getter for Reflections scanner, used by Object Factory
     * @return
     */
    Reflections getScanner();
}
