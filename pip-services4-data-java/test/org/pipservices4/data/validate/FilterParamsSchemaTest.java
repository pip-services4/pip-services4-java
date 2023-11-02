package org.pipservices4.data.validate;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.pipservices4.data.query.FilterParams;

public class FilterParamsSchemaTest {
	
	@Test
	public void testEmptyFilterParams() {
		FilterParamsSchema schema = new FilterParamsSchema();
		FilterParams filterParams = new FilterParams();

        List<ValidationResult> results = schema.validate(filterParams);
        assertEquals(0, results.size());
    }

    @Test
    public void testNonEmptyFilterParams() {
		FilterParamsSchema schema = new FilterParamsSchema();
		FilterParams filterParams = new FilterParams();
		filterParams.put("key", "test");

        List<ValidationResult> results = schema.validate(filterParams);
        assertEquals(0, results.size());
    }
}
