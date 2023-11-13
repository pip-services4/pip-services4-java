package org.pipservices4.logic.cache;

import org.pipservices4.components.context.IContext;

/**
 * Dummy cache implementation that doesn't do anything.
 * <p>
 * It can be used in testing or in situations when cache is required
 * but shall be disabled.
 * 
 * @see ICache
 */
public class NullCache implements ICache {
	/**
	 * Creates instance of null cache component.
	 */
	public NullCache() {
	}

	/**
	 * Retrieves cached value from the cache using its key. If value is missing in
	 * the cache or expired it returns null.
	 * 
	 * @param context     (optional) a context to trace execution through call chain.
	 * @param key           a unique value key.
	 * @return a cached value or null if value wasn't found or timeout expired.
	 */
	public Object retrieve(IContext context, String key) {
		return null;
	}

	/**
	 * Stores value in the cache with expiration time.
	 * 
	 * @param context     (optional) a context to trace execution through call chain.
	 * @param key           a unique value key.
	 * @param value         a value to store.
	 * @param timeToLive       expiration timeout in milliseconds.
	 * @return a cached value stored in the cache.
	 */
	public Object store(IContext context, String key, Object value, long timeToLive) {
		return value;
	}

	/**
	 * Removes a value from the cache by its key.
	 * 
	 * @param context     (optional) a context to trace execution through call chain.
	 * @param key           a unique value key.
	 */
	public void remove(IContext context, String key) {
	}
}
