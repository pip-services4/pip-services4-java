package org.pipservices4.mysql.connect;

import java.sql.*;
import java.util.Map;

import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.components.config.IConfigurable;
import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.commons.errors.ConfigException;
import org.pipservices4.commons.errors.ConnectionException;
import org.pipservices4.components.context.ContextResolver;
import org.pipservices4.components.refer.IReferenceable;
import org.pipservices4.components.refer.IReferences;
import org.pipservices4.components.refer.ReferenceException;
import org.pipservices4.components.run.IOpenable;
import org.pipservices4.observability.log.CompositeLogger;
import org.pipservices4.components.context.IContext;

/**
 * MySQL connection using plain driver.
 * <p>
 * By defining a connection and sharing it through multiple persistence components
 * you can reduce number of used database connections.
 * <p>
 * ### Configuration parameters ###
 *
 * <pre>
 * - connection(s):
 *   - discovery_key:             (optional) a key to retrieve the connection from {@link org.pipservices4.config.connect.IDiscovery}
 *   - host:                      host name or IP address
 *   - port:                      port number (default: 3306)
 *   - uri:                       resource URI or connection string with all parameters in it
 * - credential(s):
 *   - store_key:                 (optional) a key to retrieve the credentials from {@link org.pipservices4.config.auth.ICredentialStore}
 *   - username:                  user name
 *   - password:                  user password
 * - options:
 *   - connect_timeout:      (optional) number of milliseconds to wait before timing out when connecting a new client (default: 0)
 *   - idle_timeout:         (optional) number of milliseconds a client must sit idle in the pool and not be checked out (default: 10000)
 *   - max_pool_size:        (optional) maximum number of clients the pool should contain (default: 10)
 *
 * ### References ###
 *
 * - *:logger:*:*:1.0          (optional) {@link org.pipservices4.observability.log.ILogger} components to pass log messages
 * - *:discovery:*:*:1.0        (optional) {@link org.pipservices4.config.connect.IDiscovery} services
 * - *:credential-store:*:*:1.0 (optional) Credential stores to resolve credentials
 * <pre/>
 */
public class MySqlConnection implements IReferenceable, IConfigurable, IOpenable {

    private final ConfigParams _defaultConfig = ConfigParams.fromTuples(
            // connections.*
            // credential.*

            "options.connect_timeout", 0,
            "options.idle_timeout", 10000,
            "options.max_pool_size", 3
    );

    /**
     * The logger.
     */
    protected CompositeLogger _logger = new CompositeLogger();
    /**
     * The connection resolver.
     */
    protected MySqlConnectionResolver _connectionResolver = new MySqlConnectionResolver();
    /**
     * The configuration options.
     */
    protected ConfigParams _options = new ConfigParams();

    /**
     * The MySQL connection pool object.
     */
    protected Connection _connection;
    /**
     * The MySQL database name.
     */
    protected String _databaseName;

    /**
     * Configures component by passing configuration parameters.
     *
     * @param config configuration parameters to be set.
     */
    @Override
    public void configure(ConfigParams config) throws ConfigException {
        config = config.setDefaults(this._defaultConfig);

        this._connectionResolver.configure(config);

        this._options = this._options.override(config.getSection("options"));
    }

    /**
     * Sets references to dependent components.
     *
     * @param references references to locate the component dependencies.
     */
    @Override
    public void setReferences(IReferences references) throws ReferenceException, ConfigException {
        this._logger.setReferences(references);
        this._connectionResolver.setReferences(references);
    }


    /**
     * Checks if the component is opened.
     *
     * @return true if the component has been opened and false otherwise.
     */
    @Override
    public boolean isOpen() {
        return this._connection != null;
    }

    private String composeUriSettings(String uri) {
        var maxPoolSize = this._options.getAsNullableInteger("max_pool_size");
        var connectTimeoutMS = this._options.getAsNullableInteger("connect_timeout");
        var idleTimeoutMS = this._options.getAsNullableInteger("idle_timeout");

        var settings = Map.of(
                "allowMultiQueries", true,
                "connectionLimit", maxPoolSize,
                "connectTimeout", connectTimeoutMS,
                "insecureAuth", true
//            idleTimeoutMillis: idleTimeoutMS
        );

        StringBuilder params = new StringBuilder();
        for (var key : settings.keySet()) {
            if (!params.isEmpty()) {
                params.append('&');
            }

            params.append(key);

            var value = settings.get(key);
            if (value != null) {
                params.append("=").append(value);
            }
        }
        if (uri.indexOf('?') < 0)
            uri += '?' + params.toString();
        else
            uri += '&' + params.toString();

        return uri;
    }

    /**
     * Opens the component.
     *
     * @param context 	(optional) execution context to trace execution through call chain.
     */
    @Override
    public void open(IContext context) throws ApplicationException {
        var uri = this._connectionResolver.resolve(context);

        this._logger.debug(context, "Connecting to MySQL...");

        try {
            uri = this.composeUriSettings(uri);

            Class.forName("com.mysql.cj.jdbc.Driver");

            this._connection = DriverManager.getConnection("jdbc:" + uri);
            this._databaseName = _connection.getMetaData().getDatabaseProductName();

        } catch (Exception ex) {
            throw new ConnectionException(
                    context != null ? ContextResolver.getTraceId(context) : null,
                    "CONNECT_FAILED",
                    "Connection to MySQL failed"
            ).withCause(ex);
        }
    }

    /**
     * Closes component and frees used resources.
     *
     * @param context 	(optional) execution context to trace execution through call chain.
     */
    @Override
    public void close(IContext context) throws ApplicationException {
        if (this._connection == null)
            return;

        try {
            this._connection.close();

            this._logger.debug(context, "Disconnected from MySQL database %s", this._databaseName);

            this._connection = null;
            this._databaseName = null;
        } catch (Exception ex) {
            throw new ConnectionException(
                    context != null ? ContextResolver.getTraceId(context) : null,
                    "DISCONNECT_FAILED",
                    "Disconnect from MySQL failed: "
            ).withCause(ex);
        }
    }

    public Connection getConnection() {
        return this._connection;
    }

    public String getDatabaseName() {
        return this._databaseName;
    }
}
