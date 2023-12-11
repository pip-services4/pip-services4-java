package org.pipservices4.aws.count;

import org.pipservices4.aws.connect.AwsConnectionParams;
import org.pipservices4.aws.connect.AwsConnectionResolver;
import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.components.config.IConfigurable;
import org.pipservices4.components.context.Context;
import org.pipservices4.components.context.ContextInfo;
import org.pipservices4.components.context.IContext;
import org.pipservices4.components.refer.*;
import org.pipservices4.components.run.IOpenable;
import org.pipservices4.observability.count.*;
import org.pipservices4.observability.log.CompositeLogger;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Performance counters that periodically dumps counters to AWS Cloud Watch
 * Metrics.
 * <p>
 * ### Configuration parameters ###
 * <p>
 * <ul>
 * <li>connection(s):
 * <ul>
 * <li>discovery_key: (optional) a key to retrieve the connection from <a href=
 * "https://pip-services4-java.github.io/pip-services4-config-java/org/pipservices4/config/connect/IDiscovery.html">IDiscovery</a>
 * <li>region: (optional) AWS region
 * </ul>
 * <li>credentials:
 * <ul>
 * <li>store_key: (optional) a key to retrieve the credentials from <a href=
 * "https://pip-services4-java.github.io/pip-services4-auth-java/org/pipservices4/config/auth/ICredentialStore.html">ICredentialStore</a>
 * <li>access_id: AWS access/client id
 * <li>access_key: AWS access/client key
 * </ul>
 * <li>options:
 * <ul>
 * <li>interval: interval in milliseconds to save current counters measurements
 * (default: 5 mins)
 * <li>reset_timeout: timeout in milliseconds to reset the counters. 0 disables
 * the reset (default: 0)
 * </ul>
 * </ul>
 * <p>
 * ### References ###
 * <p>
 * <ul>
 * <li>*:discovery:*:*:1.0 (optional) <a href=
 * "https://pip-services4-java.github.io/pip-services4-config-java/org/pipservices4/config/connect/IDiscovery.html">IDiscovery</a>
 * services to resolve connection
 * <li>*:context-info:*:*:1.0 (optional) <a href=
 * "https://pip-services4-java.github.io/pip-services4-components-java/org/pipservices4/components/context/ContextInfo.html">ContextInfo</a>
 * to detect the context id and specify counters source
 * <li>*:credential-store:*:*:1.0 (optional) (optional) Credential stores to
 * resolve credentials
 * </ul>
 * <p>
 * ### Example ###
 * <p>
 * var counters = new CloudWatchCounters();
 * counters.configure(ConfigParams.fromTuples(
 * "connection.region", "us-east-1",
 * "connection.access_id", "XXXXXXXXXXX",
 * "connection.access_key", "XXXXXXXXXXX"
 * ));
 * counters.setReferences(References.fromTuples(
 * new Descriptor("pip-services", "logger", "console", "default", "1.0"),
 * new ConsoleLogger()
 * ));
 * <p>
 * counters.open("123");
 * <p>
 * counters.increment("mycomponent.mymethod.calls");
 * var timing = counters.beginTiming("mycomponent.mymethod.exec_time");
 * try {
 * ...
 * } finally {
 * timing.endTiming();
 * }
 * <p>
 * counters.dump();
 */
public class CloudWatchCounters extends CachedCounters implements IReferenceable, IOpenable, IConfigurable {
    private CompositeLogger _logger = new CompositeLogger();
    private AwsConnectionResolver _connectionResolver = new AwsConnectionResolver();
    private AwsConnectionParams _connection;
    private boolean _opened = false;
    private String _source;
    private String _instance;
    private CloudWatchClient _client;
    private long _connectTimeout = 30000;

    /**
     * Configures component by passing configuration parameters.
     *
     * @param config configuration parameters to be set.
     */
    @Override
    public void configure(ConfigParams config) {
        super.configure(config);

        this._connectionResolver.configure(config);
        this._source = config.getAsStringWithDefault("source", this._source);
        this._instance = config.getAsStringWithDefault("instance", this._instance);
        this._connectTimeout = config.getAsLongWithDefault("options.connect_timeout", _connectTimeout);
    }

    /**
     * Sets references to dependent components.
     *
     * @param references references to locate the component dependencies.
     */
    @Override
    public void setReferences(IReferences references) {
        this._logger.setReferences(references);
        this._connectionResolver.setReferences(references);

        var contextInfo = references.getOneOptional(ContextInfo.class,
                new Descriptor("pip-services", "context-info", "default", "*", "1.0"));

        if (contextInfo != null && this._source == null)
            this._source = contextInfo.getName();

        if (contextInfo != null && this._instance == null)
            this._instance = contextInfo.getContextId();
    }

    /**
     * Checks if the component is opened.
     *
     * @return true if the component has been opened and false otherwise.
     */
    @Override
    public boolean isOpen() {
        return this._opened;
    }

    /**
     * Opens the component.
     *
     * @param context (optional) a context to trace execution through call chain.
     */
    @Override
    public void open(IContext context) {
        if (this._opened)
            return;


        try {
            _connection = this._connectionResolver.resolve(context);

            _client = CloudWatchClient.builder()
                    .region(Region.of(this._connection.getRegion()))
                    .overrideConfiguration(c -> c.apiCallAttemptTimeout(Duration.ofMillis(_connectTimeout)))
                    .credentialsProvider(StaticCredentialsProvider
                            .create(AwsBasicCredentials
                                    .create(this._connection.getAccessId(), this._connection.getAccessKey())))
                    .build();
            this._opened = true;
        } catch (Exception ex) {
            this._client = null;
            this._logger.warn(context, "Connection to AWS CloudWatchClient is not configured: " + ex);
        }
    }

    /**
     * Closes component and frees used resources.
     *
     * @param context (optional) a context to trace execution through call chain.
     */
    @Override
    public void close(IContext context) {
        if (_client != null)
            _client.close();

        this._opened = false;
        this._client = null;
    }

    /**
     * Saves the current counters measurements.
     *
     * @param counters current counters measurements to be saves.
     */
    @Override
    protected void save(List<Counter> counters) {
        if (this._client == null)
            return;

        var data = new ArrayList<MetricDatum>();

        for (var counter : counters) {
            data.add(this.getCounterData(counter));
            if (data.size() >= 20) {
                this.putMetricData(data);
                data = new ArrayList<>();
            }
        }
        if (!data.isEmpty()) {
            this.putMetricData(data);
        }
    }

    private void putMetricData(List<MetricDatum> data) {
        try {
            PutMetricDataRequest request = PutMetricDataRequest.builder()
                    .namespace(_source)
                    .metricData(data).build();

            _client.putMetricData(request);
        } catch (CloudWatchException e) {
            this._logger.error(Context.fromTraceId("cloudwatch_counters"), e, "putMetricData error");
            throw e;
        }
    }

    private MetricDatum getCounterData(Counter counter) {
        Dimension dimension = Dimension.builder()
                .name("InstanceID")
                .value(this._instance)
                .build();

        MetricDatum value = MetricDatum.builder()
                .metricName(counter.getName())
                .unit(CloudWatchUnit.NONE)
                .timestamp(counter.getTime().toInstant())
                .dimensions(dimension).build();

        switch (counter.getType()) {
            case CounterType.Increment:
                value.toBuilder()
                        .value(counter.getCount().doubleValue())
                        .unit(CloudWatchUnit.Count).build();
                break;
            case CounterType.Interval:
                value.toBuilder()
                        .statisticValues(StatisticSet.builder()
                                .sampleCount(counter.getCount().doubleValue())
                                .maximum(counter.getMax().doubleValue())
                                .minimum(counter.getMin().doubleValue())
                                .sum(counter.getCount().doubleValue() * counter.getAverage().doubleValue())
                                .build())
                        .unit(CloudWatchUnit.Milliseconds).build();
                break;
            case CounterType.Statistics:
                value.toBuilder()
                        .statisticValues(StatisticSet.builder()
                                .sampleCount(counter.getCount().doubleValue())
                                .maximum(counter.getMax().doubleValue())
                                .minimum(counter.getMin().doubleValue())
                                .sum(counter.getCount().doubleValue() * counter.getAverage().doubleValue())
                                .build());
                break;
            case CounterType.LastValue:
                value.toBuilder()
                        .value(counter.getLast().doubleValue());
                break;
            case CounterType.Timestamp:
                value.toBuilder()
                        .value((double) counter.getTime().toInstant().toEpochMilli());
                break;
        }

        return value;
    }
}
