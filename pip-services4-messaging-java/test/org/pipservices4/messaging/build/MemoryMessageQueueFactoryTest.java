package org.pipservices4.messaging.build;

import static org.junit.Assert.*;
import org.junit.Test;
import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.build.CreateException;
import org.pipservices4.messaging.queues.IMessageQueue;

public class MemoryMessageQueueFactoryTest {
    @Test
    public void testCreateMessageQueue() throws CreateException {
        var factory = new MemoryMessageQueueFactory();
        var descriptor = new Descriptor("pip-services", "message-queue", "memory", "test", "1.0");

        var canResult = factory.canCreate(descriptor);
        assertNotNull(canResult);

        IMessageQueue queue = (IMessageQueue) factory.create(descriptor);
        assertNotNull(queue);
        assertEquals("test", queue.getName());
    }
}
