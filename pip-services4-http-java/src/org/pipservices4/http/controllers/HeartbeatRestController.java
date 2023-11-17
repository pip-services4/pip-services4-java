package org.pipservices4.http.controllers;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.process.Inflector;
import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.commons.convert.StringConverter;
import org.pipservices4.commons.errors.ConfigException;

import java.time.ZonedDateTime;

/**
 * Service returns heartbeat via HTTP/REST protocol.
 * <p>
 * The service responds on /heartbeat route (can be changed)
 * with a string with the current time in UTC.
 * <p>
 * This service route can be used to health checks by loadbalancers and 
 * container orchestrators.
 * <p>
 * ### Configuration parameters ###
 * <ul>
 * <li>base_route:              base route for remote URI (default: "")
 * <li>route:                   route to heartbeat operation (default: "heartbeat")
 * <li>dependencies:
 *   <ul>
 *   <li>endpoint:              override for HTTP Endpoint dependency
 *   </ul>
 * <li>connection(s):           
 *   <ul>
 *   <li>discovery_key:         (optional) a key to retrieve the connection from <a href="https://pip-services4-java.github.io/pip-services4-config-java/org/pipservices4/config/connect/IDiscovery.html">IDiscovery</a>
 *   <li>protocol:              connection protocol: http or https
 *   <li>host:                  host name or IP address
 *   <li>port:                  port number
 *   <li>uri:                   resource URI or connection string with all parameters in it
 *   </ul>
 * </ul>
 * <p>
 * ### References ###
 * <ul>
 * <li>*:logger:*:*:1.0               (optional) <a href="https://pip-services4-java.github.io/pip-services4-observability-java/org/pipservices4/observability/log/ILogger.html">ILogger</a> components to pass log messages
 * <li>*:counters:*:*:1.0             (optional) <a href="https://pip-services4-java.github.io/pip-services4-observability-java/org/pipservices4/observability/count/ICounters.html">ICounters</a> components to pass collected measurements
 * <li>*:discovery:*:*:1.0            (optional) <a href="https://pip-services4-java.github.io/pip-services4-config-java/org/pipservices4/config/connect/IDiscovery.html">IDiscovery</a> services to resolve connection
 * <li>*:endpoint:http:*:1.0          (optional) {@link HttpEndpoint} reference
 * </ul>
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * HeartbeatRestController controller = new HeartbeatRestController();
 * controller.configure(ConfigParams.fromTuples(
 *     "route", "ping",
 *     "connection.protocol", "http",
 *     "connection.host", "localhost",
 *     "connection.port", 8080
 * ));
 * 
 * controller.open("123");
 * System.out.println("The Heartbeat controller is accessible at http://+:8080/ping");
 * }
 * </pre>
 * @see RestController
 */
public class HeartbeatRestController extends RestController {
	private String _route = "heartbeat";

	/**
	 * Creates a new instance of this service.
	 */
	public HeartbeatRestController() {
		super();
	}

	/**
	 * Configures component by passing configuration parameters.
	 * 
	 * @param config configuration parameters to be set.
	 * @throws ConfigException when configuration is wrong.
	 */
	public void configure(ConfigParams config) throws ConfigException {
		super.configure(config);

		_route = config.getAsStringWithDefault("route", _route);
	}

	/**
	 * Registers all service routes in HTTP endpoint.
	 */
	public void register() {
		registerRoute(HttpMethod.GET, _route, new Inflector<ContainerRequestContext, Response>() {
			@Override
			public Response apply(ContainerRequestContext request) {
				return heartbeat(request);
			}
		});
	}

	/**
	 * Handles heartbeat requests
	 * 
	 * @param request a HTTP request
	 * @return http response to the request.
	 */
	private Response heartbeat(ContainerRequestContext request) {
		String result = StringConverter.toString(ZonedDateTime.now());
		return sendResult(result);
	}
}
