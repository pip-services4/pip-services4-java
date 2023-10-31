package org.pipservices4.commons.reflect;

import java.lang.reflect.*;
import java.util.*;

import org.pipservices4.commons.convert.*;

/**
 * Helper class to perform property introspection and dynamic reading.
 * <p>
 * In contrast to {@link PropertyReflector} which only introspects regular objects,
 * this ObjectReader is also able to handle maps and arrays.
 * For maps properties are key-pairs identified by string keys,
 * For arrays properties are elements identified by integer index.
 * <p>
 * This class has symmetric implementation across all languages supported
 * by Pip.Services toolkit and used to support dynamic data processing.
 * <p>
 * Because all languages have different casing and case sensitivity rules,
 * this ObjectReader treats all property names as case insensitive.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * MyObject myObj = new MyObject();
 * 
 * List<String> properties = ObjectReader.getPropertyNames();
 * ObjectReader.hasProperty(myObj, "myProperty");
 * Object value = PropertyReflector.getProperty(myObj, "myProperty");
 * 
 * Map<String, Object> myMap = new HashMap<String, Object>(){
 *  	{
 *  		put("key1", 123);
 *  		put("key2", "ABC");
 *  	}
 * };
 * ObjectReader.hasProperty(myMap, "key1");
 * Object value = ObjectReader.getProperty(myMap, "key1");
 * 
 * int[] myArray = new int[] {1, 2, 3};
 * ObjectReader.hasProperty(myArrat, "0");
 * Object value = ObjectReader.getProperty(myArray, "0");
 * }
 * </pre>
 * @see PropertyReflector
 */
public class ObjectReader {

	/**
	 * Gets a real object value. If object is a wrapper, it unwraps the value behind
	 * it. Otherwise it returns the same object value.
	 * 
	 * @param obj an object to unwrap..
	 * @return an actual (unwrapped) object value.
	 */
	public static Object getValue(Object obj) {
		// Todo: just a blank implementation for compatibility
		return obj;
	}

	/**
	 * Checks if object has a property with specified name.
	 * 
	 * The object can be a user defined object, map or array. The property name
	 * correspondently must be object property, map key or array index.
	 * 
	 * @param obj  an object to introspect.
	 * @param name a name of the property to check.
	 * @return true if the object has the property and false if it doesn't.
	 */
	@SuppressWarnings("unchecked")
	public static boolean hasProperty(Object obj, String name) {
		if (obj == null || name == null) {
			return false;
		} else if (obj instanceof Map<?, ?>) {
			Map<Object, Object> map = (Map<Object, Object>) obj;
			for (Object key : map.keySet()) {
				if (name.equalsIgnoreCase(key.toString()))
					return true;
			}
			return false;
		} else if (obj instanceof List<?>) {
			Integer index = IntegerConverter.toNullableInteger(name);
			List<Object> list = (List<Object>) obj;
			return index != null && index >= 0 && index < list.size();
		} else if (obj.getClass().isArray()) {
			Integer index = IntegerConverter.toNullableInteger(name);
			int length = Array.getLength(obj);
			return index != null && index >= 0 && index < length;
		} else {
			return PropertyReflector.hasProperty(obj, name);
		}
	}

	/**
	 * Gets value of object property specified by its name.
	 * 
	 * The object can be a user defined object, map or array. The property name
	 * correspondently must be object property, map key or array index.
	 * 
	 * @param obj  an object to read property from.
	 * @param name a name of the property to get.
	 * @return the property value or null if property doesn't exist or introspection
	 *         failed.
	 */
	@SuppressWarnings("unchecked")
	public static Object getProperty(Object obj, String name) {
		if (obj == null || name == null) {
			return null;
		} else if (obj instanceof Map<?, ?>) {
			Map<Object, Object> map = (Map<Object, Object>) obj;
			for (Object key : map.keySet()) {
				if (name.equalsIgnoreCase(key.toString()))
					return map.get(key);
			}
			return null;
		} else if (obj instanceof List<?>) {
			Integer index = IntegerConverter.toNullableInteger(name);
			List<Object> list = (List<Object>) obj;
			return index != null && index >= 0 && index < list.size() ? list.get(index)
					: null;
		} else if (obj.getClass().isArray()) {
			Integer index = IntegerConverter.toNullableInteger(name);
			int length = Array.getLength(obj);
			return index != null && index >= 0 && index < length
					? Array.get(obj, index)
					: null;
		} else {
			return PropertyReflector.getProperty(obj, name);
		}
	}

	/**
	 * Gets names of all properties implemented in specified object.
	 * 
	 * The object can be a user defined object, map or array. Returned property name
	 * correspondently are object properties, map keys or array indexes.
	 * 
	 * @param obj an objec to introspect.
	 * @return a list with property names.
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getPropertyNames(Object obj) {
		List<String> properties = new ArrayList<>();

		if (obj == null) {
			// Do nothing
		} else if (obj instanceof Map<?, ?>) {
			Map<Object, Object> map = (Map<Object, Object>) obj;
			for (Map.Entry<Object, Object> entry : map.entrySet())
				properties.add(entry.getKey().toString());
		} else if (obj instanceof List<?>) {
			List<Object> list = (List<Object>) obj;
			for (int index = 0; index < list.size(); index++)
				properties.add(Integer.toString(index));
		} else if (obj.getClass().isArray()) {
			int length = Array.getLength(obj);
			for (int index = 0; index < length; index++)
				properties.add(Integer.toString(index));
		} else {
			return PropertyReflector.getPropertyNames(obj);
		}

		return properties;
	}

	/**
	 * Get values of all properties in specified object and returns them as a map.
	 * 
	 * The object can be a user defined object, map or array. Returned properties
	 * correspondently are object properties, map key-pairs or array elements with
	 * their indexes.
	 * 
	 * @param obj an object to get properties from.
	 * @return a map, containing the names of the object's properties and their
	 *         values.
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getProperties(Object obj) {
		Map<String, Object> map = new HashMap<>();

		if (obj == null) {
			// Do nothing
		} else if (obj instanceof Map<?, ?>) {
			Map<Object, Object> mapObj = (Map<Object, Object>) obj;
			for (Map.Entry<Object, Object> entry : mapObj.entrySet())
				map.put(entry.getKey().toString(), entry.getValue());
		} else if (obj instanceof List<?>) {
			List<Object> list = (List<Object>) obj;
			for (int index = 0; index < list.size(); index++)
				map.put(Integer.toString(index), list.get(index));
		} else if (obj.getClass().isArray()) {
			int length = Array.getLength(obj);
			for (int index = 0; index < length; index++)
				map.put(Integer.toString(index), Array.get(obj, index));
		} else {
			return PropertyReflector.getProperties(obj);
		}

		return map;
	}
}
