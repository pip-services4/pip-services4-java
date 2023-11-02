package org.pipservices4.components.exec;

import java.io.Serial;
import java.util.*;

import org.pipservices4.components.config.*;
import org.pipservices4.commons.convert.*;
import org.pipservices4.commons.data.*;
import org.pipservices4.commons.reflect.*;

import com.fasterxml.jackson.core.*;

/**
 * Contains map with execution parameters.
 * <p>
 * In general, this map may contain non-serializable values.
 * And in contrast with other maps, its getters and setters
 * support dot notation and able to access properties
 * in the entire object graph.
 * <p>
 * This class is often use to pass execution and notification
 * arguments, and parameterize classes before execution.
 *
 * @see IParameterized
 * @see AnyValueMap
 */
public class Parameters extends AnyValueMap {
    @Serial
    private static final long serialVersionUID = 9145851165539523100L;

    /**
     * Creates a new instance of the map.
     */
    public Parameters() {
    }

    /**
     * Creates a new instance of the map and assigns its value.
     *
     * @param map (optional) values to initialize this map.
     */
    public Parameters(Map<?, ?> map) {
        super(map);
    }

    /**
     * Gets a map element specified by its key.
     * <p>
     * The key can be defined using dot notation and allows to recursively access
     * elements of elements.
     *
     * @param key a key of the element to get.
     * @return the value of the map element.
     */
    @Override
    public Object get(String key) {
        if (key == null)
            return null;
        else if (key.indexOf('.') > 0)
            return RecursiveObjectReader.getProperty(this, key);
        else
            return super.get(key);
    }

    /**
     * Puts a new value into map element specified by its key.
     * <p>
     * The key can be defined using dot notation and allows to recursively access
     * elements of elements.
     *
     * @param key   a key of the element to put.
     * @param value a new value for map element.
     */
    @Override
    public Object put(String key, Object value) {
        if (key == null)
            return null;
        else if (key.indexOf('.') > 0)
            RecursiveObjectWriter.setProperty(this, key, value);
        else
            super.put(key, value);
        return value;
    }

    /**
     * Converts map element into a Parameters or returns null if conversion is not
     * possible.
     *
     * @param key a key of element to get.
     * @return Parameters value of the element or null if conversion is not
     *         supported.
     */
    public Parameters getAsNullableParameters(String key) {
        AnyValueMap value = getAsNullableMap(key);
        return value != null ? new Parameters(value) : null;
    }

    /**
     * Converts map element into a Parameters or returns empty Parameters if
     * conversion is not possible.
     *
     * @param key a key of element to get.
     * @return Parameters value of the element or empty Parameters if conversion is
     *         not supported.
     */
    public Parameters getAsParameters(String key) {
        AnyValueMap value = getAsMap(key);
        return new Parameters(value);
    }

    /**
     * Converts map element into a Parameters or returns default value if
     * conversion is not possible.
     *
     * @param key          a key of element to get.
     * @param defaultValue the default value
     * @return Parameters value of the element or default value if conversion is not
     *         supported.
     */
    public Parameters getAsParametersWithDefault(String key, Parameters defaultValue) {
        Parameters result = getAsNullableParameters(key);
        return result != null ? result : defaultValue;
    }

    /**
     * Checks if this map contains an element with specified key.
     * <p>
     * The key can be defined using dot notation and allows to recursively access
     * elements of elements.
     *
     * @param key a key to be checked
     * @return true if this map contains the key or false otherwise.
     */
    @Override
    public boolean containsKey(Object key) {
        return RecursiveObjectReader.hasProperty(this, key.toString());
    }

    /**
     * Overrides parameters with new values from specified Parameters and returns a
     * new Parameters object.
     *
     * @param parameters Parameters with parameters to override the current values.
     * @return a new Parameters object.
     */
    public Parameters override(Parameters parameters) {
        return override(parameters, false);
    }

    /**
     * Overrides parameters with new values from specified Parameters and returns a
     * new Parameters object.
     *
     * @param parameters Parameters with parameters to override the current values.
     * @param recursive  (optional) true to perform deep copy, and false for shallow
     *                   copy. Default: false
     * @return a new Parameters object.
     */
    public Parameters override(Parameters parameters, boolean recursive) {
        Parameters result = new Parameters();
        if (recursive) {
            RecursiveObjectWriter.copyProperties(result, this);
            RecursiveObjectWriter.copyProperties(result, parameters);
        } else {
            ObjectWriter.setProperties(result, this);
            ObjectWriter.setProperties(result, parameters);
        }
        return result;
    }

    /**
     * Set default values from specified Parameters and returns a new Parameters
     * object.
     *
     * @param defaultParameters Parameters with default parameter values.
     * @return a new Parameters object.
     */
    public Parameters setDefaults(Parameters defaultParameters) {
        return setDefaults(defaultParameters, false);
    }

    /**
     * Set default values from specified Parameters and returns a new Parameters
     * object.
     *
     * @param defaultParameters Parameters with default parameter values.
     * @param recursive         (optional) true to perform deep copy, and false for
     *                          shallow copy. Default: false
     * @return a new Parameters object.
     */
    public Parameters setDefaults(Parameters defaultParameters, boolean recursive) {
        Parameters result = new Parameters();
        if (recursive) {
            RecursiveObjectWriter.copyProperties(result, defaultParameters);
            RecursiveObjectWriter.copyProperties(result, this);
        } else {
            ObjectWriter.setProperties(result, defaultParameters);
            ObjectWriter.setProperties(result, this);
        }
        return result;
    }

    /**
     * Assigns (copies over) properties from the specified value to this map.
     *
     * @param value value whose properties shall be copied over.
     */
    public void assignTo(Object value) {
        if (value == null || size() == 0)
            return;
        RecursiveObjectWriter.copyProperties(value, this);
    }

    /**
     * Picks select parameters from these Parameters and returns them as a new
     * Parameters object.
     *
     * @param paths keys to be picked and copied over to new Parameters.
     * @return a new Parameters object.
     */
    public Parameters pick(String... paths) {
        Parameters result = new Parameters();
        for (String path : paths) {
            if (containsKey(path))
                result.put(path, get(path));
        }
        return result;
    }

    /**
     * Omits selected parameters from these Parameters and returns the rest as a new
     * Parameters object.
     *
     * @param paths keys to be omitted from copying over to new Parameters.
     * @return a new Parameters object.
     */
    public Parameters omit(String... paths) {
        Parameters result = new Parameters(this);
        for (String path : paths)
            result.remove(path);
        return result;
    }

    /**
     * Converts this map to JSON object.
     *
     * @return a JSON representation of this map.
     * @throws JsonProcessingException when conversion fails for any reason.
     */
    public String toJson() throws JsonProcessingException {
        return JsonConverter.toJson(this);
    }

    /**
     * Creates a new Parameters object filled with key-value pairs from specified
     * object.
     *
     * @param value an object with key-value pairs used to initialize a new
     *              Parameters.
     * @return a new Parameters object.
     */
    public static Parameters fromValue(Object value) {
        AnyValueMap map = AnyValueMap.fromValue(value);
        return new Parameters(map);
    }

    /**
     * Creates a new Parameters object filled with provided key-value pairs called
     * tuples. Tuples parameters contain a sequence of key1, value1, key2, value2,
     * ... pairs.
     *
     * @param tuples the tuples to fill a new Parameters object.
     * @return a new Parameters object.
     * @see AnyValueMap#fromTuplesArray(Object[])
     */
    public static Parameters fromTuples(Object... tuples) {
        AnyValueMap map = AnyValueMap.fromTuplesArray(tuples);
        return new Parameters(map);
    }

    /**
     * Merges two or more Parameters into one. The following Parameters override
     * previously defined parameters.
     *
     * @param parameters a list of Parameters objects to be merged.
     * @return a new Parameters object.
     * @see AnyValueMap#fromMaps(Map...)
     */
    public static Parameters mergeParams(Parameters... parameters) {
        AnyValueMap map = AnyValueMap.fromMaps(parameters);
        return new Parameters(map);
    }

    /**
     * Creates new Parameters from JSON object.
     *
     * @param json a JSON string containing parameters.
     * @return a new Parameters object.
     * @see JsonConverter#toNullableMap(String)
     */
    public static Parameters fromJson(String json) {
        Map<String, Object> map = JsonConverter.toNullableMap(json);
        return map != null ? new Parameters(map) : new Parameters();
    }

    /**
     * Creates new Parameters from ConfigMap object.
     *
     * @param config a ConfigParams that contain parameters.
     * @return a new Parameters object.
     * @see ConfigParams
     */
    public static Parameters fromConfig(ConfigParams config) {
        Parameters result = new Parameters();

        if (config == null || config.isEmpty())
            return result;
        for (String key : config.getKeys()) {
            result.put(key, config.get(key));
        }
        return result;
    }
}
