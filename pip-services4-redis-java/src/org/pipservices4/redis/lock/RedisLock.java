package org.pipservices4.redis.lock;

import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.components.config.IConfigurable;
import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.commons.errors.ConfigException;
import org.pipservices4.commons.errors.ConnectionException;
import org.pipservices4.commons.errors.InvalidStateException;
import org.pipservices4.components.context.ContextResolver;
import org.pipservices4.components.context.IContext;
import org.pipservices4.components.refer.IReferenceable;
import org.pipservices4.components.refer.IReferences;
import org.pipservices4.components.run.IOpenable;
import org.pipservices4.config.auth.CredentialResolver;
import org.pipservices4.config.connect.ConnectionResolver;
import org.pipservices4.logic.lock.Lock;
import org.pipservices4.data.keys.IdGenerator;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.params.SetParams;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Distributed lock that is implemented based on Redis in-memory database.
 * <p>
 * ### Configuration parameters ###
 *
 * <pre>
 * - connection(s):
 *   - discovery_key:         (optional) a key to retrieve the connection from {@link org.pipservices4.config.connect.IDiscovery}
 *   - host:                  host name or IP address
 *   - port:                  port number
 *   - uri:                   resource URI or connection string with all parameters in it
 * - credential(s):
 *   - store_key:             key to retrieve parameters from credential store
 *   - username:              user name (currently is not used)
 *   - password:              user password
 * - options:
 *   - retry_timeout:         timeout in milliseconds to retry lock acquisition. (Default: 100)
 *   - retries:               number of retries (default: 3)
 * </pre>
 * <p>
 * ### References ###
 * <p>
 * - *:discovery:*:*:1.0        (optional) [{@link org.pipservices4.config.connect.IDiscovery} services to resolve connection
 * - *:credential-store:*:*:1.0 (optional) Credential stores to resolve credential
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * var lock = new RedisLock();
 * lock.configure(ConfigParams.fromTuples(
 *         "connection.host", "localhost",
 *         "connection.port", 6379
 * ));
 * lock.open("123");
 * lock.tryAcquireLock("123", "key1", 5000);
 * try {
 *     // Processing...
 * } finally {
 *     lock.releaseLock("123", "key1");
 * }
 * }
 * </pre>
 */
public class RedisLock extends Lock implements IConfigurable, IReferenceable, IOpenable {

    private final ConnectionResolver _connectionResolver = new ConnectionResolver();
    private final CredentialResolver _credentialResolver = new CredentialResolver();

    private final String _lock = IdGenerator.nextLong();
    private int _timeout = 30000;
    private int _retries = 3;

    private Jedis _client;

    /**
     * Configures component by passing configuration parameters.
     *
     * @param config configuration parameters to be set.
     */
    @Override
    public void configure(ConfigParams config) {
        this._connectionResolver.configure(config);
        this._credentialResolver.configure(config);

        this._timeout = config.getAsIntegerWithDefault("options.timeout", this._timeout);
        this._retries = config.getAsIntegerWithDefault("options.retries", this._retries);
    }

    /**
     * Sets references to dependent components.
     *
     * @param references references to locate the component dependencies.
     */
    @Override
    public void setReferences(IReferences references) {
        this._connectionResolver.setReferences(references);
        this._credentialResolver.setReferences(references);
    }

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
        var connection = this._connectionResolver.resolve(context);
        var credential = this._credentialResolver.lookup(context);

        if (connection == null)
            throw new ConfigException(
                    context != null ? ContextResolver.getTraceId(context) : null,
                    "NO_CONNECTION",
                    "Connection is not configured"
            );

        // Retry strategy
        Jedis jedis = null;
        var startTime = ZonedDateTime.now();
        var tryCount = 1;
        for (; tryCount <= _retries; tryCount++) {
            try {
                if ((ZonedDateTime.now().toInstant().toEpochMilli() - startTime.toInstant().toEpochMilli()) >= _timeout)
                    throw new RuntimeException(
                            new ConnectionException(
                                    context != null ? ContextResolver.getTraceId(context) : null,
                                    "NO_CONNECTION",
                                    "Redis Connection timeout"
                            )
                    );

                if (jedis != null && jedis.isConnected())
                    break;

                jedis = new Jedis(
                        connection.getAsStringWithDefault("host", "localhost"),
                        connection.getAsIntegerWithDefault("port", 6379)
                );

                jedis.connect();
            } catch (JedisConnectionException ex) {
                if (tryCount >= _retries)
                    throw new RuntimeException(ex);
            }
        }

        if (credential != null && credential.getPassword() != null)
            jedis.auth(credential.getPassword());

        _client = jedis;
    }

    /**
     * Closes component and frees used resources.
     *
     * @param context     (optional) a context to trace execution through call chain.
     */
    @Override
    public void close(IContext context) {
        if (this._client == null) return;

        this._client.close();
        this._client.quit();
        this._client = null;
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
     * Makes a single attempt to acquire a lock by its key.
     * It returns immediately a positive or negative result.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param key           a unique lock key to acquire.
     * @param ttl           a lock timeout (time to live) in milliseconds.
     * @return <code>true</code> if lock was successfully acquired and <code>false</code> otherwise.
     */
    @Override
    public boolean tryAcquireLock(IContext context, String key, int ttl) {
        this.checkOpened(context);

        var res = this._client.set(key, _lock, new SetParams().nx().px(ttl));
        return Objects.equals(res, "OK");
    }

    /**
     * Releases prevously acquired lock by its key.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param key           a unique lock key to release.
     */
    @Override
    public void releaseLock(IContext context, String key) {
        this.checkOpened(context);

        // Start transaction on key
        _client.watch(key);

        // Read and check if lock is the same
        var result = _client.get(key);

        // Remove the lock if it matches
        if (Objects.equals(result, this._lock)) {
            Transaction transaction = _client.multi();
            transaction.del(key);
            transaction.exec();
            transaction.close();
        } else {
            // Cancel transaction if it doesn't match
            this._client.unwatch();
        }
    }
}
