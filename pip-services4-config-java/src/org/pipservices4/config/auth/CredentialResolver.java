package org.pipservices4.config.auth;

import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.components.config.IConfigurable;
import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.components.context.IContext;
import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.refer.IReferenceable;
import org.pipservices4.components.refer.IReferences;
import org.pipservices4.components.refer.ReferenceException;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to retrieve component credentials.
 * <p>
 * If credentials are configured to be retrieved from ICredentialStore,
 * it automatically locates {@link ICredentialStore} in component references
 * and retrieve credentials from there using <code>store_key</code> parameter.
 * <p>
 * ### Configuration parameters ###
 * <ul>
 * <li>credential:
 *   <ul>
 *   <li>store_key:                   (optional) a key to retrieve the credentials from {@link ICredentialStore}
 *   <li>...                          other credential parameters
 *   </ul>
 * <li>credentials:                   alternative to credential
 *   <ul>
 *   <li>[credential params 1]:       first credential parameters
 *   <li> ...
 *   <li>[credential params N]:       Nth credential parameters
 *   <li> ...
 *   </ul>
 * </ul>
 * <p>
 * ### References ###
 * <ul>
 * <li>*:credential-store:*:*:1.0     (optional) Credential stores to resolve credentials
 * </ul>
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * ConfigParams config = ConfigParams.fromTuples(
 *      "credential.user", "jdoe",
 *      "credential.pass",  "pass123"
 * );
 *
 * CredentialResolver credentialResolver = new CredentialResolver();
 * credentialResolver.configure(config);
 * credentialResolver.setReferences(references);
 *
 * credentialResolver.lookup(ContextResolver.fromTraceId("123"));
 * }
 * </pre>
 *
 * @see CredentialParams
 * @see ICredentialStore
 */
public class CredentialResolver implements IConfigurable, IReferenceable {
    private final List<CredentialParams> _credentials = new ArrayList<>();
    private IReferences _references = null;

    /**
     * Creates a new instance of credentials resolver.
     */
    public CredentialResolver() {
    }

    /**
     * Creates a new instance of credentials resolver.
     *
     * @param config (optional) component configuration parameter.
     */
    public CredentialResolver(ConfigParams config) {
        configure(config);
    }

    /**
     * Creates a new instance of credentials resolver.
     *
     * @param config     (optional) component configuration parameters
     * @param references (optional) component references
     */
    public CredentialResolver(ConfigParams config, IReferences references) {
        if (config != null)
            this.configure(config);

        if (references != null)
            this.setReferences(references);
    }

    /**
     * Configures component by passing configuration parameters.
     *
     * @param config          configuration parameters to be set.
     * @param configAsDefault boolean parameter for default configuration. If "true"
     *                        the default value will be added to the result.
     */
    public void configure(ConfigParams config, boolean configAsDefault) {
        _credentials.addAll(CredentialParams.manyFromConfig(config, configAsDefault));
    }

    /**
     * Configures component by passing configuration parameters.
     *
     * @param config configuration parameters to be set.
     */
    public void configure(ConfigParams config) {
        configure(config, false);
    }

    /**
     * Sets references to dependent components.
     *
     * @param references references to locate the component dependencies.
     */
    public void setReferences(IReferences references) {
        _references = references;
    }

    /**
     * Gets all credentials configured in component configuration.
     * <p>
     * Redirect to CredentialStores is not done at this point. If you need fully
     * fleshed credential use lookup() method instead.
     *
     * @return a list with credential parameters
     */
    public List<CredentialParams> getAll() {
        return _credentials;
    }

    /**
     * Adds a new credential to component credentials
     *
     * @param connection new credential parameters to be added
     */
    public void add(CredentialParams connection) {
        _credentials.add(connection);
    }

    private CredentialParams lookupInStores(IContext context, CredentialParams credential)
            throws ApplicationException {

        if (!credential.useCredentialStore())
            return null;

        String key = credential.getStoreKey();
        if (_references == null)
            return null;

        List<Object> components = _references.getOptional(new Descriptor("*", "credential_store", "*", "*", "*"));
        if (components.isEmpty())
            throw new ReferenceException(context, "Credential store wasn't found to make lookup");

        for (Object component : components) {
            if (component instanceof ICredentialStore) {
                CredentialParams resolvedCredential = ((ICredentialStore) component).lookup(context, key);
                if (resolvedCredential != null)
                    return resolvedCredential;
            }
        }

        return null;
    }

    /**
     * Looks up component credential parameters. If credentials are configured to be
     * retrieved from Credential store it finds a ICredentialStore and lookups
     * credentials there.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @return resolved credential parameters or null if nothing was found.
     * @throws ApplicationException when errors occured.
     */
    public CredentialParams lookup(IContext context) throws ApplicationException {
        if (_credentials.isEmpty())
            return null;

        // Return connection that doesn't require discovery
        for (CredentialParams credential : _credentials) {
            if (!credential.useCredentialStore())
                return credential;
        }

        // Return connection that require discovery
        for (CredentialParams credential : _credentials) {
            if (credential.useCredentialStore()) {
                CredentialParams resolvedConnection = lookupInStores(context, credential);
                if (resolvedConnection != null)
                    return resolvedConnection;
            }
        }

        return null;
    }

}
