package org.pipservices4.components.context;

/**
 * Interface to specify execution context.
 *
 * @see Context
 */

public interface IContext {
    /**
     * Gets a map element specified by its key.
     *
     * @param key     a key of the element to get.
     * @return       the value of the map element.
     */
    Object get(String key);
}
