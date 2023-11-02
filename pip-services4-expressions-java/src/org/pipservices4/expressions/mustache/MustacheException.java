package org.pipservices4.expressions.mustache;

import org.pipservices4.commons.errors.BadRequestException;
import org.pipservices4.components.context.ContextResolver;
import org.pipservices4.components.context.IContext;

/**
 * Exception that can be thrown by Mustache Template.
 */
public class MustacheException extends BadRequestException {
    public MustacheException(IContext context, String code, String message, int line, int column) {
        super(
                context != null ? ContextResolver.getTraceId(context) : null,
                code,
                line != 0 || column != 0 ? message + " at line " + line + " and column " + column : message
        );
    }
}
