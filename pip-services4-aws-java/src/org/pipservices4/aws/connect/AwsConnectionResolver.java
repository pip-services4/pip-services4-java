package org.pipservices4.aws.connect;

import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.components.config.IConfigurable;
import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.components.context.IContext;
import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.refer.IReferenceable;
import org.pipservices4.components.refer.IReferences;
import org.pipservices4.components.refer.ReferenceException;
import org.pipservices4.config.auth.CredentialParams;
import org.pipservices4.config.auth.CredentialResolver;
import org.pipservices4.config.connect.IDiscovery;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to retrieve AWS connection and credential parameters,
 * validate them and compose a {@link AwsConnectionParams} value
 * <p>
 * ### Configuration parameters ###
 * <ul>
 * <li>connections:
 *   <ul>
 *   <li>discovery_key:               (optional) a key to retrieve the connection from {@link IDiscovery}
 *   <li>region:                      (optional) AWS region
 *   <li>partition:                   (optional) AWS partition
 *   <li>service:                     (optional) AWS service
 *   <li>resource_type:               (optional) AWS resource type
 *   <li>resource:                    (optional) AWS resource id
 *   <li>arn:                         (optional) AWS resource ARN
 *   </ul>
 * <li>credentials:
 *   <ul>
 *   <li>store_key:                   (optional) a key to retrieve the credentials from <a href="https://pip-services4-java.github.io/pip-services4-config-java/org/pipservices4/config/auth/ICredentialStore.html">ICredentialStore<a/>
 *   <li>access_id:                   AWS access/client id
 *   <li>access_key:                  AWS access/client id
 *   </ul>
 * </ul>
 * <p>
 * ### References ###
 * <ul>
 * <li>*:discovery:*:*:1.0            (optional) {@link IDiscovery} services to resolve connections
 * <li>*:credential-store:*:*:1.0            (optional) Credential stores to resolve credentials
 * </ul>
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 *     ConfigParams config = ConfigParams.fromTuples(
 *         "connection.region", "us-east1",
 *         "connection.service", "s3",
 *         "connection.bucket", "mybucket",
 *         "credential.access_id", "XXXXXXXXXX",
 *         "credential.access_key", "XXXXXXXXXX"
 *     );
 *
 *     var connectionResolver = new AwsConnectionResolver();
 *     connectionResolver.configure(config);
 *     connectionResolver.setReferences(references);
 *
 *     var connectionParams = await connectionResolver.resolve("123");
 * }
 * </pre>
 *
 * @see AwsConnectionParams
 * @see IDiscovery
 */
public class AwsConnectionResolver implements IConfigurable, IReferenceable {
    /**
     * The connection resolver.
     */
    protected AwsConnectionResolver _connectionResolver = new AwsConnectionResolver();
    /**
     * The credential resolver.
     */
    protected CredentialResolver _credentialResolver = new CredentialResolver();

    /**
     * Creates a new instance of connection resolver.
     */
    public AwsConnectionResolver() {
    }

    /**
     * Configures component by passing configuration parameters.
     *
     * @param config configuration parameters to be set.
     */
    public void configure(ConfigParams config) {
        _connectionResolver.configure(config);
        _credentialResolver.configure(config);
    }

    /**
     * Sets references to dependent components.
     *
     * @param references references to locate the component dependencies.
     */
    public void setReferences(IReferences references) {
        this._connectionResolver.setReferences(references);
        this._credentialResolver.setReferences(references);
    }

    /**
     * Resolves connection and credential parameters and generates a single
     * AWSConnectionParams value.
     *
     * @param context             (optional) transaction id to trace execution through call chain.
     *
     * @return {@link AwsConnectionParams} 	callback function that receives AWSConnectionParams value or error.
     * @throws ApplicationException when error occurred.
     * @see IDiscovery
     */
    public AwsConnectionParams resolve(IContext context) throws ApplicationException {
        var connection = new AwsConnectionParams();

        AwsConnectionParams connectionParams = _connectionResolver.resolve(context);
        connection.append(connectionParams);

        CredentialParams credentialParams = _credentialResolver.lookup(context);
        connection.append(credentialParams);

        // Force ARN parsing
        connection.setArn(connection.getArn());

        // Perform validation
        connection.validate(context);

        return connection;
    }
}
