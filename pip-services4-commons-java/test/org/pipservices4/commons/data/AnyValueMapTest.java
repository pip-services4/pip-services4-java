package org.pipservices4.commons.data;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;
import org.pipservices4.commons.convert.JsonConverter;


public class AnyValueMapTest {
	
	@Test
	public void TestGetAsString() throws IOException
    {
		AnyValueMap message = AnyValueMap.fromTuples(
            "key1", 123,
            "key2", "ABC"
        );

        Object restoredMessageObject = JsonConverter.fromJson(Object.class, JsonConverter.toJson(message));
        AnyValueMap restoredMessage = AnyValueMap.fromValue(restoredMessageObject);

        assertEquals(2, restoredMessage.size());
        assertEquals(123, restoredMessage.getAsInteger("key1"));
        assertEquals("ABC", restoredMessage.getAsString("key2"));
    }
	
}
