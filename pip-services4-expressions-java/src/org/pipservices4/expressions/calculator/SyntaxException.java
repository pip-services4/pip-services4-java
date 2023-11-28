package org.pipservices4.expressions.calculator;

import org.pipservices4.commons.errors.BadRequestException;
import org.pipservices4.components.context.ContextResolver;
import org.pipservices4.components.context.IContext;

/**
 * Exception that can be thrown by Expression Parser.
 */
public class SyntaxException extends BadRequestException {
    public SyntaxException(IContext context, String code,
                           String message, int line, int column) {
        super(
                ContextResolver.getTraceId(context),
                code,
                line != 0 || column != 0 ? message + " at line " + line + " and column " + column : message
        );
    }
}
