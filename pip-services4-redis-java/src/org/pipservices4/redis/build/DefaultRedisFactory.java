package org.pipservices4.redis.build;

import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.build.Factory;
import org.pipservices4.redis.cache.RedisCache;
import org.pipservices4.redis.lock.RedisLock;

/**
 * Creates Redis components by their descriptors.
 *
 * @see RedisCache
 * @see RedisLock
 */
public class DefaultRedisFactory extends Factory {
    private static final Descriptor RedisCacheDescriptor = new Descriptor("pip-services", "cache", "redis", "*", "1.0");
    private static final Descriptor RedisLockDescriptor = new Descriptor("pip-services", "lock", "redis", "*", "1.0");

    /**
     * Create a new instance of the factory.
     */
    public DefaultRedisFactory() {
        super();
        this.registerAsType(DefaultRedisFactory.RedisCacheDescriptor, RedisCache.class);
        this.registerAsType(DefaultRedisFactory.RedisLockDescriptor, RedisLock.class);
    }
}
