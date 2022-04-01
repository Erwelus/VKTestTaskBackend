package com.ervelus.infrastructure.configuration;

import org.reflections.Reflections;

public interface Configuration {
    <T> Class<? extends T> getImplClass(Class<T> ifc);
    Reflections getScanner();
}
