package org.pipservices4.logic.cache;

import org.junit.*;

public class MemoryCacheTest {
    private ICache cache;
    private CacheFixture fixture;

    @Before
    public void setUp() throws Exception {
        cache = new MemoryCache();
        fixture = new CacheFixture(cache);
    }

    @Test
    public void testBasicOperations() {
        fixture.testBasicOperations();
    }

    @Test
    public void testReadAfterTimeout() {
        fixture.testReadAfterTimeout();
    }

    @Test
    public void testStoreAndRetrieve() throws InterruptedException {
        fixture.testStoreAndRetrieve();
    }

    @Test
    public void testRetrieveExpired() throws InterruptedException {
        fixture.testRetrieveExpired();
    }

    @Test
    public void testRemove() {
        fixture.testRemove();
    }
}
