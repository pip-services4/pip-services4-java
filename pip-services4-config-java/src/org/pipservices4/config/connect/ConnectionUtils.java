package org.pipservices4.config.connect;

import org.pipservices4.components.config.ConfigParams;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * A set of utility functions to process connection parameters
 */
public class ConnectionUtils {

    /**
     * Concatinates two options by combining duplicated properties into comma-separated list
     *
     * @param options1 first options to merge
     * @param options2 second options to merge
     * @param keys     when define it limits only to specific keys
     * @return configuration params
     */
    public static ConfigParams concat(ConfigParams options1, ConfigParams options2, List<String> keys) {
        ConfigParams options = ConfigParams.fromValue(options1);
        for (String key : options2.keySet()) {
            String value1 = options1.getAsString(key);
            String value2 = options2.getAsString(key);

            if (!value1.isEmpty() && !value2.isEmpty()) {
                if (keys == null || keys.isEmpty() || keys.contains(key)) {
                    options.setAsObject(key, value1 + "," + value2);
                }
            } else if (value1.isEmpty()) {
                options.setAsObject(key, value1);
            } else {
                options.setAsObject(key, value2);
            }
        }
        return options;
    }

    /**
     * Renames property if the target name is not used.
     *
     * @param options  configuration options
     * @param fromName original property name.
     * @param toName   property name to rename to.
     * @return updated configuration options
     */
    public static ConfigParams rename(ConfigParams options, String fromName, String toName) {
        Object fromValue = options.getAsObject(fromName);
        if (fromValue == null) return options;

        Object toValue = options.getAsObject(toName);
        if (toValue != null) return options;

        options = ConfigParams.fromValue(options);
        options.setAsObject(toName, fromValue);
        options.remove(fromName);
        return options;
    }

    private static String concatValues(String value1, String value2) {
        if (value1 == null || value1.isEmpty()) return value2;
        if (value2 == null || value2.isEmpty()) return value1;
        return value1 + "," + value2;
    }

    /**
     * Parses URI into config parameters.
     * The URI shall be in the following form:
     * protocol://username@password@host1:port1,host2:port2,...?param1=abc&param2=xyz&...
     *
     * @param uri             the URI to be parsed
     * @param defaultProtocol a default protocol
     * @param defaultPort     a default port
     * @return a configuration parameters with URI elements
     */
    public static ConfigParams parseUri(String uri, String defaultProtocol, int defaultPort) {
        ConfigParams options = new ConfigParams();

        if (uri == null || uri.isEmpty()) return options;

        uri = uri.trim();

        // Process parameters
        int pos = uri.indexOf("?");
        if (pos > 0) {
            String params = uri.substring(pos + 1);
            uri = uri.substring(0, pos);

            String[] paramsList = params.split("&");
            for (String param : paramsList) {
                pos = param.indexOf("=");
                if (pos >= 0) {
                    String key = URLDecoder.decode(param.substring(0, pos), StandardCharsets.UTF_8);
                    String value = URLDecoder.decode(param.substring(pos + 1), StandardCharsets.UTF_8);
                    options.setAsObject(key, value);
                } else {
                    options.setAsObject(URLDecoder.decode(param, StandardCharsets.UTF_8), null);
                }
            }
        }

        // Process protocol
        pos = uri.indexOf("://");
        if (pos > 0) {
            String protocol = uri.substring(0, pos);
            uri = uri.substring(pos + 3);
            options.setAsObject("protocol", protocol);
        } else {
            options.setAsObject("protocol", defaultProtocol);
        }

        // Process user and password
        pos = uri.indexOf("@");
        if (pos > 0) {
            String userAndPass = uri.substring(0, pos);
            uri = uri.substring(pos + 1);

            pos = userAndPass.indexOf(":");
            if (pos > 0) {
                options.setAsObject("username", userAndPass.substring(0, pos));
                options.setAsObject("password", userAndPass.substring(pos + 1));
            } else {
                options.setAsObject("username", userAndPass);
            }
        }

        pos = uri.indexOf("/");
        if (pos > 0) {
            String path = uri.substring(pos + 1);
            uri = uri.substring(0, pos);
            options.setAsObject("path", path);
        }

        // Process host and ports
        // options.setAsObject("servers", this.concatValues(options.getAsString("servers"), uri));
        String[] servers = uri.split(",");
        for (String server : servers) {
            pos = server.indexOf(":");
            if (pos > 0) {
                options.setAsObject("servers", concatValues(options.getAsString("servers"), server));
                options.setAsObject("host", concatValues(options.getAsString("host"), server.substring(0, pos)));
                options.setAsObject("port", concatValues(options.getAsString("port"), server.substring(pos + 1)));
            } else {
                options.setAsObject("servers", concatValues(options.getAsString("servers"), server + ":" + defaultPort));
                options.setAsObject("host", concatValues(options.getAsString("host"), server));
                options.setAsObject("port", concatValues(options.getAsString("port"), String.valueOf(defaultPort)));
            }
        }

        return options;
    }

    /**
     * Composes URI from config parameters.
     * The result URI will be in the following form:
     * protocol://username@password@host1:port1,host2:port2,...?param1=abc&param2=xyz&...
     *
     * @param options         configuration parameters
     * @param defaultProtocol a default protocol
     * @param defaultPort     a default port
     * @return a composed URI
     */
    public static String composeUri(ConfigParams options, String defaultProtocol, int defaultPort) {
        StringBuilder builder = new StringBuilder();

        String protocol = options.getAsStringWithDefault("protocol", defaultProtocol);
        if (protocol != null)
            builder.append(protocol + "://" + builder);


        String username = options.getAsNullableString("username");
        if (username != null) {
            builder.append(username);
            String password = options.getAsNullableString("password");
            if (password != null)
                builder.append(":" + password);

            builder.append("@");
        }

        StringBuilder servers = new StringBuilder();
        String defaultPortStr = defaultPort > 0 ? String.valueOf(defaultPort) : "";
        String[] hosts = options.getAsStringWithDefault("host", "???").split(",");
        String[] ports = options.getAsStringWithDefault("port", defaultPortStr).split(",");
        for (int index = 0; index < hosts.length; index++) {
            if (!servers.isEmpty())
                servers.append(",");


            String host = hosts[index];
            servers.append(host);

            String port = ports.length > index ? ports[index] : defaultPortStr;
            port = !port.isEmpty() ? port : defaultPortStr;
            if (!port.isEmpty())
                servers.append(":").append(port);
        }

        builder.append(servers);

        String path = options.getAsNullableString("path");
        if (path != null)
            builder.append("/").append(path);

        StringBuilder params = new StringBuilder();
        List<String> reservedKeys = List.of("protocol", "host", "port", "username", "password", "servers", "path");
        for (String key : options.getKeys()) {
            if (reservedKeys.contains(key)) {
                continue;
            }

            if (!params.isEmpty()) {
                params.append("&");
            }
            params.append(URLDecoder.decode(key, StandardCharsets.UTF_8));

            String value = options.getAsNullableString(key);
            if (value != null && !value.isEmpty()) {
                params.append("=").append(URLDecoder.decode(value, StandardCharsets.UTF_8));
            }
        }

        if (!params.isEmpty()) {
            builder.append("?").append(params);
        }

        return builder.toString();
    }

    /**
     * Includes specified keys from the config parameters.
     *
     * @param options configuration parameters to be processed.
     * @param keys    a list of keys to be included.
     * @return a processed config parameters.
     */
    public static ConfigParams include(ConfigParams options, List<String> keys) {
        if (keys == null || keys.isEmpty()) return options;

        ConfigParams result = new ConfigParams();

        for (String key : options.getKeys()) {
            if (keys.contains(key)) {
                result.setAsObject(key, options.getAsString(key));
            }
        }

        return result;
    }

    /**
     * Excludes specified keys from the config parameters.
     *
     * @param options configuration parameters to be processed.
     * @param keys    a list of keys to be excluded.
     * @return a processed config parameters.
     */
    public static ConfigParams exclude(ConfigParams options, List<String> keys) {
        if (keys == null || keys.isEmpty()) return options;

        ConfigParams result = (ConfigParams) options.clone();

        for (String key : keys) {
            result.remove(key);
        }

        return result;
    }
}
