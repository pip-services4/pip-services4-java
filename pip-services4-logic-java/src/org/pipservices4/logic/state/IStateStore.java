package org.pipservices4.logic.state;

import org.pipservices4.components.context.IContext;

import java.util.List;

/**
 * Interface for state storages that are used to store and retrieve transaction states.
 */
public interface IStateStore {

    /**
     * Loads state from the store using its key.
     * If value is missing in the store it returns null.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param key           a unique state key.
     * @return the state value or <code>null</code> if value wasn't found.
     */
    <T> T load(IContext context, String key);

    /**
     * Loads an array of states from the store using their keys.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param keys          unique state keys.
     * @return an array with state values and their corresponding keys.
     */
    <T> List<StateValue<T>> loadBulk(IContext context, List<String> keys);

    /**
     * Saves state into the store.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param key           a unique state key.
     * @param value         a state value.
     * @return The state that was stored in the store.
     */
    <T> T save(IContext context, String key, T value);

    /**
     * Deletes a state from the store by its key.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param key           a unique value key.
     * @return deleted item.
     */
    <T> T delete(IContext context, String key);
}
