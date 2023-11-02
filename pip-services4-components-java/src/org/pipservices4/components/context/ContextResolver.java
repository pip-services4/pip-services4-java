package org.pipservices4.components.context;

/**
 * Context resolver that processes context and extracts values from there.
 *
 * @see IContext
 */

public class ContextResolver {
    /**
     * Extracts trace id from execution context.
     *
     * @param context     execution context to trace execution through call chain.
     * @return       a trace id or <code>null</code> if it is not defined.
     * @see IContext
     */
    public static String getTraceId(IContext context) {
        return (String) (context.get("trace_id") != null ?
                context.get("trace_id") : context.get("traceId") != null ?
                context.get("traceId") : null);
    }

    /**
     * Extracts client name from execution context.
     *
     * @param context     execution context to trace execution through call chain.
     * @return       a client name or <code>null</code> if it is not defined.
     * @see [[IContext]]
     */
    public static String getClient(IContext context) {
        return context.get("client") != null ? (String) context.get("client") : null;
    }

    /**
     * Extracts user name (identifier) from execution context.
     *
     * @param context     execution context to trace execution through call chain.
     * @return       a user reference or <code>null</code> if it is not defined.
     * @see [[IContext]]
     */
    public static String getUser(IContext context) {
        return context.get("user") != null ? (String) context.get("user") : null;
    }
}
