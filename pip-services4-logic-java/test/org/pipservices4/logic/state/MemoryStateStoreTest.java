package org.pipservices4.logic.state;

import org.junit.Before;
import org.junit.Test;

public class MemoryStateStoreTest {
    private MemoryStateStore _cache;
    private StateStoreFixture _fixture;

    @Before
    public void setup() {
        _cache = new MemoryStateStore();
        _fixture = new StateStoreFixture(_cache);
    }

    @Test
    public void testSaveAndLoad() {
        _fixture.testSaveAndLoad();
    }

    @Test
    public void testDelete() {
        _fixture.testDelete();
    }
}
