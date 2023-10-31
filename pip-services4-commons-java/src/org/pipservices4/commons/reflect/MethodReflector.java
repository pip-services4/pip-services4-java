package org.pipservices4.commons.reflect;

import java.lang.reflect.*;
import java.util.*;

/**
 * Helper class to perform method introspection and dynamic invocation.
 * <p>
 * This class has symmetric implementation across all languages supported
 * by Pip.Services toolkit and used to support dynamic data processing.
 * <p>
 * Because all languages have different casing and case sensitivity rules,
 * this MethodReflector treats all method names as case insensitive.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * MyObject myObj = new MyObject();
 *
 * List<String> methods = MethodReflector.getMethodNames();
 * MethodReflector.hasMethod(myObj, "myMethod");
 * MethodReflector.invokeMethod(myObj, "myMethod", 123);
 * }
 * </pre>
 */
public class MethodReflector {

    private static boolean matchMethod(Method method, String name) {
        int mod = method.getModifiers();
        return method.getName().equalsIgnoreCase(name) && Modifier.isPublic(mod) && !Modifier.isStatic(mod)
                && !Modifier.isAbstract(mod);
    }

    /**
     * Checks if object has a method with specified name..
     *
     * @param obj  an object to introspect.
     * @param name a name of the method to check.
     * @return true if the object has the method and false if it doesn't.
     */
    public static boolean hasMethod(Object obj, String name) {
        if (obj == null)
            throw new NullPointerException("Object cannot be null");
        if (name == null)
            throw new NullPointerException("Method name cannot be null");

        Class<?> objClass = obj.getClass();

        for (Method method : objClass.getMethods()) {
            if (matchMethod(method, name))
                return true;
        }

        return false;
    }

    /**
     * Invokes an object method by its name with specified parameters.
     *
     * @param obj  an object to invoke.
     * @param name a name of the method to invoke.
     * @param args a list of method arguments.
     * @return the result of the method invocation or null if method returns void.
     */
    public static Object invokeMethod(Object obj, String name, Object... args) {
        if (obj == null)
            throw new NullPointerException("Object cannot be null");
        if (name == null)
            throw new NullPointerException("Method name cannot be null");

        Class<?> objClass = obj.getClass();

        for (Method method : objClass.getMethods()) {
            try {
                if (matchMethod(method, name))
                    return method.invoke(obj, args);
            } catch (Throwable t) {
                // Ignore exceptions
            }
        }

        return null;
    }

    /**
     * Gets names of all methods implemented in specified object.
     *
     * @param obj an objec to introspect.
     * @return a list with method names.
     */
    public static List<String> getMethodNames(Object obj) {
        List<String> methods = new ArrayList<>();

        Class<?> objClass = obj.getClass();

        for (Method method : objClass.getMethods()) {
            if (matchMethod(method, method.getName())) {
                methods.add(method.getName());
            }
        }

        return methods;
    }

}
