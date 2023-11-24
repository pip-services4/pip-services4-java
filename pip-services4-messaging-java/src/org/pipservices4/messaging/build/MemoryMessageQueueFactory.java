package org.pipservices4.messaging.build;


import org.pipservices4.components.refer.Descriptor;
import org.pipservices4.components.refer.ReferenceException;
import org.pipservices4.messaging.queues.IMessageQueue;
import org.pipservices4.messaging.queues.MemoryMessageQueue;

/**
 * Creates {@link MemoryMessageQueue} components by their descriptors.
 * Name of created message queue is taken from its descriptor.
 *
 * @see <a href="https://pip-services4-java.github.io/pip-services4-components-java/org/pipservices4/components/build/Factory.html">Factory</a>
 * @see MemoryMessageQueue
 */
public class MemoryMessageQueueFactory extends MessageQueueFactory {
    private static final Descriptor MemoryQueueDescriptor = new Descriptor("pip-services", "message-queue", "memory", "*", "1.0");

    /**
     * Create a new instance of the factory.
     */
    public MemoryMessageQueueFactory() {
        register(MemoryQueueDescriptor, (locator) -> {
            Descriptor descriptor = (Descriptor) locator;
            return new MemoryMessageQueue(descriptor.getName());
        });
    }

    /**
     * Creates a message queue component and assigns its name.
     *
     * @param name a name of the created message queue.
     */
    @Override
    public IMessageQueue createQueue(String name) throws ReferenceException {
        var queue = new MemoryMessageQueue(name);

        if (this._config != null)
            queue.configure(this._config);

        if (this._references != null)
            queue.setReferences(this._references);

        return queue;
    }
}
