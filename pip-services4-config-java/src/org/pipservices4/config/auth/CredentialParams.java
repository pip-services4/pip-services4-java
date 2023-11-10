package org.pipservices4.config.auth;

import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.commons.data.StringValueMap;
import org.pipservices4.commons.reflect.RecursiveObjectReader;
import org.pipservices4.config.connect.ConnectionParams;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Contains credentials to authenticate against external services.
 * They are used together with connection parameters, but usually stored
 * in a separate store, protected from unauthorized access.
 * <p>
 * ### Configuration parameters ###
 * <ul>
 * <li>store_key:     key to retrieve parameters from credential store
 * <li>username:      user name
 * <li>user:          alternative to username
 * <li>password:      user password
 * <li>pass:          alternative to password
 * <li>access_id:     application access id
 * <li>client_id:     alternative to access_id
 * <li>access_key:    application secret key
 * <li>client_key:    alternative to access_key
 * <li>secret_key:    alternative to access_key
 * </ul>
 * <p>
 * In addition to standard parameters CredentialParams may contain any number of custom parameters
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * CredentialParams credential = CredentialParams.fromTuples(
 *  "user", "jdoe",
 *  "pass", "pass123",
 *  "pin", "321"
 * );
 *
 * String username = credential.getUsername();             // Result: "jdoe"
 * String password = credential.getPassword();             // Result: "pass123"
 * int pin = credential.getAsNullableString("pin");     // Result: 321
 * }
 * </pre>
 *
 * @see ConfigParams
 * @see ConnectionParams
 * @see CredentialResolver
 * @see ICredentialStore
 */
public class CredentialParams extends ConfigParams {
    @Serial
    private static final long serialVersionUID = 4144579662501676747L;

    /**
     * Creates an empty instance of credential parameters.
     */
    public CredentialParams() {
    }

    /**
     * Creates a new CredentialParams and fills it with values.
     *
     * @param map (optional) an object to be converted into key-value pairs to
     *            initialize these credentials.
     */
    public CredentialParams(Map<?, ?> map) {
        super(map);
    }

    /**
     * Checks if these credential parameters shall be retrieved from
     * CredentialStore. The credential parameters are redirected to CredentialStore
     * when store_key parameter is set.
     *
     * @return true if credentials shall be retrieved from CredentialStore
     * @see #getStoreKey()
     */
    public boolean useCredentialStore() {
        return containsKey("store_key");
    }

    /**
     * Gets the key to retrieve these credentials from CredentialStore. If this key
     * is null, then all parameters are already present.
     *
     * @return the store key to retrieve credentials.
     * @see #useCredentialStore()
     */
    public String getStoreKey() {
        return getAsNullableString("store_key");
    }

    /**
     * Sets the key to retrieve these parameters from CredentialStore.
     *
     * @param value a new key to retrieve credentials.
     */
    public void setStoreKey(String value) {
        put("store_key", value);
    }

    /**
     * Gets the user name. The value can be stored in parameters "username" or
     * "user".
     *
     * @return the user name.
     */
    public String getUsername() {
        return getAsNullableString("user") != null ? getAsNullableString("user") : getAsNullableString("username");
    }

    /**
     * Sets the user name.
     *
     * @param value a new user name.
     */
    public void setUsername(String value) {
        put("username", value);
    }

    /**
     * Get the user password. The value can be stored in parameters "password" or
     * "pass".
     *
     * @return the user password.
     */
    public String getPassword() {
        return getAsNullableString("pass") != null ? getAsNullableString("pass") : getAsNullableString("password");
    }

    /**
     * Sets the user password.
     *
     * @param password a new user password.
     */
    public void setPassword(String password) {
        put("password", password);
    }

    /**
     * Gets the application access id. The value can be stored in parameters
     * "access_id" pr "client_id"
     *
     * @return the application access id.
     */
    public String getAccessId() {
        String accessId = getAsNullableString("access_id");
        accessId = accessId != null ? accessId : getAsNullableString("client_id");
        return accessId;
    }

    /**
     * Sets the application access id.
     *
     * @param value a new application access id.
     */
    public void setAccessId(String value) {
        put("access_id", value);
    }

    /**
     * Gets the application secret key. The value can be stored in parameters
     * "access_key", "client_key" or "secret_key".
     *
     * @return the application secret key.
     */
    public String getAccessKey() {
        String accessKey = getAsNullableString("access_key");
        accessKey = accessKey != null ? accessKey : getAsNullableString("client_key");
        accessKey = accessKey != null ? accessKey : getAsNullableString("secret_key");
        return accessKey;
    }

    /**
     * Sets the application secret key.
     *
     * @param value a new application secret key.
     */
    public void setAccessKey(String value) {
        put("access_key", value);
    }

    /**
     * Creates a new CredentialParams object filled with key-value pairs serialized
     * as a string.
     *
     * @param line a string with serialized key-value pairs as
     *             "key1=value1;key2=value2;..." Example:
     *             "Key1=123;Key2=ABC;Key3=2016-09-16T00:00:00.00Z"
     * @return a new CredentialParams object.
     */
    public static CredentialParams fromString(String line) {
        StringValueMap map = StringValueMap.fromString(line);
        return new CredentialParams(map);
    }

    /**
     * Retrieves all CredentialParams from configuration parameters from
     * "credentials" section. If "credential" section is present instead, than it
     * returns a list with only one CredentialParams.
     *
     * @param config          a configuration parameters to retrieve credentials
     * @param configAsDefault boolean parameter for default configuration. If "true"
     *                        the default value will be added to the result.
     * @return a list of retrieved CredentialParams
     */
    public static List<CredentialParams> manyFromConfig(ConfigParams config, boolean configAsDefault) {
        List<CredentialParams> result = new ArrayList<>();

        // Try to get multiple credentials first
        ConfigParams credentials = config.getSection("credentials");

        if (!credentials.isEmpty()) {
            List<String> sectionsNames = credentials.getSectionNames();

            for (String section : sectionsNames) {
                ConfigParams credential = credentials.getSection(section);
                result.add(new CredentialParams(credential));
            }
        }
        // Then try to get a single connection
        else {
            ConfigParams credential = config.getSection("credential");
            if (!credential.isEmpty())
                result.add(new CredentialParams(credential));
                // Apply defaults
            else if (configAsDefault)
                result.add(new CredentialParams(config));
        }

        return result;
    }

    /**
     * Retrieves all CredentialParams from configuration parameters from
     * "credentials" section. If "credential" section is present instead, than it
     * returns a list with only one CredentialParams.
     *
     * @param config a configuration parameters to retrieve credentials
     * @return a list of retrieved CredentialParams
     */
    public static List<CredentialParams> manyFromConfig(ConfigParams config) {
        return manyFromConfig(config, true);
    }

    /**
     * Retrieves a single CredentialParams from configuration parameters from
     * "credential" section. If "credentials" section is present instead, then is
     * returns only the first credential element.
     *
     * @param config          ConfigParams, containing a section named
     *                        "credential(s)".
     * @param configAsDefault boolean parameter for default configuration. If "true"
     *                        the default value will be added to the result.
     * @return the generated CredentialParams object.
     * @see #manyFromConfig(ConfigParams, boolean)
     */
    public static CredentialParams fromConfig(ConfigParams config, boolean configAsDefault) {
        List<CredentialParams> connections = manyFromConfig(config, configAsDefault);
        return !connections.isEmpty() ? connections.get(0) : null;
    }

    /**
     * Retrieves a single CredentialParams from configuration parameters from
     * "credential" section. If "credentials" section is present instead, then is
     * returns only the first credential element.
     *
     * @param config ConfigParams, containing a section named "credential(s)".
     * @return the generated CredentialParams object.
     * @see #manyFromConfig(ConfigParams, boolean)
     */
    public static CredentialParams fromConfig(ConfigParams config) {
        return fromConfig(config, true);
    }

    /**
     * Creates a new CredentialParams object filled with key-value pairs from specified
     * object.
     *
     * @param value an object with key-value pairs used to initialize a new
     *              ConfigParams.
     * @return a new CredentialParams object.
     * @see RecursiveObjectReader#getProperties(Object)
     */
    public static CredentialParams fromValue(Object value) {
        Map<String, Object> map = RecursiveObjectReader.getProperties(value);
        return new CredentialParams(map);
    }

    /**
     * Creates a new CredentialParams object filled with provided key-value pairs called
     * tuples. Tuples parameters contain a sequence of key1, value1, key2, value2,
     * ... pairs.
     *
     * @param tuples the tuples to fill a new ConfigParams object.
     * @return a new CredentialParams object.
     * @see StringValueMap#fromTuplesArray(Object[])
     */
    public static CredentialParams fromTuples(Object... tuples) {
        StringValueMap map = StringValueMap.fromTuplesArray(tuples);
        return new CredentialParams(map);
    }

    /**
     * Merges two or more CredentialParams into one. The following ConfigParams override
     * previously defined parameters.
     *
     * @param configs a list of CredentialParams objects to be merged.
     * @return a new CredentialParams object.
     * @see StringValueMap#fromMaps(Map...)
     */
    public static CredentialParams mergeConfigs(ConfigParams... configs) {
        StringValueMap map = StringValueMap.fromMaps(configs);
        return new CredentialParams(map);
    }

    /**
     * Merges two or more CredentialParams into one. The following ConfigParams override
     * previously defined parameters.
     *
     * @param configs a list of CredentialParams objects to be merged.
     * @return a new CredentialParams object.
     * @see StringValueMap#fromMaps(Map...)
     */
    public static CredentialParams mergeConfigs(List<ConfigParams> configs) {
        var unpackConn = new ConfigParams[configs.size()];

        AtomicInteger i = new AtomicInteger();
        configs.forEach(
                (c) -> {
                    unpackConn[i.get()] = ConfigParams.fromValue(c);
                    i.getAndIncrement();
                }
        );

        return mergeConfigs(unpackConn);
    }

}
