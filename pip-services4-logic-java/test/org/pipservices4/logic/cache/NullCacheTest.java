package org.pipservices4.logic.cache;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class NullCacheTest {
    private NullCache cache = null;

    @Before
    public void setup() {
        cache = new NullCache();
    }

    @Test
    public void testRetrieveReturnsNull() {
        Object val = cache.retrieve(null, "key1");
        assertNull(val);
    }

    @Test
    public void testStoreReturnsSameValue() {
        String key = "key1";
        String initVal = "value1";

        Object val = cache.store(null, key, initVal, 0);
        assertEquals(initVal, val);
    }
}
