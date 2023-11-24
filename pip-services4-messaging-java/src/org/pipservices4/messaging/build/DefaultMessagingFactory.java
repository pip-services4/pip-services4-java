package org.pipservices4.messaging.build;

import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.build.Factory;
import org.pipservices4.messaging.queues.MemoryMessageQueue;

/**
 * Creates {@link MemoryMessageQueue} components by their descriptors.
 * Name of created message queue is taken from its descriptor.
 *
 * @see <a href="https://pip-services4-java.github.io/pip-services4-components-java/org/pipservices4/components/build/Factory.html">Factory</a>
 * @see MemoryMessageQueue
 */
public class DefaultMessagingFactory extends Factory {

    private static final Descriptor MemoryQueueDescriptor = new Descriptor("pip-services", "message-queue", "memory", "*", "1.0");
    private static final Descriptor MemoryQueueFactoryDescriptor = new Descriptor("pip-services", "queue-factory", "memory", "*", "1.0");

    /**
     * Create a new instance of the factory.
     */
    public DefaultMessagingFactory() {
        super();
        registerAsType(MemoryQueueFactoryDescriptor, MemoryMessageQueueFactory.class);
        register(MemoryQueueDescriptor, (locator) -> {
            Descriptor descriptor = (Descriptor) locator;
            return new MemoryMessageQueue(descriptor.getName());
        });
    }
}
