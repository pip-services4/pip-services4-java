package org.pipservices4.aws.connect;

import org.pipservices4.commons.errors.ConfigException;
import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.commons.data.StringValueMap;
import org.pipservices4.components.context.ContextResolver;
import org.pipservices4.components.context.IContext;
import org.pipservices4.config.auth.CredentialParams;
import org.pipservices4.config.connect.ConnectionParams;

import java.util.Map;

/**
 * Contains connection parameters to authenticate against Amazon Web Services (AWS)
 * and connect to specific AWS resource.
 *
 * The class is able to compose and parse AWS resource ARNs.
 * <p>
 * ### Configuration parameters ###
 * <ul>
 * <li>access_id:     application access id
 * <li>client_id:     alternative to access_id
 * <li>access_key:    application secret key
 * <li>client_key:    alternative to access_key
 * <li>secret_key:    alternative to access_key
 * </ul>
 * <p>
 * In addition to standard parameters <a href="https://pip-services4-java.github.io/pip-services4-config-java/org/pipservices4/config/auth/CredentialParams.html">CredentialParams<a/> may contain any number of custom parameters
 * @see AwsConnectionResolver
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * Example ConnectionParams object usage:
 *
 *    var connection = AwsConnectionParams.fromTuples(
 *         "region", "us-east-1",
 *         "access_id", "XXXXXXXXXXXXXXX",
 *         "secret_key", "XXXXXXXXXXXXXXX",
 *         "service", "s3",
 *         "bucket", "mybucket"
 *     );
 *
 *     var region = connection.getRegion();                     // Result: "us-east-1"
 *     var accessId = connection.getAccessId();                 // Result: "XXXXXXXXXXXXXXX"
 *     var secretKey = connection.getAccessKey();               // Result: "XXXXXXXXXXXXXXX"
 *     var pin = connection.getAsNullableString("bucket");      // Result: "mybucket"
 * }
 * </pre>
 *
 * @see ConfigParams
 * @see CredentialParams
 */
public class AwsConnectionParams extends ConfigParams {

    /**
     * Creates a new connection parameters and fills it with values.
     */
    public AwsConnectionParams() {
    }

    /**
     * Creates a new connection parameters and fills it with values.
     *
     * @param map (optional) an object to be converted into key-value pairs to
     *            initialize this connection.
     */
    public AwsConnectionParams(Map<?, ?> map) {
        super(map);
    }

    /**
     * Gets the AWS partition name.
     *
     * @return {string} the AWS partition name.
     */
    public String getPartition() {
        return getAsStringWithDefault("partition", "aws");
    }

    /**
     * Sets the AWS partition name.
     *
     * @param value a new AWS partition name.
     */
    public void setPartition(String value) {
        put("partition", value);
    }

    /**
     * Gets the AWS service name.
     *
     * @return {string} the AWS service name.
     */
    public String getService() {
        return getAsStringWithDefault("service", getAsNullableString("protocol"));
    }

    /**
     * Sets the AWS service name.
     *
     * @param value a new AWS service name.
     */
    public void setService(String value) {
        put("service", value);
    }

    /**
     * Gets the AWS region.
     *
     * @return {string} the AWS region.
     */
    public String getRegion() {
        return getAsNullableString("region");
    }

    /**
     * Sets the AWS region.
     *
     * @param value a new AWS region.
     */
    public void setRegion(String value) {
        put("region", value);
    }

    /**
     * Gets the AWS account id.
     *
     * @return {string} the AWS account id.
     */
    public String getAccount() {
        return getAsNullableString("account");
    }

    /**
     * Sets the AWS account id.
     *
     * @param value the AWS account id.
     */
    public void setAccount(String value) {
        put("account", value);
    }

    /**
     * Gets the AWS resource type.
     *
     * @return {string} the AWS resource type.
     */
    public String getResourceType() {
        return getAsNullableString("resource_type");
    }

    /**
     * Sets the AWS resource type.
     *
     * @param value a new AWS resource type.
     */
    public void setResourceType(String value) {
        put("resource_type", value);
    }

    /**
     * Gets the AWS resource id.
     *
     * @return {string} the AWS resource id.
     */
    public String getResource() {
        return getAsNullableString("resource");
    }

    /**
     * Sets the AWS resource id.
     *
     * @param value a new AWS resource id.
     */
    public void setResource(String value) {
        put("resource", value);
    }

    /**
     * Gets the AWS resource ARN.
     * If the ARN is not defined it automatically generates it from other properties.
     *
     * @return {string} the AWS resource ARN.
     */
    public String getArn() {
        String arn = super.getAsNullableString("arn");
        if (arn != null) return arn;

        arn = "arn";
        final String partition = this.getPartition();
        arn += ":" + partition;
        final String service = this.getService() != null ? this.getService() : "";
        arn += ":" + service;
        final String region = this.getRegion() != null ? this.getRegion() : "";
        arn += ":" + region;
        final String account = this.getAccount() != null ? this.getAccount() : "";
        arn += ":" + account;
        final String resourceType = this.getResourceType() != null ? this.getResourceType() : "";
        if (!resourceType.isEmpty())
            arn += ":" + resourceType;
        final String resource = this.getResource() != null ? this.getResource() : "";
        arn += ":" + resource;

        return arn;
    }

    /**
     * Sets the AWS resource ARN.
     * When it sets the value, it automatically parses the ARN
     * and sets individual parameters.
     *
     * @param value a new AWS resource ARN.
     */
    public void setArn(String value) {
        put("arn", value);

        if (value != null) {
            var tokens = value.split(":");
            this.setPartition(tokens[1]);
            this.setService(tokens[2]);
            this.setRegion(tokens[3]);
            this.setAccount(tokens[4]);
            if (tokens.length > 6) {
                this.setResourceType(tokens[5]);
                this.setResource(tokens[6]);
            } else {
                var temp = tokens[5];
                var pos = temp.indexOf("/");
                if (pos > 0) {
                    this.setResourceType(temp.substring(0, pos));
                    this.setResource(temp.substring(pos + 1));
                } else {
                    this.setResourceType(null);
                    this.setResource(temp);
                }
            }
        }
    }

    /**
     * Gets the AWS access id.
     *
     * @return {string} the AWS access id.
     */
    public String getAccessId() {
        return getAsStringWithDefault("access_id", getAsNullableString("client_id"));
    }

    /**
     * Sets the AWS access id.
     *
     * @param value the AWS access id.
     */
    public void setAccessId(String value) {
        put("access_id", value);
    }

    /**
     * Gets the AWS client key.
     *
     * @return {string} the AWS client key.
     */
    public String getAccessKey() {
        return getAsStringWithDefault("access_key", getAsNullableString("client_key"));
    }

    /**
     * Sets the AWS client key.
     *
     * @param value a new AWS client key.
     */
    public void setAccessKey(String value) {
        put("access_key", value);
    }

    /**
     * Creates a new AwsConnectionParams object filled with key-value pairs serialized
     * as a string.
     *
     * @param line a string with serialized key-value pairs as
     *             "key1=value1;key2=value2;..." Example:
     *             "Key1=123;Key2=ABC;Key3=2016-09-16T00:00:00.00Z"
     * @return a new AwsConnectionParams object.
     * @see StringValueMap#fromString(String)
     */
    public static AwsConnectionParams fromString(String line) {
        StringValueMap map = StringValueMap.fromString(line);
        return new AwsConnectionParams(map);
    }

    /**
     * Validates this connection parameters
     *
     * @param context     (optional) a context to trace execution through call chain.
     */
    public void validate(IContext context) throws ConfigException {
        final String arn = this.getArn();
        if (arn.equals("arn:aws::::")) {
            throw new ConfigException(
                    context != null ? ContextResolver.getTraceId(context) : null,
                    "NO_AWS_CONNECTION",
                    "AWS connection is not set"
            );
        }

        if (this.getAccessId() == null) {
            throw new ConfigException(
                    context != null ? ContextResolver.getTraceId(context) : null,
                    "NO_ACCESS_ID",
                    "No access_id is configured in AWS credential"
            );
        }

        if (this.getAccessKey() == null) {
            throw new ConfigException(
                    context != null ? ContextResolver.getTraceId(context) : null,
                    "NO_ACCESS_KEY",
                    "No access_key is configured in AWS credential"
            );
        }
    }

    /**
     * Retrieves AwsConnectionParams from configuration parameters.
     * The values are retrieves from "connection" and "credential" sections.
     *
     * @param config          configuration parameters
     * @return the generated ConnectionParams object.
     * 
     * @see #mergeConfigs(ConfigParams...)
     */
    public static AwsConnectionParams fromConfig(ConfigParams config) {
        var result = new AwsConnectionParams();

        var credentials = CredentialParams.manyFromConfig(config);
        for (var credential : credentials)
            result.append(credential);

        var connections = ConnectionParams.manyFromConfig(config);
        for (var connection : connections)
            result.append(connection);

        return result;
    }

    /**
     * Retrieves AwsConnectionParams from multiple configuration parameters.
     * The values are retrieves from "connection" and "credential" sections.
     *
     * @param configs 	                a list with configuration parameters
     * @return AwsConnectionParams	the generated AwsConnectionParams object.
     *
     * @see #fromConfig(ConfigParams) fromConfig
     */
    public static AwsConnectionParams mergeConfigs(ConfigParams... configs) {
        var config = ConfigParams.mergeConfigs(configs);
        return new AwsConnectionParams(config);
    }

}
