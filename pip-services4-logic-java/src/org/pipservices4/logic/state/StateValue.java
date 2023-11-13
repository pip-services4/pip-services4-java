package org.pipservices4.logic.state;

/**
 *  A data object that holds a retrieved state value with its key.
 * @param <T>
 */
public class StateValue<T> {
    /**
     *  A unique state key
     */
    public String key;
    /**
     * A stored state value;
     */
    public T value;

    public StateValue() {
    }

    public StateValue(String key, T value) {
        this.key = key;
        this.value = value;
    }
}
