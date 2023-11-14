package org.pipservices4.rpc.sample;

import org.pipservices4.commons.convert.TypeCode;
import org.pipservices4.data.validate.ArraySchema;
import org.pipservices4.data.validate.ObjectSchema;

public class DummySchema extends ObjectSchema {

	public DummySchema() {
		withOptionalProperty("id", TypeCode.String);
		withRequiredProperty("key", TypeCode.String);
		withOptionalProperty("content", TypeCode.String);
		withOptionalProperty("array", new ArraySchema(new SubDummySchema()));
	}
}
