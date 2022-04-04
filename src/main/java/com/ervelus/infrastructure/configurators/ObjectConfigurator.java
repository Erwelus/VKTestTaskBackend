package com.ervelus.infrastructure.configurators;

import com.ervelus.infrastructure.ApplicationContext;

/**
 * Interface for objects, used for configuring objects while creating
 */
public interface ObjectConfigurator {
    /**
     * Configuration logic, invoked after constructor
     * Does not provide proxy, used only for working with given class
     * @param t Object to configure
     * @param context Application Context where to store beans
     */
    void configure(Object t, ApplicationContext context);
}
