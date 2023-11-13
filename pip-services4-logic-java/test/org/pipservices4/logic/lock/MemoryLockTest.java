package org.pipservices4.logic.lock;

import org.junit.Before;
import org.junit.Test;
import org.pipservices4.commons.errors.ApplicationException;

public class MemoryLockTest {
    private MemoryLock _lock;
    private LockFixture _fixture;

    @Before
    public void setup() {
        _lock = new MemoryLock();
        _fixture = new LockFixture(_lock);
    }

    @Test
    public void testTryAcquireLock() {
        _fixture.testTryAcquireLock();
    }

    @Test
    public void testAcquireLock() throws ApplicationException, InterruptedException {
        _fixture.testAcquireLock();
    }

    @Test
    public void testReleaseLock() {
        _fixture.testReleaseLock();
    }
}
