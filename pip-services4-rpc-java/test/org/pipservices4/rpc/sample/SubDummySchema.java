package org.pipservices4.rpc.sample;

import org.pipservices4.commons.convert.TypeCode;
import org.pipservices4.data.validate.ObjectSchema;

public class SubDummySchema extends ObjectSchema {
    public SubDummySchema() {
        super();
        this.withRequiredProperty("key", TypeCode.String);
        this.withOptionalProperty("content", TypeCode.String);
    }
}
