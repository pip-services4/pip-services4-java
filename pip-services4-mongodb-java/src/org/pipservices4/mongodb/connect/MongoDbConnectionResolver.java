package org.pipservices4.mongodb.connect;

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
import java.util.Set;

/**
 * Helper class that resolves MongoDB connection and credential parameters,
 * validates them and generates a connection URI.
 * <p>
 * It is able to process multiple connections to MongoDB cluster nodes.
 * <p>
 * ### Configuration parameters ###
 * <ul>
 * <li>connection(s):
 *   <ul>
 *   <li>discovery_key:               (optional) a key to retrieve the connection from <a href="https://pip-services4-java.github.io/pip-services4-config-java/org/pipservices4/config/connect/IDiscovery.html">IDiscovery</a>
 *   <li>host:                        host name or IP address
 *   <li>port:                        port number (default: 27017)
 *   <li>database:                    database name
 *   <li>uri:                         resource URI or connection string with all parameters in it
 *   </ul>
 * <li>credential(s):
 *   <ul>
 *   <li>store_key:                   (optional) a key to retrieve the credentials from <a href="https://pip-services4-java.github.io/pip-services4-config-java/org/pipservices4/config/auth/ICredentialStore.html">ICredentialStore</a>
 *   <li>username:                    user name
 *   <li>password:                    user password
 *   </ul>
 * </ul>
 * <p>
 * ### References ###
 * <ul>
 * <li>*:discovery:*:*:1.0          (optional) <a href="https://pip-services4-java.github.io/pip-services4-config-java/org/pipservices4/config/connect/IDiscovery.html">IDiscovery</a> services
 * <li>*:credential-store:*:*:1.0   (optional) Credential stores to resolve credentials
 * </ul>
 */
public class MongoDbConnectionResolver implements IReferenceable, IConfigurable {

    /**
     * The connections resolver.
     */
    protected ConnectionResolver _connectionResolver = new ConnectionResolver();
    /**
     * The credentials resolver.
     */
    protected CredentialResolver _credentialResolver = new CredentialResolver();

    /**
     * Configures component by passing configuration parameters.
     *
     * @param config configuration parameters to be set.
     */
    public void configure(ConfigParams config) {
        _connectionResolver.configure(config, false);
        _credentialResolver.configure(config, false);
    }

    /**
     * Sets references to dependent components.
     *
     * @param references references to locate the component dependencies.
     */
    public void setReferences(IReferences references) {
        _connectionResolver.setReferences(references);
        _credentialResolver.setReferences(references);
    }

    private void validateConnection(IContext context, ConnectionParams connection) throws ConfigException {
        String uri = connection.getUri();
        if (uri != null)
            return;

        String host = connection.getHost();
        if (host == null)
            throw new ConfigException(ContextResolver.getTraceId(context), "NO_HOST", "Connection host is not set");

        int port = connection.getPort();
        if (port == 0)
            throw new ConfigException(ContextResolver.getTraceId(context), "NO_PORT", "Connection port is not set");

        String database = connection.getAsNullableString("database");
        if (database == null)
            throw new ConfigException(ContextResolver.getTraceId(context), "NO_DATABASE", "Connection database is not set");
    }

    private void validateConnections(IContext context, List<ConnectionParams> connections) throws ConfigException {
        if (connections == null || connections.isEmpty())
            throw new ConfigException(ContextResolver.getTraceId(context), "NO_CONNECTION", "Database connection is not set");

        for (ConnectionParams connection : connections)
            validateConnection(context, connection);
    }

    private String composeUri(List<ConnectionParams> connections, CredentialParams credential) {
        // If there is a uri then return it immediately
        for (ConnectionParams connection : connections) {
            String fullUri = connection.getAsNullableString("uri");// connection.Uri;
            if (fullUri != null)
                return fullUri;
        }

        // Define hosts
        StringBuilder hosts = new StringBuilder();
        for (ConnectionParams connection : connections) {
            String host = connection.getHost();
            int port = connection.getPort();

            if (!hosts.isEmpty())
                hosts.append(",");
            hosts.append(host).append(port == 0 ? "" : ":" + port);
        }

        // Define database
        String database = "";
        for (ConnectionParams connection : connections) {
            database = connection.getAsNullableString("database") != null ? connection.getAsNullableString("database")
                    : database;
        }

        if (!database.isEmpty())
            database = "/" + database;

        // Define authentication part
        String auth = "";
        if (credential != null) {
            String username = credential.getUsername();
            if (username != null) {
                String password = credential.getPassword();
                if (password != null)
                    auth = username + ":" + password + "@";
                else
                    auth = username + "@";
            }
        }

        // Define additional parameters parameters
        ConfigParams options = new ConfigParams();
        for (ConnectionParams connection : connections)
            options = options.override(connection);
        if (credential != null)
            options = options.override(credential);

        options.remove("uri");
        options.remove("host");
        options.remove("port");
        options.remove("database");
        options.remove("username");
        options.remove("password");

        StringBuilder parameters = new StringBuilder();
        Set<String> keys = options.keySet();
        for (String key : keys) {
            if (!parameters.isEmpty())
                parameters.append("&");

            parameters.append(key);

            String value = options.getAsString(key);
            if (value != null)
                parameters.append("=").append(value);
        }

        if (!parameters.isEmpty())
            parameters.insert(0, "?");

        // Compose uri
        return "mongodb://" + auth + hosts + database + parameters;
    }

    /**
     * Resolves MongoDB connection URI from connection and credential parameters.
     *
     * @param context 	(optional) execution context to trace execution through call chain.
     * @return resolved URI.
     * @throws ApplicationException when error occured.
     */
    public String resolve(IContext context) throws ApplicationException {
        List<ConnectionParams> connections = _connectionResolver.resolveAll(context);
        validateConnections(context, connections);

        CredentialParams credential = _credentialResolver.lookup(context);

        return composeUri(connections, credential);
    }

}
