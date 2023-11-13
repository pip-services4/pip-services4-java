package org.pipservices4.logic.lock;

import org.pipservices4.commons.errors.ApplicationException;

import static org.junit.Assert.*;

public class LockFixture {

    private final String LOCK1 = "lock_1";
    private final String LOCK2 = "lock_2";
    private final String LOCK3 = "lock_3";
    private final ILock _lock;

    public LockFixture(ILock lock) {
        _lock = lock;
    }

    public void testTryAcquireLock() {
        // Try to acquire lock for the first time
        boolean ok = this._lock.tryAcquireLock(null, LOCK1, 3000);
        assertTrue(ok);

        // Try to acquire lock for the second time
        ok = this._lock.tryAcquireLock(null, LOCK1, 3000);
        assertFalse(ok);

        // Release the lock
        this._lock.releaseLock(null, LOCK1);

        // Try to acquire lock for the third time
        ok = this._lock.tryAcquireLock(null, LOCK1, 3000);
        assertTrue(ok);

        this._lock.releaseLock(null, LOCK1);
    }

    public void testAcquireLock() throws ApplicationException, InterruptedException {
        // Acquire lock for the first time
        this._lock.acquireLock(null, LOCK2, 3000, 1000);

        // Acquire lock for the second time
        try {
            this._lock.acquireLock(null, LOCK2, 3000, 1000);
            fail("Expected exception on the second lock attempt");
        } catch (Exception e) {
            // Expected exception...
        }

        // Release the lock
        this._lock.releaseLock(null, LOCK2);

        // Acquire lock for the third time
        this._lock.acquireLock(null, LOCK2, 3000, 1000);

        this._lock.releaseLock(null, LOCK2);
    }

    public void testReleaseLock() {
        // Acquire lock for the first time
        boolean ok = this._lock.tryAcquireLock(null, LOCK3, 3000);
        assertTrue(ok);

        // Release the lock for the first time
        this._lock.releaseLock(null, LOCK3);

        // Release the lock for the second time
        this._lock.releaseLock(null, LOCK3);
    }
}
