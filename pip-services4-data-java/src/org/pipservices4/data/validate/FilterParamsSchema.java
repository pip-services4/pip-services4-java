package org.pipservices4.data.validate;

import org.pipservices4.commons.convert.TypeCode;
import org.pipservices4.data.query.FilterParams;

/**
 * Schema to validate {@link org.pipservices4.data.query.FilterParams}.
 *
 * @see FilterParams
 */
public class FilterParamsSchema extends MapSchema {

    /**
     * Creates a new instance of validation schema.
     */
    public FilterParamsSchema() {
        super(TypeCode.String, null);
    }
}
