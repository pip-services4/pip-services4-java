package org.pipservices4.observability.trace;

import org.pipservices4.components.context.IContext;

/**
 * Timing object returned by {@link ITracer#beginTrace} to end timing
 * of execution block and record the associated trace.
 *
 * ### Example ###
 * {@code
 * TraceTiming timing = tracer.beginTrace("mymethod.exec_time");
 *
 * try {
 *     ...
 *     timing.endTrace();
 * } catch (Exception err) {
 *     timing.endFailure(err);
 * }
 * }
 */
public class TraceTiming {
    private final long _start;
    private final ITracer _tracer;
    private final IContext _context;
    private final String _component;
    private final String _operation;

    public TraceTiming(IContext context, String component, String operation) {
        this._context = context;
        this._component = component;
        this._operation = operation;
        this._tracer = null;
        this._start = System.currentTimeMillis();
    }

    /**
     * Creates a new instance of the timing callback object.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param component     an associated component name
     * @param operation     an associated operation name
     * @param tracer        a callback that shall be called when endTiming is called.
     */
    public TraceTiming(IContext context, String component, String operation, ITracer tracer) {
        this._context = context;
        this._component = component;
        this._operation = operation;
        this._tracer = tracer;
        this._start = System.currentTimeMillis();
    }

    /**
     * Ends timing of an execution block, calculates elapsed time
     * and records the associated trace.
     */
    public void endTrace() {
        if (this._tracer != null) {
            long elapsed = System.currentTimeMillis() - this._start;
            this._tracer.trace(this._context, this._component, this._operation, elapsed);
        }
    }

    /**
     * Ends timing of a failed block, calculates elapsed time
     * and records the associated trace.
     *
     * @param error an error object associated with this trace.
     */
    public void endFailure(Exception error) {
        if (this._tracer != null) {
            long elapsed = System.currentTimeMillis() - this._start;
            this._tracer.failure(this._context, this._component, this._operation, error, elapsed);
        }
    }
}
