package com.ervelus.infrastructure.configurators;

import com.ervelus.infrastructure.ApplicationContext;

public interface ObjectConfigurator {
    void configure(Object t, ApplicationContext context);
}
