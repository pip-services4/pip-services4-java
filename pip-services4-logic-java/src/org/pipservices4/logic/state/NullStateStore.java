package org.pipservices4.logic.state;

import org.pipservices4.components.context.IContext;

import java.util.List;

/**
 * Dummy state store implementation that doesn't do anything.
 *
 * It can be used in testing or in situations when state management is not required
 * but shall be disabled.
 */
public class NullStateStore implements IStateStore {
    /**
     * Loads state from the store using its key.
     * If value is missing in the stored it returns null.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param key           a unique state key.
     * @return the state value or <code>null</code> if value wasn't found.
     */
    @Override
    public <T> T load(IContext context, String key) {
        return null;
    }

    /**
     * Loads an array of states from the store using their keys.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param keys          unique state keys.
     * @return an array with state values and their corresponding keys.
     */
    @Override
    public <T> List<StateValue<T>> loadBulk(IContext context, List<String> keys) {
        return List.of();
    }

    /**
     * Saves state into the store.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param key           a unique state key.
     * @param value         a state value.
     * @return The state that was stored in the store.
     */
    @Override
    public <T> T save(IContext context, String key, T value) {
        return value;
    }

    /**
     * Deletes a state from the store by its key.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param key           a unique value key.
     * @return deleted value
     */
    @Override
    public <T> T delete(IContext context, String key) {
        return null;
    }
}
