package org.pipservices4.observability.trace;

import org.pipservices4.commons.errors.ConfigException;
import org.pipservices4.components.context.IContext;
import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.refer.IReferenceable;
import org.pipservices4.components.refer.IReferences;
import org.pipservices4.components.refer.ReferenceException;

import java.util.ArrayList;
import java.util.List;

/**
 * Aggregates all tracers from component references under a single component.
 * <p>
 * It allows to record traces and conveniently send them to multiple destinations.
 * <p>
 * ### References ###
 * <ul>
 * <li>*:tracer:*:*:1.0         (optional) {@link ITracer} components to pass operation traces
 * </ul>
 *
 * @see ITracer
 * <p>
 * ### Example ###
 * <p>
 * {@code
 * public class MyComponent implements IReferenceable {
 *     private CompositeTracer _tracer = new CompositeTracer();
 *
 *     @Override
 *     public void setReferences(IReferences references) throws ReferenceException, ConfigException {
 *         this._tracer.setReferences(references);
 *         // ...
 *     }
 *
 *     public void myMethod(IContext context) {
 *         var timing = this._tracer.beginTrace(context, "mycomponent", "mymethod");
 *         try {
 *             // ...
 *             timing.endTrace();
 *         } catch (Exception err) {
 *             timing.endFailure(err);
 *         }
 *     }
 * }
 * }
 */
public class CompositeTracer implements ITracer, IReferenceable {
    protected final List<ITracer> _tracers = new ArrayList<>();

    /**
     * Creates a new instance of the tracer.
     *
     * @param references references to locate the component dependencies.
     * @throws ReferenceException
     * @throws ConfigException
     */
    public CompositeTracer(IReferences references) throws ReferenceException, ConfigException {
        if (references != null)
            this.setReferences(references);
    }

    public CompositeTracer() {
    }

    /**
     * Sets references to dependent components.
     *
     * @param references references to locate the component dependencies.
     */
    @Override
    public void setReferences(IReferences references) {
        List<ITracer> tracers = references.getOptional(ITracer.class, new Descriptor(null, "tracer", null, null, null));
        for (ITracer tracer : tracers) {
            if (tracer != this)
                this._tracers.add(tracer);
        }
    }

    /**
     * Records an operation trace with its name and duration
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param component     a name of called component
     * @param operation     a name of the executed operation.
     * @param duration      execution duration in milliseconds.
     */
    @Override
    public void trace(IContext context, String component, String operation, Long duration) {
        for (var tracer : this._tracers)
            tracer.trace(context, component, operation, duration);
    }

    /**
     * Records an operation failure with its name, duration and error
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param component     a name of called component
     * @param operation     a name of the executed operation.
     * @param error         an error object associated with this trace.
     * @param duration      execution duration in milliseconds.
     */
    @Override
    public void failure(IContext context, String component, String operation, Exception error, long duration) {
        for (var tracer : this._tracers)
            tracer.failure(context, component, operation, error, duration);
    }

    /**
     * Begings recording an operation trace
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param component     a name of called component
     * @param operation     a name of the executed operation.
     * @return a trace timing object.
     */
    @Override
    public TraceTiming beginTrace(IContext context, String component, String operation) {
        return new TraceTiming(context, component, operation, this);
    }
}
