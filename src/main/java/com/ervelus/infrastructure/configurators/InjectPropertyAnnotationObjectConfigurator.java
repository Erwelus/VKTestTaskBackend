package com.ervelus.infrastructure.configurators;

import com.ervelus.infrastructure.ApplicationContext;
import com.ervelus.infrastructure.annotations.InjectProperty;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

/**
 * Object configurator, used for injecting values from property files
 * Requires @InjectProperty to be called
 * @see InjectProperty
 */
public class InjectPropertyAnnotationObjectConfigurator implements ObjectConfigurator{
    /**
     * Storage of values from property file
     */
    private Map<String, String> propertiesMap;

    /**
     * @throws FileNotFoundException when property file (application.properties) not found
     */
    public InjectPropertyAnnotationObjectConfigurator() throws FileNotFoundException {
        String path = ClassLoader.getSystemClassLoader().getResource("application.properties").getPath();
        Stream<String> lines = new BufferedReader(new FileReader(path)).lines();
        propertiesMap = lines.map(line -> line.split("=")).collect(toMap(arr -> arr[0].trim(), arr -> arr[1].trim()));
    }
    @Override
    public void configure(Object t, ApplicationContext context) {
        Class<?> implClass = t.getClass();
        for (Field field : implClass.getDeclaredFields()) {
            InjectProperty annotation = field.getAnnotation(InjectProperty.class);
            if (annotation != null) {
                String value = annotation.value().isEmpty() ? propertiesMap.get(field.getName()) : propertiesMap.get(annotation.value());
                field.setAccessible(true);
                try {
                    field.set(t,value);
                } catch (IllegalAccessException e) {
                    System.err.println("Failed to inject property value from resource");
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }
    }
}
