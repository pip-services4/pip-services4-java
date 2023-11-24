package org.pipservices4.messaging.queues;

import org.pipservices4.components.config.ConfigParams;
import org.pipservices4.components.config.IConfigurable;
import org.pipservices4.components.config.NameResolver;
import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.commons.errors.InvalidStateException;
import org.pipservices4.components.context.ContextResolver;
import org.pipservices4.components.refer.IReferenceable;
import org.pipservices4.components.refer.IReferences;
import org.pipservices4.components.refer.ReferenceException;
import org.pipservices4.components.run.IClosable;
import org.pipservices4.components.run.IOpenable;
import org.pipservices4.config.auth.CredentialParams;
import org.pipservices4.config.auth.CredentialResolver;
import org.pipservices4.config.connect.ConnectionParams;
import org.pipservices4.config.connect.ConnectionResolver;
import org.pipservices4.observability.count.CompositeCounters;
import org.pipservices4.observability.log.CompositeLogger;
import org.pipservices4.components.context.IContext;

import java.util.List;

/**
 * Abstract message queue that is used as a basis for specific message queue implementations.
 * <p>
 * ### Configuration parameters ###
 * <ul>
 * <li>name:                        name of the message queue
 * <li>connection(s):
 *   <ul>
 *   <li>discovery_key:             key to retrieve parameters from discovery service
 *   <li>protocol:                  connection protocol like http, https, tcp, udp
 *   <li>host:                      host name or IP address
 *   <li>port:                      port number
 *   <li>uri:                       resource URI or connection string with all parameters in it
 *   </ul>
 * <li>credential(s):
 *   <ul>
 *   <li>store_key:                 key to retrieve parameters from credential store
 *   <li>username:                  user name
 *   <li>password:                  user password
 *   <li>access_id:                 application access id
 *   <li>access_key:                application secret key
 *   </ul>
 * </ul>
 * <p>
 * ### References ###
 * <ul>
 * <li>*:logger:*:*:1.0              (optional) <a href="https://pip-services4-java.github.io/pip-services4-observability-java/org/pipservices4/observability/log/ILogger.html">ILogger</a> components to pass log messages
 * <li>*:counters:*:*:1.0            (optional) <a href="https://pip-services4-java.github.io/pip-services4-observability-java/org/pipservices4/observability/count/ICounters.html">ICounters</a> components to pass collected measurements
 * <li>*:discovery:*:*:1.0           (optional) <a href="https://pip-services4-java.github.io/pip-services4-config-java/org/pipservices4/config/connect/IDiscovery.html">IDiscovery</a> services to resolve connection
 * <li>*:credential-store:*:*:1.0    (optional) <a href="https://pip-services4-java.github.io/pip-services4-config-java/org/pipservices4/config/auth/ICredentialStore.html">ICredentialStore</a> componetns to lookup credential(s)
 * </ul>
 */
public abstract class MessageQueue implements IMessageQueue, IReferenceable, IConfigurable, IOpenable, IClosable {

    protected String _name;
    protected String _kind;
    protected MessagingCapabilities _capabilities = new MessagingCapabilities(true, true, true, true, true, true, true,
            false, true);

    protected final Object _lock = new Object();
    protected CompositeLogger _logger = new CompositeLogger();
    protected CompositeCounters _counters = new CompositeCounters();
    protected ConnectionResolver _connectionResolver = new ConnectionResolver();
    protected CredentialResolver _credentialResolver = new CredentialResolver();

    /**
     * Creates a new instance of the message queue.
     */
    public MessageQueue() {
    }

    /**
     * Creates a new instance of the message queue.
     *
     * @param name         (optional) a queue name
     * @param capabilities (optional) a capabilities of this message queue
     */
    public MessageQueue(String name, MessagingCapabilities capabilities) {
        this._name = name;
        this._capabilities = capabilities != null ? capabilities : this._capabilities;
    }

    /**
     * Creates a new instance of the message queue.
     *
     * @param name (optional) a queue name
     */
    public MessageQueue(String name) {
        _name = name;
    }

    /**
     * Creates a new instance of the message queue.
     *
     * @param name   (optional) a queue name
     * @param config configuration parameters
     */
    public MessageQueue(String name, ConfigParams config) {
        _name = name;
        if (config != null)
            configure(config);
    }

    /**
     * Configures component by passing configuration parameters.
     *
     * @param config configuration parameters to be set.
     */
    public void configure(ConfigParams config) {
        _name = NameResolver.resolve(config, _name);
        _logger.configure(config);
        _connectionResolver.configure(config, true);
        _credentialResolver.configure(config, true);

        _name = config.getAsStringWithDefault("queue", _name);
    }

    /**
     * Sets references to dependent components.
     *
     * @param references references to locate the component dependencies.
     * @throws ReferenceException when no found references.
     */
    public void setReferences(IReferences references) throws ReferenceException {
        _logger.setReferences(references);
        _counters.setReferences(references);
        _connectionResolver.setReferences(references);
        _credentialResolver.setReferences(references);
    }

    /**
     * Gets the queue name
     *
     * @return the queue name.
     */
    public String getName() {
        return _name != null ? _name : "undefined";
    }

    /**
     * Gets the queue capabilities
     *
     * @return the queue's capabilities object.
     */
    public MessagingCapabilities getCapabilities() {
        return _capabilities;
    }

    public abstract int readMessageCount();

    /**
     * Checks if the component is opened.
     *
     * @return true if the component has been opened and false otherwise.
     */
    public abstract boolean isOpen();

    /**
     * Opens the component.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @throws ApplicationException when error occured.
     */
    @Override
    public void open(IContext context) throws ApplicationException {
        ConnectionParams connection = _connectionResolver.resolve(context);
        CredentialParams credential = _credentialResolver.lookup(context);
        openWithParams(context, connection, credential);
    }

    /**
     * Opens the component with given connection and credential parameters.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param connection    connection parameters
     * @param credential    credential parameters
     * @throws ApplicationException when error occured.
     */
    protected abstract void openWithParams(IContext context, ConnectionParams connection, CredentialParams credential)
            throws ApplicationException;

    /**
     * Checks if the queue has been opened and throws an exception is it's not.
     * @param context     (optional) a context to trace execution through call chain.
     */
    protected void checkOpen(IContext context) throws InvalidStateException {
        if (!this.isOpen()) {
            throw new InvalidStateException(
                    context != null ? ContextResolver.getTraceId(context) : null,
                    "NOT_OPENED",
                    "The queue is not opened"
            );
        }
    }

    /**
     * Closes component and frees used resources.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @throws ApplicationException when error occured.
     */
    public abstract void close(IContext context) throws ApplicationException;

    /**
     * Clears component state.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @throws ApplicationException when error occured.
     */
    public abstract void clear(IContext context) throws ApplicationException;

    /**
     * Sends a message into the queue.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param message       a message envelop to be sent.
     * @throws ApplicationException when error occured.
     */
    public abstract void send(IContext context, MessageEnvelope message) throws ApplicationException;

    /**
     * Sends an object into the queue. Before sending the object is converted into
     * JSON string and wrapped in a [[MessageEnvelope]].
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param messageType   a message type
     * @param message       an object value to be sent
     * @throws ApplicationException when error occured.
     * @see #send(IContext, MessageEnvelope)
     */
    public void sendAsObject(IContext context, String messageType, Object message) throws ApplicationException {
        MessageEnvelope envelop = new MessageEnvelope(context, messageType, message);
        send(context, envelop);
    }

    /**
     * Peeks a single incoming message from the queue without removing it. If there
     * are no messages available in the queue it returns null.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @return a message envelop object.
     * @throws ApplicationException when error occured.
     */
    public abstract MessageEnvelope peek(IContext context) throws ApplicationException;

    /**
     * Peeks multiple incoming messages from the queue without removing them. If
     * there are no messages available in the queue it returns an empty list.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param messageCount  a maximum number of messages to peek.
     * @return a list with messages.
     * @throws ApplicationException when error occured.
     */
    public abstract List<MessageEnvelope> peekBatch(IContext context, int messageCount) throws ApplicationException;

    /**
     * Receives an incoming message and removes it from the queue.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param waitTimeout   a timeout in milliseconds to wait for a message to come.
     * @return a message envelop object.
     * @throws ApplicationException when error occured.
     */
    public abstract MessageEnvelope receive(IContext context, long waitTimeout) throws ApplicationException;

    /**
     * Renews a lock on a message that makes it invisible from other receivers in
     * the queue. This method is usually used to extend the message processing time.
     *
     * @param message     a message to extend its lock.
     * @param lockTimeout a locking timeout in milliseconds.
     * @throws ApplicationException when error occured.
     */
    public abstract void renewLock(MessageEnvelope message, long lockTimeout) throws ApplicationException;

    /**
     * Returnes message into the queue and makes it available for all subscribers to
     * receive it again. This method is usually used to return a message which could
     * not be processed at the moment to repeat the attempt. Messages that cause
     * unrecoverable errors shall be removed permanently or/and send to dead letter
     * queue.
     *
     * @param message a message to return.
     * @throws ApplicationException when error occured.
     */
    public abstract void abandon(MessageEnvelope message) throws ApplicationException;

    /**
     * Permanently removes a message from the queue. This method is usually used to
     * remove the message after successful processing.
     *
     * @param message a message to remove.
     * @throws ApplicationException when error occured.
     */
    public abstract void complete(MessageEnvelope message) throws ApplicationException;

    /**
     * Permanently removes a message from the queue and sends it to dead letter
     * queue.
     *
     * @param message a message to be removed.
     * @throws ApplicationException when error occured.
     */
    public abstract void moveToDeadLetter(MessageEnvelope message) throws ApplicationException;

    /**
     * Listens for incoming messages and blocks the current thread until queue is
     * closed.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param receiver      a receiver to receive incoming messages.
     * @throws ApplicationException when error occured.
     * @see IMessageReceiver
     */
    public abstract void listen(IContext context, IMessageReceiver receiver) throws ApplicationException;

    /**
     * Listens for incoming messages without blocking the current thread.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param receiver      a receiver to receive incoming messages.
     * @see IMessageReceiver
     */
    public void beginListen(IContext context, IMessageReceiver receiver) {
        // Start listening on a parallel tread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    listen(context, receiver);
                } catch (Exception ex) {
                    _logger.error(context, ex, "Failed to listen messages");
                }
            }
        }).start();
    }

    /**
     * Ends listening for incoming messages. When this method is call [[listen]]
     * unblocks the thread and execution continues.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @throws ApplicationException when error occured.
     */
    public abstract void endListen(IContext context) throws ApplicationException;

    /**
     * Gets a string representation of the object.
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "[" + getName() + "]";
    }

}
