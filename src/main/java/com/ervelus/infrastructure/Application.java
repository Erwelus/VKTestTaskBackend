package com.ervelus.infrastructure;


import com.ervelus.infrastructure.configuration.Configuration;
import com.ervelus.infrastructure.configuration.JavaConfig;

import java.util.Map;

public class Application {
    public static ApplicationContext run(String packageToScan, Map<Class, Class> ifcToClass){
        Configuration configuration = new JavaConfig(packageToScan, ifcToClass);
        ApplicationContext context = new ApplicationContext(configuration);
        ObjectFactory factory = new ObjectFactory(context);
        context.setFactory(factory);

        for (Class aClass: configuration.getScanner().getTypesAnnotatedWith(com.ervelus.infrastructure.annotations.Configuration.class)) {
            try {
                context.getObject(aClass);
            } catch (ReflectiveOperationException e) {
                System.err.println("Failed to instantiate configuration class");
                e.printStackTrace();
                System.exit(1);
            }
        }

        return context;
    }
}
