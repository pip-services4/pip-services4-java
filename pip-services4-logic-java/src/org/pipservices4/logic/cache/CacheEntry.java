package org.pipservices4.logic.cache;

/**
 * Data object to store cached values with their keys used by {@link MemoryCache}
 */
public class CacheEntry {
	private long _expiration;
	private final String _key;
	private Object _value;

	/**
	 * Creates a new instance of the cache entry and assigns its values.
	 * 
	 * @param key     a unique key to locate the value.
	 * @param value   a value to be stored.
	 * @param timeout expiration timeout in milliseconds.
	 */
	public CacheEntry(String key, Object value, long timeout) {
		_key = key;
		_value = value;
		_expiration = System.currentTimeMillis() + timeout;
	}

	/**
	 * Gets the key to locate the cached value.
	 * 
	 * @return the value key.
	 */
	public String getKey() {
		return _key;
	}

	/**
	 * Gets the cached value.
	 * 
	 * @return the value object.
	 */
	public Object getValue() {
		return _value;
	}

	/**
	 * Sets a new value and extends its expiration.
	 * 
	 * @param value   a new cached value.
	 * @param timeout a expiration timeout in milliseconds.
	 */
	public void setValue(Object value, long timeout) {
		_value = value;
		_expiration = System.currentTimeMillis() + timeout;
	}

	/**
	 * Gets the expiration timeout.
	 * 
	 * @return the expiration timeout in milliseconds.
	 */
	public long getExpiration() {
		return _expiration;
	}

	/**
	 * Checks if this value already expired.
	 * 
	 * @return true if the value already expires and false otherwise.
	 */
	public boolean isExpired() {
		return _expiration < System.currentTimeMillis();
	}
}
