package org.pipservices4.logic.cache;

import static org.junit.Assert.*;

public class CacheFixture {

	private String KEY1 = "key1";
	private String KEY2 = "key2";

	private String VALUE1 = "value1";
	private String VALUE2 = "value2";
    private final ICache _cache;

    public CacheFixture(ICache cache) {
        _cache = cache;
    }

    public void testBasicOperations() {
    	// Set the first value
    	Object value = _cache.store(null, "test", 123, 0);
    	assertEquals(123, value);
    	
    	value = _cache.retrieve(null, "test");
    	assertEquals(123, value);

    	// Set null value
    	value = _cache.store(null, "test", null, 0);
    	assertNull(value);

		value = _cache.retrieve(null, "test");
		assertNull(value);
		
		// Set the second value
    	value = _cache.store(null, "test", "ABC", 0);
    	assertEquals("ABC", value);
    	
    	value = _cache.retrieve(null, "test");
    	assertEquals("ABC", value);

    	// Unset value
    	_cache.remove(null, "test");

		value = _cache.retrieve(null, "test");
		assertNull(value);
    }

    public void testReadAfterTimeout() {
    	// Set value
    	Object value = _cache.store(null, "test", 123, 50);
    	assertEquals(123, value);
    	
    	// Read the value
    	value = _cache.retrieve(null, "test");
    	assertEquals(123, value);
    	
    	// Wait
    	try {
    		Thread.sleep(200);
    	} catch (Exception ex) {
    		// Ignore..
    	}
    	
    	// Read the value again
    	value = _cache.retrieve(null, "test");
    	assertNull(value);
    }

	public void testStoreAndRetrieve() throws InterruptedException {
		this._cache.store(null, KEY1, VALUE1, 5000);
		this._cache.store(null, KEY2, VALUE2, 5000);

		Thread.sleep(500);

		Object val = _cache.retrieve(null, KEY1);
		assertNotNull(val);
		assertEquals(VALUE1, val);

		val = _cache.retrieve(null, KEY2);
		assertNotNull(val);
		assertEquals(VALUE2, val);
	}

	public void testRetrieveExpired() throws InterruptedException {
		this._cache.store(null, KEY1, VALUE1, 1000);

		Thread.sleep(1500);

		Object val = this._cache.retrieve(null, KEY1);
		assertNull(val);
	}

	public void testRemove() {
		this._cache.store(null, KEY1, VALUE1, 1000);

		this._cache.remove(null, KEY1);

		Object val = this._cache.retrieve(null, KEY1);
		assertNull(val);
	}
    
}