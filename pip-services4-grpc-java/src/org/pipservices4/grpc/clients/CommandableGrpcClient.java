package org.pipservices4.grpc.clients;

import org.pipservices4.commons.convert.JsonConverter;
import org.pipservices4.commons.errors.ApplicationExceptionFactory;
import org.pipservices4.commons.errors.ErrorDescription;
import org.pipservices4.components.context.ContextResolver;
import org.pipservices4.components.context.IContext;
import org.pipservices4.grpc.commandable.CommandableGrpc;
import org.pipservices4.grpc.commandable.InvokeReply;
import org.pipservices4.grpc.commandable.InvokeRequest;

import java.util.Map;

/**
 * Abstract client that calls commandable GRPC service.
 * <p>
 * Commandable services are generated automatically for {@link org.pipservices4.rpc.commands.ICommandable} objects.
 * Each command is exposed as Invoke method that receives all parameters as args.
 * <p>
 * ### Configuration parameters ###
 *
 * <pre>
 * - connection(s):
 *   - discovery_key:         (optional) a key to retrieve the connection from {@link org.pipservices4.config.connect.IDiscovery}
 *   - protocol:              connection protocol: http or https
 *   - host:                  host name or IP address
 *   - port:                  port number
 *   - uri:                   resource URI or connection string with all parameters in it
 * - options:
 *   - retries:               number of retries (default: 3)
 *   - connect_timeout:       connection timeout in milliseconds (default: 10 sec)
 *   - timeout:               invocation timeout in milliseconds (default: 10 sec)
 *   </pre>
 * <p>
 * ### References ###
 * <p>
 * - *:logger:\*:\*:1.0         (optional) {@link org.pipservices4.observability.log.ILogger} components to pass log messages
 * - *:counters:\*:\*:1.0         (optional) {@link org.pipservices4.observability.count.ICounters} components to pass collected measurements
 * - *:discovery:*:*:1.0        (optional) {@link org.pipservices4.config.connect.IDiscovery} services to resolve connection
 * ### Example ###
 * <pre>
 * {@code
 * class MyCommandableGrpcClient extends CommandableGrpcClient implements IMyClient {
 *
 *     public MyCommandableGrpcClient() {
 *         super("mydata");
 *     }
 *
 *     // ...
 *
 *     @Override
 *     public MyData getData(IContext context, String id) {
 *         return this.callCommand(MyData.class,
 *                 "get_data",
 *                 context,
 *                 Map.of("id", id)
 *         );
 *     }
 *
 *     public static void main(String[] args) throws ConfigException {
 *         var client = new MyCommandableGrpcClient();
 *         client.configure(ConfigParams.fromTuples(
 *                 "connection.protocol", "http",
 *                 "connection.host", "localhost",
 *                 "connection.port", 8080
 *         ));
 *
 *         var result = client.getData("123", "1");
 *     }
 * }
 * }
 */
public class CommandableGrpcClient extends GrpcClient {
    /**
     * The service name
     */
    protected String _name;

    /**
     * Create new instance of the commandable client
     *
     * @param name The service name
     */
    public CommandableGrpcClient(String name) {
        super(CommandableGrpc.getServiceDescriptor());
        _name = name;
    }

    /**
     * Calls a remote method via GRPC commadable protocol.
     * The call is made via Invoke method and all parameters are sent in args object.
     * The complete route to remote method is defined as serviceName + "." + name.
     *
     * @param returnType    generic type of the return object
     * @param name          a name of the command to call.
     * @param context     (optional) a context to trace execution through call chain.
     * @param params        command parameters.
     * @return the received result.
     */
    protected <T> T callCommand(Class<T> returnType, String name, IContext context, Object params) {
        var method = this._name + '.' + name;
        var timing = this.instrument(context, method);

        try {
            var request = InvokeRequest.newBuilder()
                    .setMethod(method)
                    .setArgsEmpty(params == null || params instanceof Map<?, ?> && ((Map<?, ?>) params).isEmpty());

            if (context != null)
                request.setTraceId(ContextResolver.getTraceId(context));

            if (params != null)
                request.setArgsJson(JsonConverter.toJson(params));

            InvokeReply response = this.call("invoke", context, request.build());

            // Handle error response
            if (!response.getError().getMessage().equals(""))
                throw ApplicationExceptionFactory.create(convertErrorDescription(response.getError()));


            // Handle empty response
            if (response.getResultEmpty() || response.getResultJson().equals("") || response.getResultJson().equals("{}")) {
                return null;
            }

            // Handle regular response
            return JsonConverter.fromJson(returnType, response.getResultJson());
        } catch (Exception ex) {
            timing.endFailure(ex);
            throw new RuntimeException(ex);
        } finally {
            timing.endSuccess();
        }
    }

    private ErrorDescription convertErrorDescription(org.pipservices4.grpc.commandable.ErrorDescription ex) {
        var err = new ErrorDescription();
        err.setStatus(ex.getStatus());
        err.setCause(ex.getCause());
        err.setTraceId(ex.getTraceId());
        err.setMessage(ex.getMessage());
        err.setCause(ex.getCause());
        err.setStackTrace(ex.getStackTrace());
        err.setCategory(ex.getCategory());
        err.setCode(ex.getCode());

        return err;
    }
}
