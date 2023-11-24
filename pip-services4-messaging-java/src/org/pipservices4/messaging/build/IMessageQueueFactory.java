package org.pipservices4.messaging.build;

import org.pipservices4.components.refer.ReferenceException;
import org.pipservices4.messaging.queues.IMessageQueue;

/**
 * Creates message queue componens.
 *
 * @see org.pipservices4.messaging.queues.IMessageQueue
 */
public interface IMessageQueueFactory {
    /**
     * Creates a message queue component and assigns its name.
     *
     * @param name a name of the created message queue.
     */
    IMessageQueue createQueue(String name) throws ReferenceException;
}
