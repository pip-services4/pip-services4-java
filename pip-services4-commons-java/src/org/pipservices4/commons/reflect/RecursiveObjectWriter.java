package org.pipservices4.commons.reflect;

import java.util.*;

/**
 * Helper class to perform property introspection and dynamic writing.
 * <p>
 * It is similar to {@link ObjectWriter} but writes properties recursively
 * through the entire object graph. Nested property names are defined
 * using dot notation as <code>"object.subobject.property"</code>
 *
 * @see PropertyReflector
 * @see ObjectWriter
 */
public class RecursiveObjectWriter {

    // Todo: Make it smarter
    private static Object createProperty(Object obj, String name) {
        return new HashMap<String, Object>();
    }

    private static void performSetProperty(Object obj, String[] names, int nameIndex, Object value) {
        if (nameIndex < names.length - 1) {
            Object subObj = ObjectReader.getProperty(obj, names[nameIndex]);
            if (subObj != null)
                performSetProperty(subObj, names, nameIndex + 1, value);
            else {
                subObj = createProperty(obj, names[nameIndex]);
                performSetProperty(subObj, names, nameIndex + 1, value);
                ObjectWriter.setProperty(obj, names[nameIndex], subObj);
            }
        } else
            ObjectWriter.setProperty(obj, names[nameIndex], value);
    }

    /**
     * Recursively sets value of object and its subobjects property specified by its
     * name.
     * <p>
     * The object can be a user defined object, map or array. The property name
     * correspondently must be object property, map key or array index.
     * <p>
     * If the property does not exist or introspection fails this method doesn't do
     * anything and doesn't any throw errors.
     *
     * @param obj   an object to write property to.
     * @param name  a name of the property to set.
     * @param value a new value for the property to set.
     */
    public static void setProperty(Object obj, String name, Object value) {
        if (obj == null || name == null)
            return;

        String[] names = name.split("\\.");
        if (names.length == 0)
            return;

        performSetProperty(obj, names, 0, value);
    }

    /**
     * Recursively sets values of some (all) object and its subobjects properties.
     * <p>
     * The object can be a user defined object, map or array. Property values
     * correspondently are object properties, map key-pairs or array elements with
     * their indexes.
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

    /**
     * Copies content of one object to another object by recursively reading all
     * properties from source object and then recursively writing them to
     * destination object.
     *
     * @param dest a destination object to write properties to.
     * @param src  a source object to read properties from
     */
    public static void copyProperties(Object dest, Object src) {
        if (dest == null || src == null)
            return;

        Map<String, Object> values = RecursiveObjectReader.getProperties(src);
        setProperties(dest, values);
    }

}
