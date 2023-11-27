package org.pipservices4.swagger.example.data;

import org.pipservices4.commons.convert.TypeCode;
import org.pipservices4.data.validate.ObjectSchema;

public class DummySchema extends ObjectSchema {
    public DummySchema() {
        this.withOptionalProperty("id", TypeCode.String);
        this.withRequiredProperty("key", TypeCode.String);
        this.withOptionalProperty("content", TypeCode.String);
    }
}
