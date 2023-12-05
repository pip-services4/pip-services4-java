package org.pipservices4.aws.clients;

import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.components.context.IContext;

import java.util.Map;

/**
 * Abstract client that calls commandable AWS Lambda Functions.
 * <p>
 * Commandable services are generated automatically for <a href="https://pip-services4-java.github.io/pip-services4-rpc-java/org/pipservices4/rpc/commands/ICommandable.html">ICommandable</a> objects.
 * Each command is exposed as action determined by "cmd" parameter.
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
 * class MyLambdaClient extends CommandableLambdaClient implements IMyClient {
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
public class CommandableLambdaClient extends LambdaClient {
	private String _name;

	/**
	 * Creates a new instance of this client.
	 *
	 * @param name a service name.
	 */
	public CommandableLambdaClient(String name) {
		this._name = name;
	}

	/**
	 * Calls a remote action in AWS Lambda function.
	 * The name of the action is added as "cmd" parameter
	 * to the action parameters.
	 *
	 * @param type          the generic class type of data.
	 * @param cmd               an action name
	 * @param context     (optional) a context to trace execution through call chain.
	 * @param params            command parameters.
	 * @return           action result.
	 */
	public <T> T callCommand(Class<T> type, String cmd, IContext context, Map<String, Object> params)
			throws ApplicationException {
		var command = this._name + '.' + cmd;
        var timing = this.instrument(context, command);
		try {
            var result = call(type, command, context, params);
			timing.endTiming();
			return result;
		} catch (Exception ex) {
			timing.endFailure(ex);
			throw ex;
		}
	}

}
