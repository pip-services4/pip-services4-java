package org.pipservices4.commons.convert;

import static org.junit.Assert.*;

import java.time.*;
import java.util.List;
import java.util.Map;

import org.junit.*;
import org.pipservices4.commons.data.*;

public class JsonConverterTest {

	@Test
	public void testToJson() throws Exception {
		assertNull(JsonConverter.toJson(null));
		assertEquals("123", JsonConverter.toJson(123));
		assertEquals("\"ABC\"", JsonConverter.toJson("ABC"));
				
		FilterParams filter = FilterParams.fromTuples("Key1", 123, "Key2", "ABC");
		String jsonFilter = JsonConverter.toJson(filter);
		assertEquals("{\"Key2\":\"ABC\",\"Key1\":\"123\"}", jsonFilter);
		
		AnyValueArray array = AnyValueArray.fromValues(123, "ABC");
		String jsonArray = JsonConverter.toJson(array);
		assertEquals("[123,\"ABC\"]", jsonArray);

		ZonedDateTime date = DateTimeConverter.toDateTime("1975-04-08T00:00:00.000Z");
		String jsonDate = JsonConverter.toJson(date);
		assertEquals("\"1975-04-08T00:00:00Z\"", jsonDate);
	}

	@Test
	public void testFromJson() throws Exception {
		assertNull(JsonConverter.toJson(null));
		assertEquals(123, (int)JsonConverter.fromJson(Integer.class, "123"));
		assertEquals("ABC", JsonConverter.fromJson(String.class, "\"ABC\""));
				
		FilterParams filter = JsonConverter.fromJson(FilterParams.class, "{\"Key2\":\"ABC\",\"Key1\":\"123\"}");
		assertEquals(2, filter.size());
		
		AnyValueArray array = JsonConverter.fromJson(AnyValueArray.class, "[123,\"ABC\"]");
		assertEquals(2, array.size());

		ZonedDateTime date = DateTimeConverter.toDateTime("1975-04-08T00:00:00.000Z");
		ZonedDateTime jsonDate = JsonConverter.fromJson(ZonedDateTime.class, "\"1975-04-08T00:00Z\"");
		assertEquals(date.getYear(), jsonDate.getYear());
		assertEquals(date.getMonth(), jsonDate.getMonth());
		assertEquals(date.getDayOfMonth(), jsonDate.getDayOfMonth());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testJsonToMap() {
		// Handling simple objects
		String value = "{ \"value1\":123, \"value2\":234 }";
		Map<String, Object> result = JsonConverter.toNullableMap(value);
		assertEquals(123, result.get("value1"));
		assertEquals(234, result.get("value2"));

        // Recursive conversion
        value = "{ \"value1\":123, \"value2\": { \"value1\": 111, \"value2\": 222 } }";
        result = JsonConverter.toNullableMap(value);
        assertNotNull(result);
        assertEquals(123, result.get("value1"));
        assertNotNull(result.get("value2"));
        assertTrue(result.get("value2") instanceof Map<?,?>);

        // Handling arrays
        value = "{ \"value1\": [{ \"value1\": 111, \"value2\": 222 }] }";
        result = JsonConverter.toNullableMap(value);
        assertNotNull(result);
        assertTrue(result.get("value1") instanceof List<?>);
        List<Object> resultElements = ((List<Object>)result.get("value1"));
        Map<String, Object> resultElement0 = (Map<String, Object>)resultElements.get(0);
        assertNotNull(resultElement0);
        assertEquals(111, resultElement0.get("value1"));
        assertEquals(222, resultElement0.get("value2"));
	}

}
