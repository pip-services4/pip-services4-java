package org.pipservices4.memcached.lock;

import net.rubyeye.xmemcached.XMemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;
import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.components.config.IConfigurable;
import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.commons.errors.ConfigException;
import org.pipservices4.commons.errors.InvalidStateException;
import org.pipservices4.components.context.ContextResolver;
import org.pipservices4.components.refer.IReferenceable;
import org.pipservices4.components.refer.IReferences;
import org.pipservices4.components.run.IOpenable;
import org.pipservices4.config.connect.ConnectionResolver;
import org.pipservices4.logic.lock.Lock;
import org.pipservices4.components.context.IContext;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Distributed lock that implemented based on Memcaches caching service.
 * <p>
 * The current implementation does not support authentication.
 * <p>
 * ### Configuration parameters ###
 * <p>
 * <ul>
 * <li>connection(s):
 * <ul>
 *   <li>discovery_key:         (optional) a key to retrieve the connection from <a href="https://pip-services4-java.github.io/pip-services4-config-java/org/pipservices4/config/connect/IDiscovery.html">IDiscovery</a> IDiscovery</a>
 *   <li>host:                  host name or IP address
 *   <li>port:                  port number
 *   <li>uri:                   resource URI or connection string with all parameters in it
 * </ul>
 * <li>options:
 * <ul>
 *   <li>retry_timeout:         timeout in milliseconds to retry lock acquisition. (Default: 100)
 *   <li>max_size:              maximum number of values stored in this cache (default: 1000)
 *   <li>max_key_size:          maximum key length (default: 250)
 *   <li>max_expiration:        maximum expiration duration in milliseconds (default: 2592000)
 *   <li>max_value:             maximum value length (default: 1048576)
 *   <li>pool_size:             pool size (default: 5)
 *   <li>reconnect:             reconnection timeout in milliseconds (default: 10 sec)
 *   <li>retries:               number of retries (default: 3)
 *   <li>timeout:               default caching timeout in milliseconds (default: 1 minute)
 *   <li>failures:              number of failures before stop retrying (default: 5)
 *   <li>retry:                 retry timeout in milliseconds (default: 30 sec)
 *   <li>idle:                  idle timeout before disconnect in milliseconds (default: 5 sec)
 * </ul>
 * </ul>
 * <p>
 * ### References ###
 * <p>
 * <ul><li><code>\*:discovery:\*:\*:1.0</code>        (optional) <a href="https://pip-services4-java.github.io/pip-services4-config-java/org/pipservices4/config/connect/IDiscovery.html">IDiscovery</a> IDiscovery</a> services to resolve connection
 * </ul>
 * <p>
 * ### Example ###
 * <p>
 *     var lock = new MemcachedLock();
 *     lock.configure(ConfigParams.fromTuples(
 *       "host", "localhost",
 *       "port", 11211
 *     ));
 * <p>
 *     lock.open("123");
 * <p>
 *     lock.acquire("123", "key1");
 *     try {
 *       // Processing...
 *     } finally {
 *       lock.releaseLock("123", "key1");
 *     }
 */
public class MemcachedLock extends Lock implements IConfigurable, IReferenceable, IOpenable {
    private final ConnectionResolver _connectionResolver = new ConnectionResolver();

//    private int _maxKeySize = 250;
//    private int _maxExpiration = 2592000;
//    private int _maxValue = 1048576;
//    private int _poolSize = 5;
//    private int _reconnect = 10000;
//    private int _timeout = 5000;
//    private int _retries = 5;
//    private int _failures = 5;
//    private int _retry = 30000;
//    private boolean _remove = false;
//    private int _idle = 5000;

    private XMemcachedClient _client = null;

    @Override
    public void configure(ConfigParams config) {
        super.configure(config);

        this._connectionResolver.configure(config);

//        todo this options is not supported
//        this._maxKeySize = config.getAsIntegerWithDefault("options.max_key_size", this._maxKeySize);
//        this._maxExpiration = config.getAsLongWithDefault("options.max_expiration", this._maxExpiration);
//        this._maxValue = config.getAsLongWithDefault("options.max_value", this._maxValue);
//        this._poolSize = config.getAsIntegerWithDefault("options.pool_size", this._poolSize);
//        this._reconnect = config.getAsIntegerWithDefault("options.reconnect", this._reconnect);
//        this._timeout = config.getAsIntegerWithDefault("options.timeout", this._timeout);
//        this._retries = config.getAsIntegerWithDefault("options.retries", this._retries);
//        this._failures = config.getAsIntegerWithDefault("options.failures", this._failures);
//        this._retry = config.getAsIntegerWithDefault("options.retry", this._retry);
//        this._remove = config.getAsBooleanWithDefault("options.remove", this._remove);
//        this._idle = config.getAsIntegerWithDefault("options.idle", this._idle);
    }

    /**
     * Sets references to dependent components.
     *
     * @param references references to locate the component dependencies.
     */
    @Override
    public void setReferences(IReferences references) {
        this._connectionResolver.setReferences(references);
    }

    /**
     * Checks if the component is opened.
     *
     * @return true if the component has been opened and false otherwise.
     */
    @Override
    public boolean isOpen() {
        return this._client != null;
    }

    /**
     * Opens the component.
     *
     * @param context     (optional) a context to trace execution through call chain.
     */
    @Override
    public void open(IContext context) throws ApplicationException {
        var connections = this._connectionResolver.resolveAll(context);
        if (connections.isEmpty()) {
            throw new ConfigException(
                    ContextResolver.getTraceId(context),
                    "NO_CONNECTION",
                    "Connection is not configured"
            );
        }

        try {
            _client = new XMemcachedClient();

            for (var connection : connections) {
                var host = connection.getHost();
                var port = connection.getAsIntegerWithDefault("port", 11211);

                _client.addServer(host, port);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Closes component and frees used resources.
     *
     * @param context     (optional) a context to trace execution through call chain.
     */
    @Override
    public void close(IContext context) {
        try {
            _client.shutdown();
            _client = null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkOpened(IContext context) {
        if (!this.isOpen()) {
            throw new RuntimeException(
                    new InvalidStateException(
                            ContextResolver.getTraceId(context),
                            "NOT_OPENED",
                            "Connection is not opened"
                    )
            );
        }
    }

    /**
     * Makes a single attempt to acquire a lock by its key.
     * It returns immediately a positive or negative result.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param key           a unique lock key to acquire.
     * @param ttl           a lock timeout (time to live) in milliseconds.
     * @return <code>true</code> if lock was successfull and <code>false</code> otherwise.
     */
    @Override
    public boolean tryAcquireLock(IContext context, String key, int ttl) {
        this.checkOpened(context);

        var lifetimeInSec = ttl / 1000;

        try {
            return this._client.add(key, lifetimeInSec, "lock");
        } catch (TimeoutException | InterruptedException | MemcachedException e) {
            if (e.getMessage().contains("not stored"))
                return false;
            throw new RuntimeException(e);
        }
    }

    /**
     * Releases previously acquired lock by its key.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param key           a unique lock key to release.
     */
    @Override
    public void releaseLock(IContext context, String key) {
        this.checkOpened(context);

        try {
            _client.delete(key);
        } catch (TimeoutException | InterruptedException | MemcachedException e) {
            throw new RuntimeException(e);
        }
    }
}
