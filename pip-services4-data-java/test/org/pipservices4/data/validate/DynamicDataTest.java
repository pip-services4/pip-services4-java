package org.pipservices4.data.validate;

import org.junit.Test;
import org.pipservices4.commons.convert.JsonConverter;
import org.pipservices4.commons.convert.TypeCode;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class DynamicDataTest {
    @Test
    public void testValidateDynamicData() throws IOException {
        String dynamicString = "{ \"string_field\": \"ABC\", \"date_field\": \"2019-01-01T11:30:00.00+00:00\", \"int_field\": 123, \"float_field\": 123.456 }";
        Object dynamicObject = JsonConverter.fromJson(Object.class, dynamicString);

        Schema schema = new ObjectSchema()
                .withRequiredProperty("string_field", TypeCode.String)
                .withRequiredProperty("date_field", TypeCode.DateTime)
                .withRequiredProperty("int_field", TypeCode.Integer)
                .withRequiredProperty("float_field", TypeCode.Float);

        List<ValidationResult> results = schema.validate(dynamicObject);
        assertEquals(results.size(), 0);
    }
}
