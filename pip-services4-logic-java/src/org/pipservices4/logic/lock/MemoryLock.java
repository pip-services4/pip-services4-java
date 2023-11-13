package org.pipservices4.logic.lock;

import org.pipservices4.components.context.IContext;

import java.util.HashMap;

/**
 * Lock that is used to synchronize execution within one process using shared memory.
 *
 * Remember: This implementation is not suitable for synchronization of distributed processes.
 *
 * ### Configuration parameters ###
 *
 * <ul>
 * options:
 * <li> - retry_timeout:   timeout in milliseconds to retry lock acquisition. (Default: 100)
 * <li>
 * </ul>
 *
 * ### Example ###
 * <pre>
 * {@code
 * MemoryLock lock = new MemoryLock();
 * try {
 *  // Processing...
 * }
 * finally  {
 * lock.releaseLock("123", "key1");
 *  // Continue...
 * });
 * }
 *
 * @see ILock
 * @see Lock
 */
public class MemoryLock extends Lock {
    private final HashMap<String, Long> _locks = new HashMap<>();

    /**
     * Makes a single attempt to acquire a lock by its key.
     * It returns immediately a positive or negative result.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param key           a unique lock key to acquire.
     * @param ttl           a lock timeout (time to live) in milliseconds.
     * @return <code>true</code> if the lock was acquired and <code>false</code> otherwise.
     */
    @Override
    public boolean tryAcquireLock(IContext context, String key, int ttl) {
        long now = System.currentTimeMillis();

        synchronized (_locks) {
            Long expireTime = this._locks.getOrDefault(key, null);

            if (expireTime == null || expireTime < now) {
                _locks.put(key, now + ttl);
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Releases the lock with the given key.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param key           a unique lock key to release.
     */
    @Override
    public void releaseLock(IContext context, String key) {
        synchronized (_locks) {
            this._locks.remove(key);
        }
    }
}
