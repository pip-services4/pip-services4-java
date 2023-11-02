package org.pipservices4.data.validate;

import org.pipservices4.commons.convert.TypeCode;

/**
 * Schema to validate {@link org.pipservices4.data.query.PagingParams}.
 *
 * @see org.pipservices4.data.query.PagingParams
 */
public class PagingParamsSchema extends ObjectSchema {

    /**
     * Creates a new instance of validation schema.
     */
    public PagingParamsSchema() {
        super();
        withOptionalProperty("skip", TypeCode.Long);
        withOptionalProperty("take", TypeCode.Long);
        withOptionalProperty("total", TypeCode.Boolean);
    }

}
