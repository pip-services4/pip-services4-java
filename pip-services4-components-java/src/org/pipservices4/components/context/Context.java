package org.pipservices4.components.context;

import org.pipservices4.commons.data.AnyValueMap;
import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.components.exec.Parameters;

import java.util.Map;

/**
 * Basic implementation of an execution context.
 *
 * @see IContext
 * @see AnyValueMap
 */
public class Context implements IContext {
    private AnyValueMap _values;

    public Context(Map<?, ?> values) {
        _values = new AnyValueMap(values);
    }

    public Context() {
    }

    /**
     * Gets a map element specified by its key.
     *
     * @param key     a key of the element to get.
     * @return       the value of the map element.
     */
    @Override
    public Object get(String key) {
        return _values.get(key);
    }

    /**
     * Creates a new Parameters object filled with key-value pairs from specified object.
     *
     * @param value        an object with key-value pairs used to initialize a new Parameters.
     * @return            a new Parameters object.
     */
    public static Context fromValue(Map<?, ?> value) {
        return new Context(value);
    }

    /**
     * Creates a new Context object filled with provided key-value pairs called tuples.
     * Tuples parameters contain a sequence of key1, value1, key2, value2, ... pairs.
     *
     * @param tuples    the tuples to fill a new Parameters object.
     * @return            a new Parameters object.
     *
     * @see AnyValueMap#fromTuplesArray(Object[])
     */
    public static Context fromTuples(Object[] tuples) {
        AnyValueMap map = AnyValueMap.fromTuples(tuples);
        return new Context(map);
    }

    /**
     * Creates new Context from ConfigMap object.
     *
     * @param config     a ConfigParams that contain parameters.
     * @return            a new Context object.
     *
     * @see ConfigParams
     */
    public static Context fromConfig(ConfigParams config) {
        if (config == null) {
            return new Context();
        }

        AnyValueMap values = new AnyValueMap();
        for (String key : config.getKeys()) {
            values.put(key, config.get(key));
        }
        return new Context(values);
    }

    /**
     * Creates new Context from trace id.
     *
     * @param traceId     a transaction id to trace execution through call chain.
     * @return a new Parameters object.
     */
    public static Context fromTraceId(String traceId) {
        Parameters map = Parameters.fromTuples("trace_id", traceId);
        return new Context(map);
    }
}
