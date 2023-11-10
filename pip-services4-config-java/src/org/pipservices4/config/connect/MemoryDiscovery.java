package org.pipservices4.config.connect;

import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.components.config.IReconfigurable;
import org.pipservices4.components.context.IContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Discovery service that keeps connections in memory.
 * <p>
 * ### Configuration parameters ###
 * <ul>
 * <li>[connection key 1]:
 * <li>...                          connection parameters for key 1
 * <li>[connection key 2]:
 * <li>...                          connection parameters for key N
 * </ul>
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * ConfigParams config = ConfigParams.fromTuples(
 *      "key1.host", "10.1.1.100",
 *      "key1.port", "8080",
 *      "key2.host", "10.1.1.100",
 *      "key2.port", "8082"
 * );
 *
 * MemoryDiscovery discovery = new MemoryDiscovery();
 * discovery.readConnections(config);
 *
 * discovery.resolve("123", "key1");
 * }
 * </pre>
 *
 * @see IDiscovery
 * @see ConnectionParams
 */
public class MemoryDiscovery implements IDiscovery, IReconfigurable {
    private final List<DiscoveryItem> _items = new ArrayList<>();
    private final Object _lock = new Object();

    /**
     * Creates a new instance of discovery service.
     */
    public MemoryDiscovery() {
    }

    /**
     * Creates a new instance of discovery service.
     *
     * @param config (optional) configuration with connection parameters.
     */
    public MemoryDiscovery(ConfigParams config) {
        if (config != null)
            configure(config);
    }

    private static class DiscoveryItem {
        public String key;
        public ConnectionParams connection;
    }

    /**
     * Configures component by passing configuration parameters.
     *
     * @param config configuration parameters to be set.
     */
    public void configure(ConfigParams config) {
        readConnections(config);
    }

    /**
     * Reads connections from configuration parameters. Each section represents an
     * individual Connection params
     *
     * @param config configuration parameters to be read
     */
    public void readConnections(ConfigParams config) {
        synchronized (_lock) {
            _items.clear();
            ConfigParams connections = config.getSection("connections");

            if (!connections.isEmpty()) {
                List<String> connectionSections = connections.getSectionNames();
                for (String key : connectionSections) {
                    ConfigParams value = connections.getSection(key);

                    DiscoveryItem item = new DiscoveryItem();
                    item.key = key;
                    item.connection = new ConnectionParams(value);
                    _items.add(item);
                }
            }
        }
    }

    /**
     * Registers connection parameters into the discovery service.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param key           a key to uniquely identify the connection parameters.
     * @param connection    a connection to be registered.
     */
    public void register(IContext context, String key, ConnectionParams connection) {
        synchronized (_lock) {
            DiscoveryItem item = new DiscoveryItem();
            item.key = key;
            item.connection = connection;
            _items.add(item);
        }
    }

    /**
     * Resolves a single connection parameters by its key.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param key           a key to uniquely identify the connection.
     * @return receives found connection.
     */
    public ConnectionParams resolveOne(IContext context, String key) {
        ConnectionParams connection = null;

        synchronized (_lock) {
            for (DiscoveryItem item : _items) {
                if (Objects.equals(item.key, key) && item.connection != null) {
                    connection = item.connection;
                    break;
                }
            }
        }

        return connection;
    }

    /**
     * Resolves all connection parameters by their key.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param key           a key to uniquely identify the connections.
     * @return receives found connections.
     */
    public List<ConnectionParams> resolveAll(IContext context, String key) {
        List<ConnectionParams> connections = new ArrayList<>();

        synchronized (_lock) {
            for (DiscoveryItem item : _items) {
                if (Objects.equals(item.key, key) && item.connection != null)
                    connections.add(item.connection);
            }
        }

        return connections;
    }
}
