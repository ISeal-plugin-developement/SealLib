package dev.iseal.sealLib.Utils;

import org.reflections.Reflections;

import java.util.Set;

public class Utils {

    public static Set<Class<?>> findAllClassesInPackage(String packageName, Class<?> clazz) {
        Reflections reflections = new Reflections(packageName);
        return (Set<Class<?>>) reflections.getSubTypesOf(clazz);
    }

}
