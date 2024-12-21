package dev.iseal.sealLib.Helpers;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class UnsafeInvocationHandler implements InvocationHandler {

    private final Object target;

    public UnsafeInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            // Try to invoke the method on the target object
            return method.invoke(target, args);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            // If the method does not exist, provide a default implementation
            Class<?> returnType = method.getReturnType();
            if (returnType.equals(Void.TYPE)) {
                return null; // For void methods, do nothing
            } else if (returnType.equals(Boolean.TYPE)) {
                return false; // Default boolean value
            } else if (returnType.equals(Character.TYPE)) {
                return '\0'; // Default char value
            } else if (returnType.isPrimitive()) {
                return 0; // Default value for other primitive types
            } else {
                return null; // Default value for non-primitive types
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T createProxy(Class<T> interfaceType, T target) {
        return (T) Proxy.newProxyInstance(
                interfaceType.getClassLoader(),
                new Class<?>[]{interfaceType},
                new UnsafeInvocationHandler(target)
        );
    }
}