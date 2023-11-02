package org.pipservices4.components.context;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.components.config.IReconfigurable;
import org.pipservices4.commons.data.StringValueMap;

import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Context information component that provides detail information
 * about execution context: container or/and process.
 * <p>
 * Most often ContextInfo is used by logging and performance counters
 * to identify source of the collected logs and metrics.
 * <p>
 * ### Configuration parameters ###
 * <ul>
 * <li>name: 					the context (container or process) name
 * <li>description: 		   	human-readable description of the context
 * <li>properties: 			entire section of additional descriptive properties
 * <li>...
 * </ul>
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * ContextInfo contextInfo = new ContextInfo();
 * contextInfo.configure(ConfigParams.fromTuples(
 * 		"name", "MyMicroservice",
 * 		"description", "My first microservice"
 * ));
 *
 * context.getName();			// Result: "MyMicroservice"
 * context.getContextId();		// Possible result: "mylaptop"
 * context.getStartTime();		// Possible result: 2018-01-01:22:12:23.45Z
 * context.getUptime();			// Possible result: 3454345
 * }
 * </pre>
 */
public final class ContextInfo implements IReconfigurable {
    private String _name = "unknown";
    private StringValueMap _properties = new StringValueMap();
    private String _description = null;
    private String contextId = System.getenv("HOSTNAME") == null ?
            System.getenv("COMPUTERNAME") : System.getenv("HOSTNAME");
    private ZonedDateTime startTime = ZonedDateTime.now(ZoneId.of("UTC"));

    /**
     * Creates a new instance of this context info.
     */
    public ContextInfo() {
        _name = "unknown";
    }

    /**
     * Creates a new instance of this context info.
     *
     * @param name        (optional) a context name.
     * @param description (optional) a human-readable description of the context.
     */
    public ContextInfo(String name, String description) {
        setName(name != null ? name : "unknown");
        setDescription(description);
    }

    /**
     * Configures component by passing configuration parameters.
     *
     * @param config configuration parameters to be set.
     */
    @Override
    public void configure(ConfigParams config) {
        _name = config.getAsStringWithDefault("name", _name);
        _description = config.getAsStringWithDefault("description", _description);
        _properties = config.getSection("properties");

    }

    /**
     * Gets the context name.
     *
     * @return the context name
     */
    @JsonProperty("name")
    public String getName() {
        return _name;
    }

    /**
     * Sets the context name.
     *
     * @param _name a new name for the context.
     */
    public void setName(String _name) {
        this._name = _name != null ? _name : "unknown";
    }

    /**
     * Gets the human-readable description of the context.
     *
     * @return the human-readable description of the context.
     */
    @JsonProperty("description")
    public String getDescription() {
        return _description;
    }

    /**
     * Sets the human-readable description of the context.
     *
     * @param _description a new human-readable description of the context.
     */
    public void setDescription(String _description) {
        this._description = _description;
    }

    /**
     * Gets the unique context id. Usually it is the current host name.
     *
     * @return the unique context id.
     */
    @JsonProperty("context_id")
    public String getContextId() {
        return contextId;
    }

    /**
     * Sets the unique context id.
     *
     * @param contextId a new unique context id.
     */
    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    /**
     * Gets the context start time.
     *
     * @return the context start time.
     */
    @JsonProperty("start_time")
    public ZonedDateTime getStartTime() {
        return startTime;
    }

    /**
     * Sets the context start time.
     *
     * @param startTime a new context start time.
     */
    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime != null ? startTime : ZonedDateTime.now(ZoneId.of("UTC"));
    }

    /**
     * Calculates the context uptime as from the start time.
     *
     * @return number of milliseconds from the context start time.
     */
    @JsonProperty("uptime")
    public long getUptime() {
        return ZonedDateTime.now(ZoneId.of("UTC")).toInstant().toEpochMilli() - startTime.toInstant().toEpochMilli();
    }

    /**
     * Gets context additional parameters.
     *
     * @return a JSON object with additional context parameters.
     */
    @JsonProperty("properties")
    public StringValueMap getProperties() {
        return _properties;
    }

    /**
     * Sets context additional parameters.
     *
     * @param _properties a JSON object with context additional parameters
     */
    public void setProperties(StringValueMap _properties) {
        this._properties = _properties;
    }

    /**
     * Creates a new ContextInfo and sets its configuration parameters.
     *
     * @param config configuration parameters for the new ContextInfo.
     * @return a newly created ContextInfo
     */
    public static ContextInfo fromConfig(ConfigParams config) {
        ContextInfo result = new ContextInfo();
        result.configure(config);
        return result;
    }

}
