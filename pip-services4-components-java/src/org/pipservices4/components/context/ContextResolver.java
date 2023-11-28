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
     * @param context execution context to trace execution through call chain.
     * @return a trace id or <code>null</code> if it is not defined.
     * @see IContext
     */
    public static String getTraceId(IContext context) {
        return (
                context.get("trace_id") != null ? valToString(context.get("trace_id")) :
                        context.get("traceId") != null ? valToString(context.get("traceId")) : null
        );
    }

    /**
     * Extracts client name from execution context.
     *
     * @param context execution context to trace execution through call chain.
     * @return a client name or <code>null</code> if it is not defined.
     * @see [[IContext]]
     */
    public static String getClient(IContext context) {
        return context.get("client") != null ? valToString(context.get("client")) : null;
    }

    /**
     * Extracts user name (identifier) from execution context.
     *
     * @param context execution context to trace execution through call chain.
     * @return a user reference or <code>null</code> if it is not defined.
     * @see [[IContext]]
     */
    public static String getUser(IContext context) {
        return context.get("user") != null ? valToString(context.get("user")) : null;
    }

    private static String valToString(Object value) {
        if (value instanceof String)
            return (String) value;
        else
            return null;
    }
}
