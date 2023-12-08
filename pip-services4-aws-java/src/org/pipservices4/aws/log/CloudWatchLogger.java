package org.pipservices4.aws.log;


import org.pipservices4.aws.connect.AwsConnectionParams;
import org.pipservices4.aws.connect.AwsConnectionResolver;
import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.commons.errors.ConfigException;
import org.pipservices4.commons.errors.InvocationException;
import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.components.context.Context;
import org.pipservices4.components.context.ContextInfo;
import org.pipservices4.components.context.ContextResolver;
import org.pipservices4.components.context.IContext;
import org.pipservices4.components.refer.*;
import org.pipservices4.components.run.IOpenable;
import org.pipservices4.observability.log.CachedLogger;

import org.pipservices4.observability.log.CompositeLogger;
import org.pipservices4.observability.log.LogLevel;
import org.pipservices4.observability.log.LogMessage;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.CloudWatchException;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.*;

import java.time.Duration;
import java.util.*;

/**
 * Logger that writes log messages to AWS Cloud Watch Log.
 * <p>
 * ### Configuration parameters ###
 *
 * <ul>
 * <li>stream:                        (optional) Cloud Watch Log stream (default: context name)
 * <li>group:                         (optional) Cloud Watch Log group (default: context instance ID or hostname)
 * <li>connections:
 * <ul>
 *     <li>discovery_key:               (optional) a key to retrieve the connection from <a href="https://pip-services4-java.github.io/pip-services4-config-java/org/pipservices4/config/connect/IDiscovery.html">IDiscovery</a>
 *     <li>region:                      (optional) AWS region
 * <ul/>
 * <li>credentials:
 * <ul>
 *     <li>store_key:                   (optional) a key to retrieve the credentials from <a href="https://pip-services4-java.github.io/pip-services4-auth-java/org/pipservices4/config/auth/ICredentialStore.html">ICredentialStore</a>
 *     <li>access_id:                   AWS access/client id
 *     <li>access_key:                  AWS access/client id
 * <ul/>    
 * <li>options:
 * <ul>
 *     <li>interval:        interval in milliseconds to save current counters measurements (default: 5 mins)
 *     <li>reset_timeout:   timeout in milliseconds to reset the counters. 0 disables the reset (default: 0)
 * <ul/>
 * </ul>
 * <p>
 * ### References ###
 * <p>
 * <li>*:context-info:*:*:1.0      (optional) {@link org.pipservices4.components.context.ContextInfo} to detect the context id and specify counters source
 * <li>*:discovery:*:*:1.0<         (optional) {@link org.pipservices4.config.connect.IDiscovery} services to resolve connection
 * <li>*:credential-store:*:*:1.0 (optional) (optional) Credential stores to resolve credentials
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 *     var logger = new Logger();
 *     logger.configure(ConfigParams.fromTuples(
 *         "stream", "mystream",
 *         "group", "mygroup",
 *         "connection.region", "us-east-1",
 *         "connection.access_id", "XXXXXXXXXXX",
 *         "connection.access_key", "XXXXXXXXXXX"
 *     ));
 *
 *     logger.setReferences(References.fromTuples(
 *         new Descriptor("pip-services", "logger", "console", "default", "1.0"),
 *         new ConsoleLogger()
 *     ));
 *     logger.open("123");
 *     logger.setLevel(LogLevel.debug);
 *
 *     var ex = new Exception();
 *     logger.error("123", ex, "Error occured: %s", ex.getMessage());
 *     logger.debug("123", "Everything is OK.");
 * }
 * </pre>
 */
public class CloudWatchLogger extends CachedLogger implements IReferenceable, IOpenable {

    private final AwsConnectionResolver _connectionResolver = new AwsConnectionResolver();
    private CloudWatchLogsClient _client = null;
    private Timer _timer;
    private AwsConnectionParams _connection;
    private long _connectTimeout = 30000;
    private String _group = "undefined";
    private String _stream = null;
    private String _lastToken = null;
    private final CompositeLogger _logger = new CompositeLogger();

    /**
     * Configures component by passing configuration parameters.
     *
     * @param config configuration parameters to be set.
     */
    @Override
    public void configure(ConfigParams config) {
        super.configure(config);

        this._connectionResolver.configure(config);

        this._group = config.getAsStringWithDefault("group", this._group);
        this._stream = config.getAsStringWithDefault("stream", this._stream);
        this._connectTimeout = config.getAsLongWithDefault("options.connect_timeout", this._connectTimeout);
    }

    /**
     * Sets references to dependent components.
     *
     * @param references references to locate the component dependencies.
     */
    @Override
    public void setReferences(IReferences references) {
        super.setReferences(references);
        this._logger.setReferences(references);
        this._connectionResolver.setReferences(references);

        var contextInfo = references.getOneOptional(ContextInfo.class,
                new Descriptor("pip-services", "context-info", "default", "*", "1.0"));

        if (contextInfo != null && this._stream == null)
            this._stream = contextInfo.getName();

        if (contextInfo != null && this._group == null)
            this._group = contextInfo.getContextId();
    }

    /**
     * Writes a log message to the logger destination.
     *
     * @param level             a log level.
     * @param context     (optional) a context to trace execution through call chain.
     * @param ex             an error object associated with this message.
     * @param message           a human-readable message to log.
     */
    protected void write(LogLevel level, IContext context, Exception ex, String message) {
        super.write(level, context, ex, message);
    }

    /**
     * Checks if the component is opened.
     *
     * @return true if the component has been opened and false otherwise.
     */
    @Override
    public boolean isOpen() {
        return this._timer != null;
    }

    /**
     * Opens the component.
     *
     * @param context 	(optional) execution context to trace execution through call chain.
     */
    @Override
    public void open(IContext context) throws ApplicationException {
        if (this.isOpen())
            return;


        var connection = this._connectionResolver.resolve(context);
        if (connection == null) {
            throw new ConfigException(
                    ContextResolver.getTraceId(context),
                    "NO_CONNECTION",
                    "Connection is not configured"
            );
        }

        _client = CloudWatchLogsClient.builder()
                .region(Region.of(this._connection.getRegion()))
                .overrideConfiguration(c -> c.apiCallAttemptTimeout(Duration.ofMillis(_connectTimeout)))
                .credentialsProvider(StaticCredentialsProvider
                        .create(AwsBasicCredentials
                                .create(this._connection.getAccessId(), this._connection.getAccessKey())))
                .build();
        try {
            _client.createLogGroup(CreateLogGroupRequest.builder().logGroupName(_group).build());
        } catch (CloudWatchLogsException ex) {
            if (!ex.awsErrorDetails().errorCode().equals("ResourceAlreadyExistsException")) {
                throw ex;
            }
        }

        try {
            _client.createLogStream(CreateLogStreamRequest.builder()
                    .logGroupName(_group)
                    .logStreamName(_stream).build());
        } catch (CloudWatchLogsException ex) {
            if (ex.awsErrorDetails().errorCode().equals("ResourceAlreadyExistsException")) {
                try {
                    DescribeLogStreamsRequest logStreamRequest = DescribeLogStreamsRequest.builder()
                            .logGroupName(_group)
                            .logStreamNamePrefix(_stream)
                            .build();
                    DescribeLogStreamsResponse describeLogStreamsResponse = _client.describeLogStreams(
                            logStreamRequest
                    );
                    if (!describeLogStreamsResponse.logStreams().isEmpty()) {
                        _lastToken = describeLogStreamsResponse.logStreams().get(0).uploadSequenceToken();
                    }
                    if (_timer == null) {
                        _timer = new Timer();
                        _timer.scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {
                                dump();
                            }
                        }, 0, _interval);
                    }
                    return;
                } catch(Exception err) {
                    throw err;
                }
            }
            throw ex;
        }
        _lastToken = null;
    }

    /**
     * Closes component and frees used resources.
     *
     * @param context 	(optional) execution context to trace execution through call chain.
     */
    @Override
    public void close(IContext context) throws InvocationException {
        this.save(this._cache);

        if (this._timer != null)
            this._timer.cancel();

        this._cache = new ArrayList<>();
        this._timer = null;
        this._client = null;
    }

    private String formatMessageText(LogMessage message) {
        var result = "";
        result += "[" + (message.getSource() != null ? message.getSource() : "---") + ":" +
                (message.getTraceId() != null ? message.getTraceId() : "---") + ":" + message.getLevel() + "] " +
                message.getMessage();
        if (message.getError() != null) {
            if (message.getMessage() == null || message.getMessage().isEmpty() ) {
                result += "Error: ";
            } else {
                result += ": ";
            }

            result += message.getError().getMessage();

            if (message.getError().getStackTrace() != null) {
                result += " StackTrace: " + message.getError().getStackTrace();
            }
        }

        return result;
    }

    /**
     * Saves log messages from the cache.
     *
     * @param messages a list with log messages
     */
    @Override
    protected void save(List<LogMessage> messages) throws InvocationException {
        if (!this.isOpen() || messages == null || messages.isEmpty())
            return;

        if (this._client == null) {
            throw new InvocationException(
                    "cloudwatch_logger", "NOT_OPENED", "CloudWatchLogger is not opened"
            );
        }

        var events = new ArrayList<InputLogEvent>();
        for (var message : messages) {
            events.add(InputLogEvent.builder()
                    .message(formatMessageText(message))
                    .timestamp(message.getTime().toInstant().toEpochMilli())
                    .build());
        }

        try {
            DescribeLogStreamsRequest logStreamRequest = DescribeLogStreamsRequest.builder()
                    .logGroupName(_group)
                    .logStreamNamePrefix(_stream)
                    .build();
            DescribeLogStreamsResponse describeLogStreamsResponse = _client.describeLogStreams(
                    logStreamRequest
            );
            if (!describeLogStreamsResponse.logStreams().isEmpty()) {
                _lastToken = describeLogStreamsResponse.logStreams().get(0).uploadSequenceToken();
            }

            PutLogEventsRequest putLogEventsRequest = PutLogEventsRequest.builder()
                    .logEvents(events)
                    .logGroupName(_group)
                    .logStreamName(_stream)
                    .sequenceToken(_lastToken)
                    .build();

            PutLogEventsResponse putLogEventsResponse = _client.putLogEvents(putLogEventsRequest);
            _lastToken = putLogEventsResponse.nextSequenceToken();
        } catch (Exception ex) {
            _logger.error(Context.fromTraceId("cloudwatch_logger"), ex, "putLogEvents error");
            throw ex;
        }
    }
}
