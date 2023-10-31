package org.pipservices4.commons.data;

import java.util.*;

/**
 * Data transfer object used to pass filter parameters as simple key-value pairs.
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * FilterParams filter = FilterParams.fromTuples(
 *   "type", "Type1",
 *   "from_create_time", new Date(2000, 0, 1),
 *   "to_create_time", new Date(),
 *   "completed", true
 * );
 * PagingParams paging = new PagingParams(0, 100);
 *
 * myDataClient.getDataByFilter(filter, paging);
 * }
 * </pre>
 *
 * @see StringValueMap
 */
public class FilterParams extends StringValueMap {
    private static final long serialVersionUID = -5476176704133599595L;

    public FilterParams() {
    }

    /**
     * Creates a new instance and initalizes it with elements from the specified
     * map.
     *
     * @param map a map to initialize this instance.
     */
    public FilterParams(Map<?, ?> map) {
        append(map);
    }

    /**
     * Creates a new FilterParams from a list of key-value pairs called tuples.
     *
     * @param tuples a list of values where odd elements are keys and the following
     *               even elements are values
     * @return a newly created FilterParams.
     */
    public static FilterParams fromTuples(Object... tuples) {
        StringValueMap map = StringValueMap.fromTuplesArray(tuples);
        return new FilterParams(map);
    }

    /**
     * Parses semicolon-separated key-value pairs and returns them as a
     * FilterParams.
     *
     * @param line semicolon-separated key-value list to initialize FilterParams.
     * @return a newly created FilterParams.
     * @see StringValueMap#toString()
     */
    public static FilterParams fromString(String line) {
        StringValueMap map = StringValueMap.fromString(line);
        return new FilterParams(map);
    }

    /**
     * Converts specified value into FilterParams.
     *
     * @param value value to be converted
     * @return a newly created FilterParams.
     */
    public static FilterParams fromValue(Object value) {
        if (value instanceof FilterParams)
            return (FilterParams) value;

        AnyValueMap map = AnyValueMap.fromValue(value);
        return new FilterParams(map);
    }

}
