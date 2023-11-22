package org.pipservices4.commons.errors;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Factory to create serializeable {@link ErrorDescription} from {@link ApplicationException}
 * or from arbitrary errors.
 * <p>
 * The ErrorDescriptions are used to pass errors through the wire between microservices
 * implemented in different languages. They allow to restore exceptions on the receiving side
 * close to the original type and preserve additional information.
 *
 * @see ErrorDescription
 * @see ApplicationException
 */
public class ErrorDescriptionFactory {

    /**
     * Creates a serializable ErrorDescription from error object.
     *
     * @param ex an error object
     * @return a serializeable ErrorDescription object that describes the error.
     */
    public static ErrorDescription create(Exception ex) {
        ErrorDescription description = new ErrorDescription();

        if (ex instanceof ApplicationException) {
            description.setCategory(((ApplicationException) ex).getCategory());
            description.setStatus(((ApplicationException) ex).getStatus());
            description.setCode(((ApplicationException) ex).getCode());
            description.setMessage(ex.getMessage());
            description.setDetails(((ApplicationException) ex).getDetails());
            description.setTraceId(((ApplicationException) ex).getTraceId());
            description.setCause(((ApplicationException) ex).getCauseString());
            description.setStackTrace(((ApplicationException) ex).getStackTraceString());
        } else {
            description.setType(ex.getClass().toString());
            description.setCategory(ErrorCategory.Unknown);
            description.setStatus(500);
            description.setCode("UNKNOWN");
            description.setMessage(ex.getMessage() == null || ex.getMessage().isEmpty() ? ex.toString() : ex.getMessage());

            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();

            description.setStackTrace(exceptionAsString);
        }

        return description;
    }

    /**
     * Creates a serializable ErrorDescription from throwable object with unknown error category.
     *
     * @param ex            an error object
     * @param traceId (optional) a unique transaction id to trace execution through call chain.
     * @return a serializeable ErrorDescription object that describes the error.
     */
    public static ErrorDescription create(Throwable ex, String traceId) {
        ErrorDescription description = new ErrorDescription();
        description.setType(ex.getClass().getCanonicalName());
        description.setCategory(ErrorCategory.Unknown);
        description.setStatus(500);
        description.setCode("Unknown");
        description.setMessage(ex.getMessage());

        Throwable t = ex.getCause();
        description.setCause(t != null ? t.toString() : null);

        StackTraceElement[] ste = ex.getStackTrace();
        StringBuilder builder = new StringBuilder();
        if (ste != null) {
            for (StackTraceElement stackTraceElement : ste) {
                if (!builder.isEmpty())
                    builder.append(" ");
                builder.append(stackTraceElement.toString());
            }
        }
        description.setStackTrace(builder.toString());
        description.setTraceId(traceId);

        return description;
    }

}
