package org.pipservices4.messaging.queues;

import static org.junit.Assert.*;

import org.junit.Test;
import org.pipservices4.commons.convert.JsonConverter;
import org.pipservices4.components.context.Context;

import java.io.IOException;

public class MessageEnvelopTest {
    @Test
    public void testFromToJson() throws IOException {
        var message = new MessageEnvelope(Context.fromTraceId("123"), "Test", "This is a test message");
        var json = JsonConverter.toJson(message);

        var message2 = MessageEnvelope.fromJSON(json);
        assertEquals(message.getMessageId(), message2.getMessageId());
        assertEquals(message.getTraceId(), message2.getTraceId());
        assertEquals(message.getMessageType(), message2.getMessageType());
        assertEquals(message.getMessage().toString(), message2.getMessage().toString());
    }
}
