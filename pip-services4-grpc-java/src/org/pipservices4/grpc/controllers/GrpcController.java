package org.pipservices4.grpc.controllers;

import com.google.protobuf.GeneratedMessageV3;
import io.grpc.*;
import io.grpc.stub.ServerCalls;
import io.grpc.stub.StreamObserver;
import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.components.config.IConfigurable;
import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.commons.errors.ConfigException;
import org.pipservices4.commons.errors.InvalidStateException;
import org.pipservices4.components.context.ContextResolver;
import org.pipservices4.components.refer.*;
import org.pipservices4.components.run.IOpenable;
import org.pipservices4.data.validate.Schema;
import org.pipservices4.observability.count.CompositeCounters;
import org.pipservices4.observability.log.CompositeLogger;
import org.pipservices4.observability.trace.CompositeTracer;
import org.pipservices4.rpc.trace.InstrumentTiming;
import org.pipservices4.components.context.IContext;

import java.util.*;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;

/**
 * Used for creating GRPC endpoints. An endpoint is a URL, at which a given service can be accessed by a client.
 * <p>
 * ### Configuration parameters ###
 * <p>
 * Parameters to pass to the {@link #configure} method for component configuration:
 * <pre>
 * - dependencies:
 *   - endpoint:              override for GRPC Endpoint dependency
 *   - controller:            override for Controller dependency
 * - connection(s):
 *   - discovery_key:         (optional) a key to retrieve the connection from {@link org.pipservices4.config.connect.IDiscovery}
 *   - protocol:              connection protocol: http or https
 *   - host:                  host name or IP address
 *   - port:                  port number
 *   - uri:                   resource URI or connection string with all parameters in it
 * - credential - the HTTPS credentials:
 *   - ssl_key_file:         the SSL private key in PEM
 *   - ssl_crt_file:         the SSL certificate in PEM
 *   - ssl_ca_file:          the certificate authorities (root cerfiticates) in PEM
 * </pre>
 * <p>
 * ### References ###
 * <p>
 * A logger, counters, and a connection resolver can be referenced by passing the
 * following references to the object's {@link #setReferences} method:
 * <p>
 * - *:logger:*:*:1.0               (optional) {@link org.pipservices4.observability.log.ILogger} components to pass log messages
 * - *:counters:*:*:1.0             (optional) {@link org.pipservices4.observability.count.ICounters} components to pass collected measurements
 * - *:discovery:*:*:1.0            (optional) {@link org.pipservices4.config.connect.IDiscovery} services to resolve connection
 * - *:endpoint:grpc:*:1.0           (optional) {@link GrpcEndpoint} reference
 *
 * @see org.pipservices4.grpc.clients.GrpcClient
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * class MyGrpcController extends GrpcController {
 *     private IMyService _service;
 *
 *     public MyGrpcService() {
 *         super(myGrpcController.getServiceDescriptor());
 *         this._dependencyResolver.put("service",new Descriptor("mygroup","service","*","*","1.0"));
 *     }
 *
 *     public void setReferences(IReferences references) throws ReferenceException, ConfigException {
 *         super.setReferences(references);
 *         this._service = this._dependencyResolver.getRequired(IMyController.class, "service");
 *     }
 *
 *     private void getMydata(MyDataRequest request, StreamObserver<MyDataPage> responseObserver) {
 *         var context = Context.fromTraceId(request.getTraceId());
 *         var id = request.getId();
 *         return this._service.getMyData(context, id);
 *     }
 *
 *     public void register() {
 *         this.registerMethod(
 *                 "get_mydata",
 *                 null,
 *                 // new ObjectSchema(true)
 *                 //     .withOptionalProperty("paging", new PagingParamsSchema())
 *                 //     .withOptionalProperty("filter", new FilterParamsSchema()),
 *                 this::getMydata
 *         );
 *         // ...
 *     }
 *
 *     public static void main(String[] args) throws ApplicationException {
 *         var controller = new MyGrpcController();
 *         controller.configure(ConfigParams.fromTuples(
 *                 "connection.protocol", "http",
 *                 "connection.host", "localhost",
 *                 "connection.port", 8080
 *         ));
 *         controller.setReferences(References.fromTuples(
 *                 new Descriptor("mygroup","service","default","default","1.0"), _service
 *         ));
 *         controller.open("123");
 *         System.out.println("The GRPC service is running on port 8080");
 *     }
 * }
 * }
 */
public abstract class GrpcController implements IOpenable, IConfigurable, IReferenceable,
        IUnreferenceable, IRegisterable {

    private static final ConfigParams _defaultConfig = ConfigParams.fromTuples(
            "dependencies.endpoint", "*:endpoint:grpc:*:1.0"
    );

    private final ServerServiceDefinition.Builder _builder;

    private final io.grpc.ServiceDescriptor _serviceDescriptor;
    private final String _serviceName;
    private ConfigParams _config;
    private IReferences _references;
    private boolean _localEndpoint;
    private final IRegisterable _registrable;
    //    private List<Interceptor> _interceptors = new List<Interceptor>();
    //    private _implementation: any = {};

    Map<String, CommandFunction> _commandableMethods = new HashMap<>();
    private boolean _opened = false;

    /**
     * The GRPC endpoint that exposes this service.
     */
    protected GrpcEndpoint _endpoint;
    /**
     * The dependency resolver.
     */
    protected DependencyResolver _dependencyResolver = new DependencyResolver(GrpcController._defaultConfig);
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


    public GrpcController(io.grpc.ServiceDescriptor serviceDescriptor) {
        _serviceDescriptor = serviceDescriptor;
        _serviceName = _serviceDescriptor.getName();
        _builder = ServerServiceDefinition.builder(_serviceName);
        _registrable = this::registerService;
    }

    /**
     * Configures component by passing configuration parameters.
     *
     * @param config configuration parameters to be set.hnnhhjjnnmmkkkjjhhujnmjjhhhhhh
     */
    public void configure(ConfigParams config) throws ConfigException {
        config = config.setDefaults(GrpcController._defaultConfig);

        this._config = config;
        this._dependencyResolver.configure(config);
    }

    /**
     * Sets references to dependent components.
     *
     * @param references references to locate the component dependencies.
     */
    public void setReferences(IReferences references) throws ReferenceException, ConfigException {
        this._references = references;

        this._logger.setReferences(references);
        this._counters.setReferences(references);
        this._tracer.setReferences(references);
        this._dependencyResolver.setReferences(references);

        // Get endpoint
        this._endpoint = this._dependencyResolver.getOneOptional(GrpcEndpoint.class, "endpoint");
        // Or create a local one
        if (this._endpoint == null) {
            this._endpoint = this.createEndpoint();
            this._localEndpoint = true;
        } else {
            this._localEndpoint = false;
        }
        // Add registration callback to the endpoint
        this._endpoint.register(this._registrable);
    }

    /**
     * Unsets (clears) previously set references to dependent components.
     */
    public void unsetReferences() {
        // Remove registration callback from endpoint
        if (this._endpoint != null) {
            this._endpoint.unregister(this._registrable);
            this._endpoint = null;
        }
    }

    private GrpcEndpoint createEndpoint() throws ReferenceException, ConfigException {
        var endpoint = new GrpcEndpoint();

        if (this._config != null)
            endpoint.configure(this._config);

        if (this._references != null)
            endpoint.setReferences(this._references);

        return endpoint;
    }

    /**
     * Adds instrumentation to log calls and measure call time.
     * It returns a Timing object that is used to end the time measurement.
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
    public boolean isOpen() {
        return this._opened;
    }

    /**
     * Opens the component.
     *
     * @param context     (optional) a context to trace execution through call chain.
     */
    public void open(IContext context) throws ApplicationException {
        if (this._opened)
            return;

        if (this._endpoint == null) {
            this._endpoint = this.createEndpoint();
            this._endpoint.register(this);
            this._localEndpoint = true;
        }

        if (this._localEndpoint) {
            this._endpoint.open(context);
        }

        this._opened = true;
    }

    /**
     * Closes component and frees used resources.
     *
     * @param context     (optional) a context to trace execution through call chain.
     */
    public void close(IContext context) throws InvalidStateException {
        if (!this._opened)
            return;

        if (this._endpoint == null) {
            throw new InvalidStateException(
                    ContextResolver.getTraceId(context),
                    "NO_ENDPOINT",
                    "GRPC endpoint is missing"
            );
        }

        if (this._localEndpoint)
            this._endpoint.close(context);

        this._opened = false;
    }

    private void registerService() {
        this.register();

        if (_endpoint != null) {
            var serviceDefinitions = _builder.build();
            _endpoint.registerService(serviceDefinitions);
        }
    }

    /**
     * Registers a middleware for methods in GRPC endpoint.
     *
     * @param action an action function that is called when middleware is invoked.
     */
    protected void registerInterceptor(InterceptorFunc action) {
        if (this._endpoint == null) return;
        this._endpoint._interceptors.add(new Interceptor(action));
    }

    /**
     * Registers a method in GRPC service.
     *
     * @param name   a method name
     * @param schema a validation schema to validate received parameters.
     * @param action an action function that is called when operation is invoked.
     */
    protected <TRequest extends GeneratedMessageV3, TResponse extends GeneratedMessageV3> void registerMethod(String name, Schema schema, GrpcFunc<TRequest, StreamObserver<TResponse>> action) {

        ServerCalls.UnaryMethod<TRequest, TResponse> handler = new ServerCalls.UnaryMethod<TRequest, TResponse>() {
            @Override
            public void invoke(TRequest request, StreamObserver<TResponse> responseObserver) {
                // TODO Validation schema

                action.apply(request, responseObserver);
            }
        };

        try {
            var method = _serviceDescriptor.getMethods().stream().filter((m) -> {
                var splitName = m.getFullMethodName().split("/");
                return splitName.length > 1 && Objects.equals(splitName[1], name);
            }).findFirst();

            MethodDescriptor<TRequest, TResponse> METHOD_INVOKE = MethodDescriptor.<TRequest, TResponse>newBuilder()
                    .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                    .setFullMethodName(generateFullMethodName(
                            _serviceName, name))
                    .setRequestMarshaller((MethodDescriptor.Marshaller<TRequest>) method.get().getRequestMarshaller())
                    .setResponseMarshaller((MethodDescriptor.Marshaller<TResponse>) method.get().getResponseMarshaller())
                    .build();

            _builder.addMethod(METHOD_INVOKE, asyncUnaryCall(handler));

        } catch (Exception ex) {
            System.err.println("Error register method");
            throw new RuntimeException(ex);
        }
    }

    /**
     * Registers all service routes in Grpc endpoint.
     * <p>
     * This method is called by the service and must be overriden
     * in child classes.
     */
    @Override
    public abstract void register();
}


class Interceptor implements ServerInterceptor {
    private final InterceptorFunc _interceptor;

    public Interceptor(InterceptorFunc interceptor) {
        _interceptor = interceptor;
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        return _interceptor.apply(call, headers, next);
    }
}
