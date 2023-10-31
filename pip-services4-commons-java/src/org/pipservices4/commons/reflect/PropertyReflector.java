package org.pipservices4.commons.reflect;

import java.lang.reflect.*;
import java.util.*;

/**
 * Helper class to perform property introspection and dynamic reading and writing.
 * <p>
 * This class has symmetric implementation across all languages supported
 * by Pip.Services toolkit and used to support dynamic data processing.
 * <p>
 * Because all languages have different casing and case sensitivity rules,
 * this PropertyReflector treats all property names as case insensitive.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * MyObject myObj = new MyObject();
 *
 * List<String> properties = PropertyReflector.getPropertyNames();
 * PropertyReflector.hasProperty(myObj, "myProperty");
 * Object value = PropertyReflector.getProperty(myObj, "myProperty");
 * PropertyReflector.setProperty(myObj, "myProperty", 123);
 * }
 * </pre>
 */
public class PropertyReflector {

    private static boolean matchField(Field field, String name) {
        int mod = field.getModifiers();
        return field.getName().equalsIgnoreCase(name) && Modifier.isPublic(mod) && !Modifier.isStatic(mod);
    }

    private static boolean matchPropertyGetter(Method method, String name) {
        // Skip "special" fields
        if (method.getName().equals("getClass"))
            return false;

        int mod = method.getModifiers();
        return method.getName().equalsIgnoreCase(name) && Modifier.isPublic(mod) && !Modifier.isStatic(mod) && !Modifier.isAbstract(mod) && method.getParameterCount() == 0;
    }

    private static boolean matchPropertySetter(Method method, String name) {
        int mod = method.getModifiers();
        return method.getName().equalsIgnoreCase(name) && Modifier.isPublic(mod) && !Modifier.isStatic(mod)
                && !Modifier.isAbstract(mod) && method.getParameterCount() == 1
                /* && method.getReturnType() == null */;
    }

    /**
     * Checks if object has a property with specified name..
     *
     * @param obj  an object to introspect.
     * @param name a name of the property to check.
     * @return true if the object has the property and false if it doesn't.
     */
    public static boolean hasProperty(Object obj, String name) {
        if (obj == null)
            throw new NullPointerException("Object cannot be null");
        if (name == null)
            throw new NullPointerException("Property name cannot be null");

        Class<?> objClass = obj.getClass();

        // Search in fields
        for (Field field : objClass.getFields()) {
            if (matchField(field, name))
                return true;
        }

        // Search in properties
        name = "get" + name;
        for (Method method : objClass.getMethods()) {
            if (matchPropertyGetter(method, name))
                return true;
        }

        return false;
    }

    /**
     * Gets value of object property specified by its name.
     *
     * @param obj  an object to read property from.
     * @param name a name of the property to get.
     * @return the property value or null if property doesn't exist or introspection
     * failed.
     */
    public static Object getProperty(Object obj, String name) {
        if (obj == null)
            throw new NullPointerException("Object cannot be null");
        if (name == null)
            throw new NullPointerException("Property name cannot be null");

        Class<?> objClass = obj.getClass();

        // Search in fields
        for (Field field : objClass.getFields()) {
            try {
                if (matchField(field, name))
                    return field.get(obj);
            } catch (Throwable t) {
                // Ignore exceptions
            }
        }

        // Search in properties
        name = "get" + name;
        for (Method method : objClass.getMethods()) {
            try {
                if (matchPropertyGetter(method, name))
                    return method.invoke(obj);
            } catch (Throwable t) {
                // Ignore exceptions
            }
        }

        return null;
    }

    /**
     * Gets names of all properties implemented in specified object.
     *
     * @param obj an objec to introspect.
     * @return a list with property names.
     */
    public static List<String> getPropertyNames(Object obj) {
        List<String> properties = new ArrayList<>();

        Class<?> objClass = obj.getClass();

        // Get all properties
        for (Field field : objClass.getFields()) {
            if (matchField(field, field.getName()))
                properties.add(field.getName());
        }

        // Get all properties
        for (Method method : objClass.getMethods()) {
            if (method.getName().startsWith("get") && matchPropertyGetter(method, method.getName())) {
                String name = method.getName().substring(3);
                name = name.substring(0, 1).toLowerCase() + name.substring(1);
                properties.add(name);
            }
        }

        return properties;
    }

    /**
     * Get values of all properties in specified object and returns them as a map.
     *
     * @param obj an object to get properties from.
     * @return a map, containing the names of the object's properties and their
     * values.
     */
    public static Map<String, Object> getProperties(Object obj) {
        Map<String, Object> map = new HashMap<>();

        Class<?> objClass = obj.getClass();

        // Get all fields
        for (Field field : objClass.getFields()) {
            try {
                if (matchField(field, field.getName())) {
                    String name = field.getName();
                    Object value = field.get(obj);
                    map.put(name, value);
                }
            } catch (Throwable t) {
                // Ignore exception
            }
        }

        // Get all properties
        for (Method method : objClass.getMethods()) {
            try {
                if (method.getName().startsWith("get") && matchPropertyGetter(method, method.getName())) {
                    String name = method.getName().substring(3);
                    name = name.substring(0, 1).toLowerCase() + name.substring(1);
                    Object value = method.invoke(obj);
                    map.put(name, value);
                }
            } catch (Throwable t) {
                // Ignore exception
            }
        }

        return map;
    }

    /**
     * Sets value of object property specified by its name.
     * <p>
     * If the property does not exist or introspection fails this method doesn't do
     * anything and doesn't any throw errors.
     *
     * @param obj   an object to write property to.
     * @param name  a name of the property to set.
     * @param value a new value for the property to set.
     */
    public static void setProperty(Object obj, String name, Object value) {
        if (obj == null)
            throw new NullPointerException("Object cannot be null");
        if (name == null)
            throw new NullPointerException("Property name cannot be null");

        Class<?> objClass = obj.getClass();

        for (Field field : objClass.getFields()) {
            try {
                if (matchField(field, name)) {
                    field.set(obj, value);
                    return;
                }
            } catch (Exception ex) {
                // Ignore exception
            }
        }

        name = "set" + name;
        for (Method method : objClass.getMethods()) {
            try {
                if (matchPropertySetter(method, name)) {
                    method.invoke(obj, value);
                    return;
                }
            } catch (Exception ex) {
                // Ignore exception
            }
        }
    }

    /**
     * Sets values of some (all) object properties.
     * <p>
     * If some properties do not exist or introspection fails they are just silently
     * skipped and no errors thrown.
     *
     * @param obj    an object to write properties to.
     * @param values a map, containing property names and their values.
     * @see #setProperty(Object, String, Object)
     */
    public static void setProperties(Object obj, Map<String, Object> values) {
        if (values == null || values.size() == 0)
            return;

        for (Map.Entry<String, Object> entry : values.entrySet()) {
            setProperty(obj, entry.getKey(), entry.getValue());
        }
    }
}
