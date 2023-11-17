package org.pipservices4.http.clients;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.core.GenericType;
import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.components.context.IContext;

/**
 * Abstract client that calls commandable HTTP service.
 * <p>
 * Commandable services are generated automatically for <a href="https://pip-services4-java.github.io/pip-services4-rpc-java/org/pipservices4/rpc/commands/ICommandable.html">ICommandable</a> objects.
 * Each command is exposed as POST operation that receives all parameters
 * in body object.
 * <p>
 * ### Configuration parameters ###
 * <ul>
 * <li>base_route:              base route for remote URI
 * <li>connection(s):           
 *   <ul>
 *   <li>discovery_key:         (optional) a key to retrieve the connection from <a href="https://pip-services4-java.github.io/pip-services4-config-java/org/pipservices4/config/connect/IDiscovery.html">IDiscovery</a>
 *   <li>protocol:              connection protocol: http or https
 *   <li>host:                  host name or IP address
 *   <li>port:                  port number
 *   <li>uri:                   resource URI or connection string with all parameters in it
 *   </ul>
 * <li>options:
 *   <ul>
 *   <li>retries:               number of retries (default: 3)
 *   <li>connect_timeout:       connection timeout in milliseconds (default: 10 sec)
 *   <li>timeout:               invocation timeout in milliseconds (default: 10 sec)
 *   </ul>
 * </ul>  
 * <p>
 * ### References ###
 * <ul>
 * <li>*:logger:*:*:1.0         (optional) <a href="https://pip-services4-java.github.io/pip-services4-observability-java/org/pipservices4/observability/log/ILogger.html">ILogger</a> components to pass log messages
 * <li>*:counters:*:*:1.0         (optional) <a href="https://pip-services4-java.github.io/pip-services4-observability-java/org/pipservices4/observability/count/ICounters.html">ICounters</a> components to pass collected measurements
 * <li>*:tracer:*:*:1.0        (optional) <a href="https://pip-services4-java.github.io/pip-services4-observability-java/org/pipservices4/observability/trace/ITracer.html">ITracer</a> components to record traces
 * <li>*:discovery:*:*:1.0        (optional) <a href="https://pip-services4-java.github.io/pip-services4-config-java/org/pipservices4/config/connect/IDiscovery.html">IDiscovery</a> services to resolve connection
 * </ul>
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * class MyCommandableHttpClient extends CommandableHttpClient implements IMyClient {
 *    ...
 * 
 *    public MyData getData(IContext context, String id) {
 *        return this.callCommand(
 *        	  MyData.class,
 *            "get_data",
 *            context,
 *            new MyData(id)
 *        );        
 *    }
 *    ...
 * }
 * 
 * MyCommandableHttpClient client = new MyCommandableHttpClient();
 * client.configure(ConfigParams.fromTuples(
 *     "connection.protocol", "http",
 *     "connection.host", "localhost",
 *     "connection.port", 8080
 * ));
 * 
 * MyData data = client.getData("123", "1");
 * ...
 * }
 * </pre>
 */
public class CommandableHttpClient extends RestClient {

	/**
	 * Creates a new instance of the client.
	 * 
	 * @param baseRoute a base route for remote service.
	 */
	public CommandableHttpClient(String baseRoute) {
		this._baseRoute = baseRoute;
	}

	/**
	 * Calls a remote method via HTTP commadable protocol. The call is made via POST
	 * operation and all parameters are sent in body object. The complete route to
	 * remote method is defined as baseRoute + "/" + name.
	 * 
	 * @param type          the class type.
	 * @param route         a name of the command to call.
	 * @param context     (optional) a context to trace execution through call chain.
	 * @param entity        body object.
	 * @return result of the command.
	 * @throws ApplicationException when error occured.
	 */
	public <T> T callCommand(Class<T> type, String route, IContext context, Object entity)
			throws ApplicationException {
		return call(type, context, HttpMethod.POST, route, entity);
	}

	/**
	 * Calls a remote method via HTTP commadable protocol. The call is made via POST
	 * operation and all parameters are sent in body object. The complete route to
	 * remote method is defined as baseRoute + "/" + name.
	 * 
	 * @param type          the generic class type.
	 * @param route         a name of the command to call.
	 * @param context     (optional) a context to trace execution through call chain.
	 * @param entity        body object.
	 * @return result of the command.
	 * @throws ApplicationException when error occured.
	 */
	public <T> T callCommand(GenericType<T> type, String route, IContext context, Object entity)
			throws ApplicationException {
		return call(type, context, HttpMethod.POST, route, entity);
	}

}
