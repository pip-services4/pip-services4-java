package org.pipservices4.mysql.connect;

import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.components.config.IConfigurable;
import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.commons.errors.ConfigException;
import org.pipservices4.components.context.ContextResolver;
import org.pipservices4.components.refer.IReferenceable;
import org.pipservices4.components.refer.IReferences;
import org.pipservices4.config.auth.CredentialParams;
import org.pipservices4.config.auth.CredentialResolver;
import org.pipservices4.config.connect.ConnectionParams;
import org.pipservices4.config.connect.ConnectionResolver;
import org.pipservices4.components.context.IContext;

import java.util.List;

/**
 * Helper class that resolves MySQL connection and credential parameters,
 * validates them and generates a connection URI.
 * <p>
 * It is able to process multiple connections to MySQL cluster nodes.
 * <p>
 * ### Configuration parameters ###
 *
 * <pre>
 * - connection(s):
 *   - discovery_key:               (optional) a key to retrieve the connection from  {@link org.pipservices4.config.connect.IDiscovery}
 *   - host:                        host name or IP address
 *   - port:                        port number (default: 3306)
 *   - database:                    database name
 *   - uri:                         resource URI or connection string with all parameters in it
 * - credential(s):
 *   - store_key:                   (optional) a key to retrieve the credentials from {@link org.pipservices4.config.auth.ICredentialStore}
 *   - username:                    user name
 *   - password:                    user password
 *
 * ### References ###
 *
 * - *:discovery:*:*:1.0             (optional) {@link org.pipservices4.config.connect.IDiscovery} services
 * - *:credential-store:*:*:1.0      (optional) Credential stores to resolve credentials
 * <pre/>
 */
public class MySqlConnectionResolver implements IReferenceable, IConfigurable {

    /**
     * The connections resolver.
     */
    protected ConnectionResolver _connectionResolver = new ConnectionResolver();
    /**
     * The credentials resolver.
     */
    protected CredentialResolver _credentialResolver = new CredentialResolver();

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

    /**
     * Configures component by passing configuration parameters.
     *
     * @param config configuration parameters to be set.
     */
    @Override
    public void configure(ConfigParams config) {
        this._connectionResolver.configure(config);
        this._credentialResolver.configure(config);
    }

    private void validateConnection(IContext context, ConnectionParams connection) throws ConfigException {
        var uri = connection.getUri();
        if (uri != null) return;

        var host = connection.getHost();
        if (host == null) {
            throw new ConfigException(
                    context != null ? ContextResolver.getTraceId(context) : null,
                    "NO_HOST",
                    "Connection host is not set"
            );
        }

        var port = connection.getPort();
        if (port == 0) {
            throw new ConfigException(
                    context != null ? ContextResolver.getTraceId(context) : null,
                    "NO_PORT",
                    "Connection port is not set"
            );
        }

        var database = connection.getAsNullableString("database");
        if (database == null) {
            throw new ConfigException(
                    context != null ? ContextResolver.getTraceId(context) : null,
                    "NO_DATABASE",
                    "Connection database is not set"
            );
        }
    }

    private void validateConnections(IContext context, List<ConnectionParams> connections) throws ConfigException {
        if (connections == null || connections.isEmpty()) {
            throw new ConfigException(
                    context != null ? ContextResolver.getTraceId(context) : null,
                    "NO_CONNECTION",
                    "Database connection is not set"
            );
        }

        for (var connection : connections)
            this.validateConnection(context, connection);
    }

    private String composeUri(List<ConnectionParams> connections, CredentialParams credential) {
        // If there is a uri then return it immediately
        for (var connection : connections) {
            var uri = connection.getUri();
            if (uri != null) return uri;
        }

        // Define hosts
        StringBuilder hosts = new StringBuilder();
        for (var connection : connections) {
            var host = connection.getHost();
            var port = connection.getPort();

            if (!hosts.isEmpty()) {
                hosts.append(',');
            }
            hosts.append(host).append(':').append(port);
        }

        // Define database
        var database = "";
        for (var connection : connections) {
            database = !database.isEmpty() ? database : connection.getAsNullableString("database");
        }
        if (!database.isEmpty()) {
            database = '/' + database;
        }

        // Define authentication part
        var auth = "";
        if (credential != null) {
            var username = credential.getUsername();
            if (username != null) {
                var password = credential.getPassword();
                if (password != null) {
                    auth = username + ':' + password + '@';
                } else {
                    auth = username + '@';
                }
            }
        }

        // Define additional parameters parameters
        var connArr = new ConfigParams[connections.size()];
        for (var i = 0; i < connections.size(); i++)
            connArr[i] = connections.get(i);

        var options = ConfigParams.mergeConfigs(connArr).override(credential);
        options.remove("uri");
        options.remove("host");
        options.remove("port");
        options.remove("database");
        options.remove("username");
        options.remove("password");
        StringBuilder params = new StringBuilder();
        var keys = options.getKeys();
        for (var key : keys) {
            if (!params.isEmpty()) {
                params.append('&');
            }

            params.append(key);

            var value = options.getAsString(key);
            if (value != null) {
                params.append('=').append(value);
            }
        }
        if (!params.isEmpty()) {
            params.insert(0, '?');
        }

        // Compose uri
        return "mysql://" + auth + hosts + database + params;
    }

    /**
     * Resolves MySql connection URI from connection and credential parameters.
     *
     * @param context 	(optional) execution context to trace execution through call chain.
     * @return a resolved URI.
     */
    public String resolve(IContext context) throws ApplicationException {
        var connections = this._connectionResolver.resolveAll(context);
        // Validate connections
        this.validateConnections(context, connections);

        var credential = this._credentialResolver.lookup(context);
        // Credentials are not validated right now

        return this.composeUri(connections, credential);
    }
}
