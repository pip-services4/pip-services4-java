package org.pipservices4.components.refer;

import org.pipservices4.commons.errors.*;
import org.pipservices4.components.context.ContextResolver;
import org.pipservices4.components.context.IContext;

import java.io.Serial;

/**
 * Error when required component dependency cannot be found.
 */
public class ReferenceException extends InternalException {
    @Serial
    private static final long serialVersionUID = 439183381933188434L;

    /**
     * Creates an error instance and assigns its values.
     */
    public ReferenceException() {
        this(null, null);
    }

    /**
     * Creates an error instance and assigns its values.
     *
     * @param locator the locator to find reference to dependent component.
     */
    public ReferenceException(Object locator) {
        super(null, "REF_ERROR", "Failed to obtain reference to " + locator);
        this.withDetails("locator", locator);
    }

    /**
     * Creates an error instance and assigns its values.
     *
     * @param context (optional) a unique transaction id to trace execution
     *                      through call chain.
     * @param locator       the locator to find reference to dependent component.
     */
    public ReferenceException(IContext context, Object locator) {
        super(ContextResolver.getTraceId(context), "REF_ERROR", "Failed to obtain reference to " + locator);
        this.withDetails("locator", locator);
    }

    public ReferenceException(IContext context, String message) {
        super(ContextResolver.getTraceId(context), "REF_ERROR", message);
    }

    public ReferenceException(IContext context, String code, String message) {
        super(ContextResolver.getTraceId(context), code, message);
    }
}
