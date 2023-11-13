package org.pipservices4.logic.lock;

import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.components.context.IContext;

/**
 * Interface for locks to synchronize work or parallel processes and to prevent collisions.
 * <p>
 * The lock allows to manage multiple locks identified by unique keys.
 */
public interface ILock {
    /**
     * Makes a single attempt to acquire a lock by its key.
     * It returns immediately a positive or negative result.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param key           a unique lock key to acquire.
     * @param ttl           a lock timeout (time to live) in milliseconds.
     * @return <code>true</code> if the lock was acquired and <code>false</code> otherwise.
     */
    boolean tryAcquireLock(IContext context, String key, int ttl);

    /**
     * Makes multiple attempts to acquire a lock by its key within give time interval.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param key           a unique lock key to acquire.
     * @param ttl           a lock timeout (time to live) in milliseconds.
     * @param timeout       a lock acquisition timeout.
     */
    void acquireLock(IContext context, String key, int ttl, long timeout) throws InterruptedException, ApplicationException;

    /**
     * Releases prevously acquired lock by its key.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param key           a unique lock key to release.
     */
    void releaseLock(IContext context, String key);
}
