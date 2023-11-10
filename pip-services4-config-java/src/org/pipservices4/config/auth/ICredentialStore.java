package org.pipservices4.config.auth;

import org.pipservices4.config.connect.ConnectionParams;
import org.pipservices4.components.context.IContext;

/**
 * Interface for credential stores which are used to store and lookup credentials
 * to authenticate against external services.
 *
 * @see CredentialParams
 * @see ConnectionParams
 */
public interface ICredentialStore {
    /**
     * Stores credential parameters into the store.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param key           a key to uniquely identify the credential.
     * @param credential    a credential to be stored.
     */
    void store(IContext context, String key, CredentialParams credential);

    /**
     * Lookups credential parameters by its key.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param key           a key to uniquely identify the credential.
     * @return found credential parameters or null if nothing was found
     */
    CredentialParams lookup(IContext context, String key);
}
