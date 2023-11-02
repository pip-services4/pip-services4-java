package org.pipservices4.data.validate;

import org.pipservices4.commons.convert.TypeCode;

/**
 * Schema to validate {@link org.pipservices4.data.query.ProjectionParams}.
 *
 * @see org.pipservices4.data.query.ProjectionParams
 */
public class ProjectionParamsSchema extends ArraySchema {

    /**
     * Creates a new instance of validation schema.
     */
    public ProjectionParamsSchema() {
        super(TypeCode.String);
    }
}
