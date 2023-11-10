package org.pipservices4.config.connect;

import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.commons.data.StringValueMap;
import org.pipservices4.config.auth.CredentialParams;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Contains connection parameters to connect to external services.
 * They are used together with credential parameters, but usually stored
 * separately from more protected sensitive values.
 * <p>
 * ### Configuration parameters ###
 * <ul>
 * <li>discovery_key: key to retrieve parameters from discovery service
 * <li>protocol:      connection protocol like http, https, tcp, udp
 * <li>host:          host name or IP address
 * <li>port:          port number
 * <li>uri:           resource URI or connection string with all parameters in it
 * </ul>
 * <p>
 * In addition to standard parameters ConnectionParams may contain any number of custom parameters
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * Example ConnectionParams object usage:
 *
 * ConnectionParams connection = ConnectionParams.fromTuples(
 *  "protocol", "http",
 *  "host", "10.1.1.100",
 *  "port", "8080",
 *  "cluster", "mycluster"
 * );
 *
 * String host = connection.getHost();                             // Result: "10.1.1.100"
 * int port = connection.getPort();                             // Result: 8080
 * String cluster = connection.getAsNullableString("cluster");     // Result: "mycluster"
 * }
 * </pre>
 *
 * @see ConfigParams
 * @see CredentialParams
 * @see ConnectionResolver
 * @see IDiscovery
 */
public class ConnectionParams extends ConfigParams {
    @Serial
    private static final long serialVersionUID = 5769508200513539527L;

    /**
     * Creates a new connection parameters and fills it with values.
     */
    public ConnectionParams() {
    }

    /**
     * Creates a new connection parameters and fills it with values.
     *
     * @param map (optional) an object to be converted into key-value pairs to
     *            initialize this connection.
     */
    public ConnectionParams(Map<?, ?> map) {
        super(map);
    }

    /**
     * Checks if these connection parameters shall be retrieved from
     * DiscoveryService. The connection parameters are redirected to
     * DiscoveryService when discovery_key parameter is set.
     *
     * @return true if connection shall be retrieved from DiscoveryService
     * @see #getDiscoveryKey()
     */
    public boolean useDiscovery() {
        return containsKey("discovery_key");
    }

    /**
     * Gets the key to retrieve this connection from DiscoveryService. If this key
     * is null, than all parameters are already present.
     *
     * @return the discovery key to retrieve connection.
     * @see #useDiscovery()
     */
    public String getDiscoveryKey() {
        return getAsNullableString("discovery_key");
    }

    /**
     * Sets the key to retrieve these parameters from DiscoveryService.
     *
     * @param value a new key to retrieve connection.
     */
    public void setDiscoveryKey(String value) {
        put("discovery_key", value);
    }

    /**
     * Gets the connection protocol.
     *
     * @return the connection protocol.
     */
    public String getProtocol() {
        return getAsNullableString("protocol");
    }

    /**
     * Gets the connection protocol.
     *
     * @param defaultValue (optional) the default protocol
     * @return the connection protocol or the default value if it's not set.
     */
    public String getProtocolWithDefault(String defaultValue) {
        return getAsStringWithDefault("protocol", defaultValue);
    }

    /**
     * Sets the connection protocol.
     *
     * @param value a new connection protocol.
     */
    public void setProtocol(String value) {
        put("protocol", value);
    }

    /**
     * Gets the host name or IP address.
     *
     * @return the host name or IP address.
     */
    public String getHost() {
        String host = getAsNullableString("host");
        host = host != null ? host : getAsNullableString("ip");
        return host;
    }

    /**
     * Sets the host name or IP address.
     *
     * @param value a new host name or IP address.
     */
    public void setHost(String value) {
        put("host", value);
    }

    /**
     * Gets the port number.
     *
     * @return the port number.
     */
    public int getPort() {
        return getAsIntegerWithDefault("port", 8080);
    }

    /**
     * Gets the port number with default value.
     *
     * @param defaultPort a default port number.
     * @return the port number.
     */
    public int getPortWithDefault(int defaultPort) {
        return super.getAsIntegerWithDefault("port", defaultPort);
    }

    /**
     * Sets the port number.
     *
     * @param value a new port number.
     */
    public void setPort(int value) {
        setAsObject("port", value);
    }

    /**
     * Gets the resource URI or connection string. Usually it includes all
     * connection parameters in it.
     *
     * @return the resource URI or connection string.
     */
    public String getUri() {
        return getAsNullableString("uri");
    }

    /**
     * Sets the resource URI or connection string.
     *
     * @param value a new resource URI or connection string.
     */
    public void setUri(String value) {
        put("uri", value);
    }

    /**
     * Creates a new ConnectionParams object filled with key-value pairs serialized
     * as a string.
     *
     * @param line a string with serialized key-value pairs as
     *             "key1=value1;key2=value2;..." Example:
     *             "Key1=123;Key2=ABC;Key3=2016-09-16T00:00:00.00Z"
     * @return a new ConnectionParams object.
     * @see StringValueMap#fromString(String)
     */
    public static ConnectionParams fromString(String line) {
        StringValueMap map = StringValueMap.fromString(line);
        return new ConnectionParams(map);
    }

    /**
     * Creates a new ConnectionParams object filled with provided key-value pairs
     * called tuples. Tuples parameters contain a sequence of key1, value1, key2,
     * value2, ... pairs.
     *
     * @param tuples the tuples to fill a new ConnectionParams object.
     * @return a new ConnectionParams object.
     */
    public static ConnectionParams fromTuples(Object... tuples) {
        StringValueMap map = StringValueMap.fromTuplesArray(tuples);
        return new ConnectionParams(map);
    }

    /**
     * Retrieves all ConnectionParams from configuration parameters from
     * "connections" section. If "connection" section is present instead, than it
     * returns a list with only one ConnectionParams.
     *
     * @param config          a configuration parameters to retrieve connections
     * @param configAsDefault boolean parameter for default configuration. If "true"
     *                        the default value will be added to the result.
     * @return a list of retrieved ConnectionParams
     */
    public static List<ConnectionParams> manyFromConfig(ConfigParams config, boolean configAsDefault) {
        List<ConnectionParams> result = new ArrayList<>();

        // Try to get multiple connections first
        ConfigParams connections = config.getSection("connections");

        if (!connections.isEmpty()) {
            List<String> connectionSections = connections.getSectionNames();

            for (String section : connectionSections) {
                ConfigParams connection = connections.getSection(section);
                result.add(new ConnectionParams(connection));
            }
        }
        // Then try to get a single connection
        else {
            ConfigParams connection = config.getSection("connection");
            if (!connection.isEmpty())
                result.add(new ConnectionParams(connection));
                // Apply default if possible
            else if (configAsDefault)
                result.add(new ConnectionParams(config));
        }

        return result;
    }

    /**
     * Retrieves all ConnectionParams from configuration parameters from
     * "connections" section. If "connection" section is present instead, than it
     * returns a list with only one ConnectionParams.
     *
     * @param config a configuration parameters to retrieve connections
     * @return a list of retrieved ConnectionParams
     */
    public static List<ConnectionParams> manyFromConfig(ConfigParams config) {
        return manyFromConfig(config, true);
    }

    /**
     * Retrieves a single ConnectionParams from configuration parameters from
     * "connection" section. If "connections" section is present instead, then is
     * returns only the first connection element.
     *
     * @param config          ConnectionParams, containing a section named
     *                        "connection(s)".
     * @param configAsDefault boolean parameter for default configuration. If "true"
     *                        the default value will be added to the result.
     * @return the generated ConnectionParams object.
     * @see #manyFromConfig(ConfigParams, boolean)
     */
    public static ConnectionParams fromConfig(ConfigParams config, boolean configAsDefault) {
        List<ConnectionParams> connections = manyFromConfig(config, configAsDefault);
        return !connections.isEmpty() ? connections.get(0) : null;
    }

    /**
     * Retrieves a single ConnectionParams from configuration parameters from
     * "connection" section. If "connections" section is present instead, then is
     * returns only the first connection element.
     *
     * @param config ConnectionParams, containing a section named "connection(s)".
     * @return the generated ConnectionParams object.
     * @see #manyFromConfig(ConfigParams)
     */
    public static ConnectionParams fromConfig(ConfigParams config) {
        return fromConfig(config, true);
    }

}
