package org.pipservices4.config.auth;

import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.components.config.IReconfigurable;
import org.pipservices4.commons.data.AnyValueMap;
import org.pipservices4.components.context.IContext;

import java.util.List;

/**
 * Credential store that keeps credentials in memory.
 * <p>
 * ### Configuration parameters ###
 * <ul>
 * <li>[credential key 1]:
 * <li>...                          credential parameters for key 1
 * <li>[credential key 2]:
 * <li>...                          credential parameters for key N
 * </ul>
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * ConfigParams config = ConfigParams.fromTuples(
 *      "key1.user", "jdoe",
 *      "key1.pass", "pass123",
 *      "key2.user", "bsmith",
 *      "key2.pass", "mypass"
 * );
 *
 * MemoryCredentialStore credentialStore = new MemoryCredentialStore();
 * credentialStore.readCredentials(config);
 *
 * credentialStore.lookup("123", "key1");
 * }
 * </pre>
 *
 * @see ICredentialStore
 * @see CredentialParams
 */
public class MemoryCredentialStore implements ICredentialStore, IReconfigurable {

    private final AnyValueMap _items = new AnyValueMap();
    private final Object _lock = new Object();

    /**
     * Creates a new instance of the credential store.
     */
    public MemoryCredentialStore() {
    }

    /**
     * Creates a new instance of the credential store.
     *
     * @param credentials (optional) configuration with credential parameters.
     */
    public MemoryCredentialStore(ConfigParams credentials) {
        configure(credentials);
    }

    /**
     * Configures component by passing configuration parameters.
     *
     * @param config configuration parameters to be set.
     */
    public void configure(ConfigParams config) {
        readCredentials(config);
    }

    /**
     * Reads config from configuration parameters.
     * Each section represents an individual CredentialParams
     *
     * @param config configuration parameters to be read
     */
    public void readCredentials(ConfigParams config) {
        synchronized (_lock) {
            _items.clear();
            List<String> sections = config.getSectionNames();

            for (String section : sections) {
                ConfigParams value = config.getSection(section);
                this._items.append(CredentialParams.fromTuples(section, value));
            }
        }
    }

    /**
     * Stores credential parameters into the store.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param key           a key to uniquely identify the credential parameters.
     * @param credential    a credential parameters to be stored.
     */
    public void store(IContext context, String key, CredentialParams credential) {
        synchronized (_lock) {
            if (credential != null)
                _items.put(key, credential);
            else
                _items.remove(key);
        }
    }

    /**
     * Lookups credential parameters by its key.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param key           a key to uniquely identify the credential parameters.
     * @return resolved credential parameters or null if nothing was found.
     */
    public CredentialParams lookup(IContext context, String key) {
        synchronized (_lock) {
            if (_items.get(key) instanceof String)
                return CredentialParams.fromString((String) _items.get(key));
            return (CredentialParams) _items.get(key);
        }
    }

}
