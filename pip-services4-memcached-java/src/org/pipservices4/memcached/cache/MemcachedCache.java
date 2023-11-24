package org.pipservices4.memcached.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.rubyeye.xmemcached.XMemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;
import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.components.config.IConfigurable;
import org.pipservices4.commons.convert.JsonConverter;
import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.commons.errors.ConfigException;
import org.pipservices4.commons.errors.InvalidStateException;
import org.pipservices4.components.context.ContextResolver;
import org.pipservices4.components.refer.IReferenceable;
import org.pipservices4.components.refer.IReferences;
import org.pipservices4.components.run.IOpenable;
import org.pipservices4.logic.cache.ICache;
import org.pipservices4.config.connect.ConnectionResolver;
import org.pipservices4.components.context.IContext;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeoutException;

/**
 * Distributed cache that stores values in Memcached caching service.
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
 * <ul><li><code>\*:discovery:\*:\*:1.0</code>        (optional) <a href="https://pip-services4-java.github.io/pip-services4-config-java/org/pipservices4/config/connect/IDiscovery.html">IDiscovery</a> services to resolve connection
 * </ul>
 * <p>
 * ### Example ###
 * <p>
 *     var cache = new MemcachedCache();
 *     cache.configure(ConfigParams.fromTuples(
 *       "host", "localhost",
 *       "port", 11211
 *     ));
 * <p>
 *     cache.open("123");
 * <p>
 *     cache.store("123", "key1", "ABC");
 *     var value = cache.store("123", "key1"); // Result: "ABC"
 */
public class MemcachedCache implements ICache, IConfigurable, IReferenceable, IOpenable {

    private final ConnectionResolver _connectionResolver = new ConnectionResolver();

//    private int _maxKeySize = 250;
//    private long _maxExpiration = 2592000;
//    private long _maxValue = 1048576;
//    private int _poolSize = 5;
//    private int _reconnect = 10000;
//    private int _timeout = 5000;
//    private int _retries = 5;
//    private int _failures = 5;
//    private int _retry = 30000;
//    private boolean _remove = false;
//    private int _idle = 5000;

    private XMemcachedClient _client = null;

    /**
     * Configures component by passing configuration parameters.
     *
     * @param config configuration parameters to be set.
     */
    @Override
    public void configure(ConfigParams config) {
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
        return _client != null;
    }

    @Override
    public void open(IContext context) throws ApplicationException {
        var connections = this._connectionResolver.resolveAll(context);
        if (connections.isEmpty()) {
            throw new ConfigException(
                    context != null ? ContextResolver.getTraceId(context) : null,
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
                            context != null ? ContextResolver.getTraceId(context) : null,
                            "NOT_OPENED",
                            "Connection is not opened"
                    )
            );
        }
    }

    /**
     * Retrieves cached value from the cache using its key.
     * If value is missing in the cache or expired it returns null.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param key           a unique value key.
     * @return a cached value or <code>null</code> if nothing was found.
     */
    @Override
    public Object retrieve(IContext context, String key) {
        this.checkOpened(context);

        try {
            return _client.get(key);
        } catch (TimeoutException | InterruptedException | MemcachedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Stores value in the cache with expiration time.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param key           a unique value key.
     * @param value         a value to store.
     * @param timeout       expiration timeout in milliseconds.
     * @return the stored value
     */
    @Override
    public Object store(IContext context, String key, Object value, long timeout) {
        this.checkOpened(context);

        var timeoutInSec = (int) (timeout / 1000);

        try {
            String cacheValue;

            if (value instanceof String || value == null)
                cacheValue = String.valueOf(value);
            else if (value instanceof ZonedDateTime)
                cacheValue = ((ZonedDateTime) value).withZoneSameInstant(ZoneId.of("UTC"))
                        .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            else
                cacheValue = JsonConverter.toJson(value);

            return _client.set(key, timeoutInSec, cacheValue);
        } catch (TimeoutException | InterruptedException | MemcachedException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes a value from the cache by its key.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param key           a unique value key.
     */
    @Override
    public void remove(IContext context, String key) {
        this.checkOpened(context);

        try {
            _client.delete(key);
        } catch (TimeoutException | InterruptedException | MemcachedException e) {
            throw new RuntimeException(e);
        }
    }
}
