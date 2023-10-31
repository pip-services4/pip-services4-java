package org.pipservices4.commons.reflect;

import java.util.*;

import org.pipservices4.commons.convert.*;

/**
 * Helper class to perform property introspection and dynamic reading.
 * <p>
 * It is similar to {@link ObjectReader} but reads properties recursively
 * through the entire object graph. Nested property names are defined
 * using dot notation as <code>"object.subobject.property"</code>
 *
 * @see PropertyReflector
 * @see ObjectReader
 */
public class RecursiveObjectReader {

    private static boolean performHasProperty(Object obj, String[] names, int nameIndex) {
        if (nameIndex < names.length - 1) {
            Object value = ObjectReader.getProperty(obj, names[nameIndex]);
            if (value != null)
                return performHasProperty(value, names, nameIndex + 1);
            else
                return false;
        } else
            return ObjectReader.hasProperty(obj, names[nameIndex]);
    }

    /**
     * Checks recursively if object or its subobjects has a property with specified
     * name.
     * <p>
     * The object can be a user defined object, map or array. The property name
     * correspondently must be object property, map key or array index.
     *
     * @param obj  an object to introspect.
     * @param name a name of the property to check.
     * @return true if the object has the property and false if it doesn't.
     */
    public static boolean hasProperty(Object obj, String name) {
        if (obj == null || name == null)
            return false;

        String[] names = name.split("\\.");
        if (names.length == 0)
            return false;

        return performHasProperty(obj, names, 0);
    }

    private static Object performGetProperty(Object obj, String[] names, int nameIndex) {
        if (nameIndex < names.length - 1) {
            Object value = ObjectReader.getProperty(obj, names[nameIndex]);
            if (value != null)
                return performGetProperty(value, names, nameIndex + 1);
            else
                return null;
        } else
            return ObjectReader.getProperty(obj, names[nameIndex]);
    }

    /**
     * Recursively gets value of object or its subobjects property specified by its
     * name.
     * <p>
     * The object can be a user defined object, map or array. The property name
     * correspondently must be object property, map key or array index.
     *
     * @param obj  an object to read property from.
     * @param name a name of the property to get.
     * @return the property value or null if property doesn't exist or introspection
     * failed.
     */
    public static Object getProperty(Object obj, String name) {
        if (obj == null || name == null)
            return null;

        String[] names = name.split("\\.");
        if (names.length == 0)
            return null;

        return performGetProperty(obj, names, 0);
    }

    private static boolean isSimpleValue(Object value) {
        TypeCode code = TypeConverter.toTypeCode(value);
        return code != TypeCode.Array && code != TypeCode.Map && code != TypeCode.Object;
    }

    private static void performGetPropertyNames(Object obj, String path, List<String> result,
                                                List<Object> cycleDetect) {

        Map<String, Object> map = ObjectReader.getProperties(obj);

        if (map.size() != 0 && cycleDetect.size() < 100) {
            cycleDetect.add(obj);
            try {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    Object value = entry.getValue();

                    // Prevent cycles
                    if (cycleDetect.contains(value))
                        continue;

                    String key = path != null ? path + "." + entry.getKey() : entry.getKey();

                    // Add simple values directly
                    if (isSimpleValue(value))
                        result.add(key);
                        // Recursively go to elements
                    else
                        performGetPropertyNames(value, key, result, cycleDetect);
                }
            } finally {
                cycleDetect.remove(obj);
            }
        } else {
            if (path != null)
                result.add(path);
        }
    }

    /**
     * Recursively gets names of all properties implemented in specified object and
     * its subobjects.
     * <p>
     * The object can be a user defined object, map or array. Returned property name
     * correspondently are object properties, map keys or array indexes.
     *
     * @param obj an objec to introspect.
     * @return a list with property names.
     */
    public static List<String> getPropertyNames(Object obj) {
        List<String> propertyNames = new ArrayList<>();

        if (obj != null) {
            List<Object> cycleDetect = new ArrayList<>();
            performGetPropertyNames(obj, null, propertyNames, cycleDetect);
        }
        return propertyNames;
    }

    private static void performGetProperties(Object obj, String path, Map<String, Object> result,
                                             List<Object> cycleDetect) {

        Map<String, Object> map = ObjectReader.getProperties(obj);

        if (map.size() != 0 && cycleDetect.size() < 100) {
            cycleDetect.add(obj);
            try {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    Object value = entry.getValue();

                    // Prevent cycles
                    if (cycleDetect.contains(value))
                        continue;

                    String key = path != null ? path + "." + entry.getKey() : entry.getKey();

                    // Add simple values directly
                    if (isSimpleValue(value))
                        result.put(key, value);
                        // Recursively go to elements
                    else
                        performGetProperties(value, key, result, cycleDetect);
                }
            } finally {
                cycleDetect.remove(obj);
            }
        } else {
            if (path != null)
                result.put(path, obj);
        }
    }

    /**
     * Get values of all properties in specified object and its subobjects and
     * returns them as a map.
     * <p>
     * The object can be a user defined object, map or array. Returned properties
     * correspondently are object properties, map key-pairs or array elements with
     * their indexes.
     *
     * @param obj an object to get properties from.
     * @return a map, containing the names of the object's properties and their
     * values.
     */
    public static Map<String, Object> getProperties(Object obj) {
        Map<String, Object> properties = new HashMap<>();

        if (obj != null) {
            List<Object> cycleDetect = new ArrayList<>();
            performGetProperties(obj, null, properties, cycleDetect);
        }

        return properties;
    }

}
