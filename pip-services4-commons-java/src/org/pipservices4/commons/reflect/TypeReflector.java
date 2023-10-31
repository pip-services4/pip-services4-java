package org.pipservices4.commons.reflect;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;

import org.pipservices4.commons.convert.TypeCode;
import org.pipservices4.commons.convert.TypeConverter;
import org.pipservices4.commons.errors.*;

/**
 * Helper class to perform object type introspection and object instantiation.
 * <p>
 * This class has symmetric implementation across all languages supported
 * by Pip.Services toolkit and used to support dynamic data processing.
 * <p>
 * Because all languages have different casing and case sensitivity rules,
 * this TypeReflector treats all type names as case insensitive.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * TypeDescriptor descriptor = new TypeDescriptor("MyObject", "mylibrary");
 * TypeReflector.getTypeByDescriptor(descriptor);
 * MyObject myObj = TypeReflector.createInstanceByDescriptor(descriptor);
 *
 * TypeDescriptor.isPrimitive(myObj);        // Result: false
 * TypeDescriptor.isPrimitive(123);                // Result: true
 * }
 * </pre>
 *
 * @see TypeDescriptor
 */
public class TypeReflector {

    /**
     * Gets object type by its name and library where it is defined.
     *
     * @param name    an object type name.
     * @param library a library where the type is defined
     * @return the object type or null is the type wasn't found.
     */
    public static Class<?> getType(String name, String library) {
        try {
            // Load module
            if (library != null && !library.isEmpty()) {
                URL moduleUrl = new File(library).toURI().toURL();
                URLClassLoader child = new URLClassLoader(new URL[]{moduleUrl}, ClassLoader.getSystemClassLoader());
                return Class.forName(name, true, child);
            } else {
                return Class.forName(name);
            }
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Gets object type by its name.
     *
     * @param name an object type name.
     * @return the object type or null is the type wasn't found.
     */
    public static Class<?> getType(String name) {
        return getType(name, null);
    }

    /**
     * Gets object type by type descriptor.
     *
     * @param type a type descriptor that points to an object type
     * @return the object type or null is the type wasn't found.
     * @see #getType(String, String)
     * @see TypeDescriptor
     */
    public static Class<?> getTypeByDescriptor(TypeDescriptor type) {
        if (type == null)
            throw new NullPointerException("Type descriptor cannot be null");

        return getType(type.getName(), type.getLibrary());
    }

    /**
     * Creates an instance of an object type.
     *
     * @param type an object type (factory function) to create.
     * @param args arguments for the object constructor.
     * @return the created object instance.
     * @throws Exception when constructors with parameters are not supported
     */
    public static Object createInstanceByType(Class<?> type, Object... args) throws Exception {
        if (args.length == 0) {
            Constructor<?> constructor = type.getConstructor();
            return constructor.newInstance();
        } else {
            throw new UnsupportedException(null, "NOT_SUPPORTED", "Constructors with parameters are not supported");
        }
    }

    /**
     * Creates an instance of an object type specified by its name and library where
     * it is defined.
     *
     * @param name    an object type name.
     * @param library a library (module) where object type is defined.
     * @param args    arguments for the object constructor.
     * @return the created object instance.
     * @throws Exception when type of instance not found
     * @see #getType(String, String)
     * @see #createInstanceByType(Class, Object...)
     */
    public static Object createInstance(String name, String library, Object... args) throws Exception {
        Class<?> type = getType(name, library);
        if (type == null)
            throw new NotFoundException(null, "TYPE_NOT_FOUND", "Type " + name + "," + library + " was not found")
                    .withDetails("type", name).withDetails("library", library);

        return createInstanceByType(type, args);
    }

    /**
     * Creates an instance of an object type specified by its name.
     *
     * @param name an object type name.
     * @param args arguments for the object constructor.
     * @return the created object instance.
     * @throws Exception when type of instance not found
     * @see #getType(String, String)
     * @see #createInstanceByType(Class, Object...)
     */
    public static Object createInstance(String name, Object... args) throws Exception {
        return createInstance(name, null, args);
    }

    /**
     * Creates an instance of an object type specified by type descriptor.
     *
     * @param type a type descriptor that points to an object type
     * @param args arguments for the object constructor.
     * @return the created object instance.
     * @throws Exception when type of instance not found
     * @see #createInstance(String, String, Object...)
     * @see TypeDescriptor
     */
    public static Object createInstanceByDescriptor(TypeDescriptor type, Object... args) throws Exception {
        if (type == null)
            throw new NullPointerException("Type descriptor cannot be null");

        return createInstance(type.getName(), type.getLibrary(), args);
    }

    /**
     * Checks if value has primitive type.
     * <p>
     * Primitive types are: numbers, strings, booleans, date and time. Complex
     * (non-primitive types are): objects, maps and arrays
     *
     * @param value a value to check
     * @return true if the value has primitive type and false if value type is
     * complex.
     * @see TypeConverter#toTypeCode(Object)
     * @see TypeCode
     */
    public static boolean isPrimitive(Object value) {
        TypeCode typeCode = TypeConverter.toTypeCode(value);
        return typeCode == TypeCode.String || typeCode == TypeCode.Enum || typeCode == TypeCode.Boolean
                || typeCode == TypeCode.Integer || typeCode == TypeCode.Long || typeCode == TypeCode.Float
                || typeCode == TypeCode.Double || typeCode == TypeCode.DateTime || typeCode == TypeCode.Duration;
    }

}
