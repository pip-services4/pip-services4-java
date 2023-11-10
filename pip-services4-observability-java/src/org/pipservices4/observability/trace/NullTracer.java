package org.pipservices4.observability.trace;

import org.pipservices4.components.context.IContext;

/**
 * Dummy implementation of tracer that doesn't do anything.
 * <p>
 * It can be used in testing or in situations when tracing is required
 * but shall be disabled.
 *
 * @see ITracer
 */
public class NullTracer implements ITracer {
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
        // Do nothing...
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
        // Do nothing...
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
