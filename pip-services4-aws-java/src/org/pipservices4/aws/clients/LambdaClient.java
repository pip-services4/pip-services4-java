package org.pipservices4.aws.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.ws.rs.core.GenericType;
import org.pipservices4.aws.connect.AwsConnectionParams;
import org.pipservices4.aws.connect.AwsConnectionResolver;
import org.pipservices4.commons.convert.JsonConverter;
import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.components.config.IConfigurable;
import org.pipservices4.components.context.ContextResolver;
import org.pipservices4.components.context.IContext;
import org.pipservices4.components.refer.DependencyResolver;
import org.pipservices4.data.keys.IdGenerator;
import org.pipservices4.commons.errors.*;
import org.pipservices4.components.refer.IReferenceable;
import org.pipservices4.components.refer.IReferences;
import org.pipservices4.components.refer.ReferenceException;
import org.pipservices4.components.run.IOpenable;
import org.pipservices4.observability.count.CompositeCounters;
import org.pipservices4.observability.log.CompositeLogger;
import org.pipservices4.observability.trace.CompositeTracer;
import org.pipservices4.rpc.trace.InstrumentTiming;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.LogType;

import java.time.Duration;
import java.util.Map;


/**
 * Abstract client that calls AWS Lambda Functions.
 *
 * When making calls "cmd" parameter determines which what action shall be called, while
 * other parameters are passed to the action itself.
 * <p>
 * ### Configuration parameters ###
 * <ul>
 * <li>connection(s):
 *   <ul>
 *   <li>discovery_key:         (optional) a key to retrieve the connection from <a href="https://pip-services4-java.github.io/pip-services4-config-java/org/pipservices4/config/connect/IDiscovery.html">IDiscovery</a>
 *   <li>region:                      (optional) AWS region
 *   </ul>
 * <li>credentials:
 *   <ul>
 *   <li>store_key:         (optional) a key to retrieve the credentials from <a href="https://pip-services4-java.github.io/pip-services4-config-java/org/pipservices4/config/auth/ICredentialStore.html">ICredentialStore</a>
 *   <li>access_id:                   AWS access/client id
 *   <li>access_key:                   AWS access/client key
 *   </ul>
 * <li>options:
 *   <ul>
 *   <li>connect_timeout:             (optional) connection timeout in milliseconds (default: 10 sec)
 *   </ul>
 * </ul>
 * <p>
 * ### References ###
 * <ul>
 * <li>*:logger:*:*:1.0           (optional) <a href="https://pip-services4-java.github.io/pip-services4-observability-java/org/pipservices4/observability/log/ILogger.html">ILogger</a> components to pass log messages
 * <li>*:counters:*:*:1.0         (optional) <a href="https://pip-services4-java.github.io/pip-services4-observability-java/org/pipservices4/observability/count/ICounters.html">ICounters</a> components to pass collected measurements
 * <li>*:credential-store:*:*:1.0        (optional) <a href="https://pip-services4-java.github.io/pip-services4-config-java/org/pipservices4/config/auth/ICredentialStore.html">ICredentialStore</a> components to record traces
 * <li>*:discovery:*:*:1.0        (optional) <a href="https://pip-services4-java.github.io/pip-services4-config-java/org/pipservices4/config/connect/IDiscovery.html">IDiscovery</a> services to resolve connection
 * </ul>
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * class MyLambdaClient extends LambdaClient implements IMyClient {
 *    ...
 *
 *    public MyData getData(IContext context, String id) {
 *        Timing timing = this.instrument(context, 'myclient.get_data');
 *        MyData result = this.call("get_data", context, new MyData(id));
 *        timing.endTiming();
 *        return result;
 *    }
 *    ...
 * }
 *
 * MyLambdaClient client = new MyLambdaClient();
 * client.configure(ConfigParams.fromTuples(
 *         "connection.region", "us-east-1",
 *         "connection.access_id", "XXXXXXXXXXX",
 *         "connection.access_key", "XXXXXXXXXXX",
 *         "connection.arn", "YYYYYYYYYYYYY"
 * ));
 *
 * MyData data = client.getData("123", "1");
 * ...
 * }
 * </pre>
 */
public abstract class LambdaClient implements IOpenable, IConfigurable, IReferenceable {
    protected DependencyResolver _dependencyResolver = new DependencyResolver();
    /**
     * The connection resolver.
     */
    protected AwsConnectionResolver _connectionResolver = new AwsConnectionResolver();
    /**
     * The logger.
     */
    protected CompositeLogger _logger = new CompositeLogger();
    /**
     * The performance counters.
     */
    protected CompositeCounters _counters = new CompositeCounters();
    /**
     * The tracer.
     */
    protected CompositeTracer _tracer = new CompositeTracer();

    /**
     * The connection timeout in milliseconds.
     */
    protected long _connectTimeout = 10000;
    /**
     * The invocation timeout in milliseconds.
     */
    protected boolean _opened = false;

    /**
     * The Lambda client.
     */
    protected software.amazon.awssdk.services.lambda.LambdaClient _client;

    /**
     * The AWS connection parameters
     */
    protected AwsConnectionParams _connection;

    /**
     * Creates a new instance of the client.
     */
    protected LambdaClient() {
    }


    /**
     * Configures component by passing configuration parameters.
     *
     * @param config configuration parameters to be set.
     */
    @Override
    public void configure(ConfigParams config) throws ConfigException {
        _connectionResolver.configure(config);
        _dependencyResolver.configure(config);

        _connectTimeout = config.getAsLongWithDefault("options.connect_timeout", _connectTimeout);
    }

    /**
     * Sets references to dependent components.
     *
     * @param references references to locate the component dependencies.
     * @throws ReferenceException when no found references.
     */
    @Override
    public void setReferences(IReferences references) throws ReferenceException {
        _logger.setReferences(references);
        _counters.setReferences(references);
        _tracer.setReferences(references);
        _connectionResolver.setReferences(references);
        _dependencyResolver.setReferences(references);
    }

    /**
     * Adds instrumentation to log calls and measure call time. It returns a Timing
     * object that is used to end the time measurement.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param name          a method name.
     * @return Timing object to end the time measurement.
     */
    protected InstrumentTiming instrument(IContext context, String name) {
        this._logger.trace(context, "Executing %s method", name);
        this._counters.incrementOne(name + ".exec_count");

        var counterTiming = this._counters.beginTiming(name + ".exec_time");
        var traceTiming = this._tracer.beginTrace(context, name, null);
        return new InstrumentTiming(context, name, "exec",
                this._logger, this._counters, counterTiming, traceTiming);
    }

    /**
     * Checks if the component is opened.
     *
     * @return true if the component has been opened and false otherwise.
     */
    @Override
    public boolean isOpen() {
        return _client != null;
    }

    /**
     * Opens the component.
     *
     * @param context     (optional) a context to trace execution through call chain.
     */
    @Override
    public void open(IContext context) {
        // Skip if already opened
        if (_client != null)
            return;

        try{
            _connection = _connectionResolver.resolve(context);
            _client = software.amazon.awssdk.services.lambda.LambdaClient.builder()
                    .region(Region.of(this._connection.getRegion()))
                    .overrideConfiguration(c -> c.apiCallAttemptTimeout(Duration.ofMillis(_connectTimeout)))
                    .credentialsProvider(StaticCredentialsProvider
                            .create(AwsBasicCredentials
                                    .create(this._connection.getAccessId(), this._connection.getAccessKey())))
                    .build();
            this._opened = true;
            this._logger.debug(context, "Lambda client connected to %s", this._connection.getArn());
        } catch (Exception ex) {
            this._client = null;
            this._logger.warn(context, "Connection to AWS Lambda Client is not configured: " + ex);
        }
    }

    /**
     * Closes component and frees used resources.
     *
     * @param context     (optional) a context to trace execution through call chain.
     */
    public void close(IContext context) {
        if (_client == null)
            return;

        _client.close();
        _client = null;
    }


    /**
     * Performs AWS Lambda Function invocation.
     *
     * @param type          the class type of data.
     * @param invocationType    an invocation type: "RequestResponse" or "Event"
     * @param cmd               an action name to be called.
     * @param context 	(optional) execution context to trace execution through call chain.
     * @param args              action arguments
     * @return            action result.
     */
    protected <T> T invoke(Class<T> type, String invocationType, String cmd, IContext context, Map<String, Object> args) throws ApplicationException {
        if (cmd == null) {
            try {
                throw new UnknownException(null, "NO_COMMAND", "Missing Seneca pattern cmd");
            } catch (UnknownException e) {
                throw new RuntimeException(e);
            }
        }

        args.put("cmd", cmd);
        args.put("trace_id", ContextResolver.getTraceId(context) != null
                ? ContextResolver.getTraceId(context) : IdGenerator.nextShort());

        InvokeRequest request = null;
        try {
            request = InvokeRequest.builder()
                    .functionName(this._connection.getArn())
                    .invocationType(invocationType)
                    .logType(LogType.NONE)
                    .payload(SdkBytes.fromUtf8String(JsonConverter.toJson(args)))
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        try {
            var data = _client.invoke(request);

            var payload = data.payload().asUtf8String();

            if (payload != null) {
                try {
                    return JsonConverter.fromJson(type, payload);
                } catch (Exception ex) {
                    throw new InvocationException(
                            ContextResolver.getTraceId(context),
                            "DESERIALIZATION_FAILED",
                            "Failed to deserialize result"
                    ).withCause(ex);
                }
            }
            return null;
        } catch (Exception ex) {
            throw new InvocationException(
                    ContextResolver.getTraceId(context),
                    "CALL_FAILED",
                    "Failed to invoke lambda function"
            ).withCause(ex);
        }
    }

    /**
     * Performs AWS Lambda Function invocation.
     *
     * @param type          the class type of data.
     * @param invocationType    an invocation type: "RequestResponse" or "Event"
     * @param cmd               an action name to be called.
     * @param context 	(optional) execution context to trace execution through call chain.
     * @param args              action arguments
     * @return            action result.
     */
    protected <T> T invoke(TypeReference<T> type, String invocationType, String cmd, IContext context, Map<String, Object> args) throws ApplicationException {
        if (cmd == null) {
            try {
                throw new UnknownException(null, "NO_COMMAND", "Missing Seneca pattern cmd");
            } catch (UnknownException e) {
                throw new RuntimeException(e);
            }
        }

        args.put("cmd", cmd);
        args.put("trace_id", ContextResolver.getTraceId(context) != null
                ? ContextResolver.getTraceId(context) : IdGenerator.nextShort());

        InvokeRequest request = null;
        try {
            request = InvokeRequest.builder()
                    .functionName(this._connection.getArn())
                    .invocationType(invocationType)
                    .logType(LogType.NONE)
                    .payload(SdkBytes.fromUtf8String(JsonConverter.toJson(args)))
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        try {
            var data = _client.invoke(request);

            var payload = data.payload().asUtf8String();

            if (payload != null) {
                try {
                    return JsonConverter.fromJson(type, payload);
                } catch (Exception ex) {
                    throw new InvocationException(
                            ContextResolver.getTraceId(context),
                            "DESERIALIZATION_FAILED",
                            "Failed to deserialize result"
                    ).withCause(ex);
                }
            }
            return null;
        } catch (Exception ex) {
            throw new InvocationException(
                    ContextResolver.getTraceId(context),
                    "CALL_FAILED",
                    "Failed to invoke lambda function"
            ).withCause(ex);
        }
    }

    /**
     * Calls a AWS Lambda Function action.
     *
     * @param type          the generic class type of data.
     * @param cmd               an action name to be called.
     * @param context     (optional) a context to trace execution through call chain.
     * @param params            (optional) action parameters.
     * @return           action result.
     * @throws ApplicationException when error occurred.
     */
    protected <T> T call(Class<T> type, String cmd, IContext context, Map<String, Object> params) throws ApplicationException {
        return invoke(type, "RequestResponse", cmd, context, params);
    }

    /**
     * Calls a AWS Lambda Function action.
     *
     * @param type          the generic class type of data.
     * @param cmd               an action name to be called.
     * @param context     (optional) a context to trace execution through call chain.
     * @param params            (optional) action parameters.
     * @return           action result.
     * @throws ApplicationException when error occurred.
     */
    protected <T> T call(TypeReference<T> type, String cmd, IContext context, Map<String, Object> params) throws ApplicationException {
        return invoke(type, "RequestResponse", cmd, context, params);
    }


    /**
     * Calls a AWS Lambda Function action asynchronously without waiting for response.
     *
     * @param type          the class type of data.
     * @param cmd               an action name to be called.
     * @param context     (optional) a context to trace execution through call chain.
     * @param params            (optional) action parameters.
     * @return            action result.
     * @throws ApplicationException when error occurred.
     */
    protected <T> T callOneWay(Class<T> type, String cmd,  IContext context, Map<String, Object> params) throws ApplicationException {
        return invoke(type,"Event", cmd, context, params);
    }

    /**
     * Calls a AWS Lambda Function action asynchronously without waiting for response.
     *
     * @param type          the generic class type of data.
     * @param cmd               an action name to be called.
     * @param context     (optional) a context to trace execution through call chain.
     * @param params            (optional) action parameters.
     * @return            action result.
     * @throws ApplicationException when error occurred.
     */
    protected <T> T callOneWay(TypeReference<T> type, String cmd,  IContext context, Map<String, Object> params) throws ApplicationException {
        return invoke(type,"Event", cmd, context, params);
    }
}
