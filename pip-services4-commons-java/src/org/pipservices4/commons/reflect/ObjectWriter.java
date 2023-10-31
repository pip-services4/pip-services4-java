package org.pipservices4.commons.reflect;

import java.lang.reflect.*;
import java.util.*;

import org.pipservices4.commons.convert.IntegerConverter;

/**
 * Helper class to perform property introspection and dynamic writing.
 * <p>
 * In contrast to {@link PropertyReflector} which only introspects regular objects,
 * this ObjectWriter is also able to handle maps and arrays.
 * For maps properties are key-pairs identified by string keys,
 * For arrays properties are elements identified by integer index.
 * <p>
 * This class has symmetric implementation across all languages supported
 * by Pip.Services toolkit and used to support dynamic data processing.
 * <p>
 * Because all languages have different casing and case sensitivity rules,
 * this ObjectWriter treats all property names as case insensitive.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * MyObject myObj = new MyObject();
 *
 * ObjectWriter.setProperty(myObj, "myProperty", 123);
 *
 * Map<String, Object> myMap = new HashMap<String, Object>(){
 *    {
 *  		put("key1", 123);
 *  		put("key2", "ABC");
 *    }
 * };
 * ObjectWriter.setProperty(myMap, "key1", "XYZ");
 *
 * int[] myArray = new int[] {1, 2, 3};
 * ObjectWriter.setProperty(myArray, "0", 123);
 * }
 * </pre>
 *
 * @see PropertyReflector
 */
public class ObjectWriter {

    /**
     * Sets value of object property specified by its name.
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
    @SuppressWarnings("unchecked")
    public static void setProperty(Object obj, String name, Object value) {
        if (obj == null)
            throw new NullPointerException("Object cannot be null");
        if (name == null)
            throw new NullPointerException("Property name cannot be null");

        if (obj instanceof Map<?, ?>) {
            Map<Object, Object> mapObj = (Map<Object, Object>) obj;
            for (Object key : mapObj.keySet()) {
                if (name.equalsIgnoreCase(key.toString())) {
                    mapObj.put(key, value);
                    return;
                }
            }
            mapObj.put(name, value);
        } else if (obj instanceof List<?>) {
            List<Object> list = (List<Object>) obj;
            int index = IntegerConverter.toIntegerWithDefault(name, -1);
            if (index < 0)
                return;
            else if (index < list.size())
                list.set(index, value);
            else {
                while (index - 1 >= list.size())
                    list.add(null);
                list.add(value);
            }
        } else if (obj.getClass().isArray()) {
            int length = Array.getLength(obj);
            int index = IntegerConverter.toIntegerWithDefault(name, -1);
            if (index >= 0 && index < length)
                Array.set(obj, index, value);
        } else {
            PropertyReflector.setProperty(obj, name, value);
        }
    }

    /**
     * Sets values of some (all) object properties.
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
}
