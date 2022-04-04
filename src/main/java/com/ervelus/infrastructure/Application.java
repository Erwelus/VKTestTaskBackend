package com.ervelus.infrastructure;


import com.ervelus.infrastructure.configuration.Config;
import com.ervelus.infrastructure.configuration.JavaConfig;

import java.util.Map;

/**
 * Main infrastructure class, required to be able to use infrastructure
 * Should be instantiated and run in Main class
 */
public class Application {
    /**
     * Launches the infrastructure
     * @param packageToScan path for your project to enable dependency injection
     * @param ifcToClass initial config, used for multiple implementations resolution
     * @return Application context, that should be used for getting objects
     */
    public static ApplicationContext run(String packageToScan, Map<Class, Class> ifcToClass){
        Config config = new JavaConfig(packageToScan, ifcToClass);
        ApplicationContext context = new ApplicationContext(config);
        ObjectFactory factory = new ObjectFactory(context);
        context.setFactory(factory);

        for (Class aClass: config.getScanner().getTypesAnnotatedWith(com.ervelus.infrastructure.annotations.Configuration.class)) {
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
