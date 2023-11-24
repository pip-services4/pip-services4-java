package org.pipservices4.messaging.build;

import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.components.config.IConfigurable;
import org.pipservices4.commons.errors.ConfigException;
import org.pipservices4.components.refer.IReferenceable;
import org.pipservices4.components.refer.IReferences;
import org.pipservices4.components.refer.ReferenceException;
import org.pipservices4.components.build.Factory;
import org.pipservices4.messaging.queues.IMessageQueue;

/**
 * Creates {@link IMessageQueue} components by their descriptors.
 * Name of created message queue is taken from its descriptor.
 *
 * @see Factory
 * @see org.pipservices4.messaging.queues.MessageQueue
 */
public abstract class MessageQueueFactory extends Factory implements IMessageQueueFactory, IConfigurable, IReferenceable {
    protected ConfigParams _config;
    protected IReferences _references;

    /**
     * Configures component by passing configuration parameters.
     *
     * @param config configuration parameters to be set.
     */
    @Override
    public void configure(ConfigParams config) throws ConfigException {
        this._config = config;
    }

    /**
     * Creates a message queue component and assigns its name.
     *
     * @param name a name of the created message queue.
     */
    @Override
    public abstract IMessageQueue createQueue(String name) throws ReferenceException;

    /**
     * Sets references to dependent components.
     *
     * @param references references to locate the component dependencies.
     */
    @Override
    public void setReferences(IReferences references) throws ReferenceException, ConfigException {
        this._references = references;
    }
}
