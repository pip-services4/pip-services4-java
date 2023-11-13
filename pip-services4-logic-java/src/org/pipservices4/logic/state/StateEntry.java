package org.pipservices4.logic.state;

/**
 * Data object to store state values with their keys used by {@link StateEntry}
 */
public class StateEntry {

    private final String _key;
    private Object _value;
    private long _lastUpdateTime;

    public StateEntry(String key, Object value) {
        this._key = key;
        this._value = value;
        this._lastUpdateTime = System.currentTimeMillis();
    }

    /**
     * Gets the key to locate the state value.
     *
     * @return the value key.
     */
    public String getKey() {
        return _key;
    }

    /**
     * Gets the sstate value.
     *
     * @return the value object.
     */
    public Object getValue() {
        return _value;
    }

    /**
     * Gets the last update time.
     *
     * @return the timestamp when the value ware stored.
     */
    public long getLastUpdateTime() {
        return _lastUpdateTime;
    }

    /**
     * Sets a new state value.
     *
     * @param value a new cached value.
     */
    public void setValue(Object value) {
        this._value = value;
        this._lastUpdateTime = System.currentTimeMillis();
    }
}
