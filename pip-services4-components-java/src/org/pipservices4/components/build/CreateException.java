package org.pipservices4.components.build;


import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.commons.errors.InternalException;

/**
 * Error raised when factory is not able to create requested component.
 *
 * @see ApplicationException
 * @see InternalException
 */
public class CreateException extends InternalException {
    private static final long serialVersionUID = 2506495188126378894L;

    /**
     * Creates an error instance.
     */
    public CreateException() {
        this(null, null);
    }

    /**
     * Creates an error instance and assigns its values.
     *
     * @param traceId    (optional) a unique transaction id to trace execution through call chain.
     * @param locator       locator of the component that cannot be created.
     */
    public CreateException(String traceId, Object locator) {
        super(traceId, "CANNOT_CREATE", "Requested component " + locator + " cannot be created");
        this.withDetails("locator", locator);
    }

    /**
     * Creates an error instance and assigns its values.
     *
     * @param traceId    (optional) a unique transaction id to trace execution through call chain.
     * @param message       human-readable error.
     */
    public CreateException(String traceId, String message) {
        super(traceId, "CANNOT_CREATE", message);
    }
}
