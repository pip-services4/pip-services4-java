package org.pipservices4.logic.build;

import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.build.Factory;
import org.pipservices4.logic.cache.ICache;
import org.pipservices4.logic.cache.MemoryCache;
import org.pipservices4.logic.cache.NullCache;
import org.pipservices4.logic.lock.MemoryLock;
import org.pipservices4.logic.lock.NullLock;
import org.pipservices4.logic.state.MemoryStateStore;
import org.pipservices4.logic.state.NullStateStore;

/**
 * Creates logic components by their descriptors.
 *
 * @see Factory
 * @see NullCache
 * @see ICache
 * @see MemoryCache
 */
public class DefaultLogicFactory extends Factory {
    private static final Descriptor Descriptor = new Descriptor("pip-services", "factory", "logic", "default", "1.0");
    private static final Descriptor NullCacheDescriptor = new Descriptor("pip-services", "cache", "null", "*",
            "1.0");
    private static final Descriptor MemoryCacheDescriptor = new Descriptor("pip-services", "cache", "memory", "*",
            "1.0");
    private static final Descriptor NullLockDescriptor = new Descriptor("pip-services", "lock", "null", "*", "1.0");
    private static final Descriptor MemoryLockDescriptor = new Descriptor("pip-services", "lock", "memory", "*",
            "1.0");
    private static final Descriptor NullStateStoreDescriptor = new Descriptor("pip-services", "state-store", "null", "*", "1.0");
    private static final Descriptor MemoryStateStoreDescriptor = new Descriptor("pip-services", "state-store", "memory", "*", "1.0");

    /**
     * Create a new instance of the factory.
     */
    public DefaultLogicFactory() {
        super();
        registerAsType(DefaultLogicFactory.MemoryCacheDescriptor, MemoryCache.class);
        registerAsType(DefaultLogicFactory.NullCacheDescriptor, NullCache.class);
        registerAsType(DefaultLogicFactory.NullLockDescriptor, NullLock.class);
        registerAsType(DefaultLogicFactory.MemoryLockDescriptor, MemoryLock.class);
        registerAsType(DefaultLogicFactory.MemoryStateStoreDescriptor, MemoryStateStore.class);
        registerAsType(DefaultLogicFactory.NullStateStoreDescriptor, NullStateStore.class);
    }
}
