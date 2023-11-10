package org.pipservices4.observability.trace;

import org.pipservices4.components.context.IContext;

/**
 * Interface for tracer components that capture operation traces.
 */
public interface ITracer {
    void trace(IContext context, String component, String operation, Long duration);

    void failure(IContext context, String component, String operation, Exception error, long duration);

    TraceTiming beginTrace(IContext context, String component, String operation);
}
