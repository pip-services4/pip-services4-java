package org.pipservices4.messaging.queues;

import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.components.context.IContext;
import org.pipservices4.components.run.IOpenable;

import java.util.List;

/**
 * Interface for asynchronous message queues.
 * <p>
 * Not all queues may implement all the methods.
 * Attempt to call non-supported method will result in NotImplemented exception.
 * To verify if specific method is supported consult with {@link MessagingCapabilities}.
 *
 * @see MessageEnvelope
 * @see MessagingCapabilities
 */
public interface IMessageQueue extends IOpenable {

    /**
     * Gets the queue name
     *
     * @return the queue name.
     */
    String getName();

    /**
     * Gets the queue capabilities
     *
     * @return the queue's capabilities object.
     */
    MessagingCapabilities getCapabilities();

    /**
     * Gets the current number of messages in the queue to be delivered.
     *
     * @return number of messages.
     */
    int readMessageCount();

    /**
     * Sends a message into the queue.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param envelop       a message envelop to be sent.
     * @throws ApplicationException when error occured.
     */
    void send(IContext context, MessageEnvelope envelop) throws ApplicationException;

    /**
     * Sends an object into the queue. Before sending the object is converted into
     * JSON string and wrapped in a MessageEnvelope.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param messageType   a message type
     * @param message       an object value to be sent
     * @throws ApplicationException when error occured.
     * @see #send(IContext, MessageEnvelope) 
     */
    void sendAsObject(IContext context, String messageType, Object message) throws ApplicationException;

    /**
     * Peeks a single incoming message from the queue without removing it. If there
     * are no messages available in the queue it returns null.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @return a message envelop object.
     * @throws ApplicationException when error occured.
     */
    MessageEnvelope peek(IContext context) throws ApplicationException;

    /**
     * Peeks multiple incoming messages from the queue without removing them. If
     * there are no messages available in the queue it returns an empty list.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param messageCount  a maximum number of messages to peek.
     * @return a list with messages.
     * @throws ApplicationException when error occured.
     */
    List<MessageEnvelope> peekBatch(IContext context, int messageCount) throws ApplicationException;

    /**
     * Receives an incoming message and removes it from the queue.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param waitTimeout   a timeout in milliseconds to wait for a message to come.
     * @return a message envelop object.
     * @throws ApplicationException when error occured.
     */
    MessageEnvelope receive(IContext context, long waitTimeout) throws ApplicationException;

    /**
     * Renews a lock on a message that makes it invisible from other receivers in
     * the queue. This method is usually used to extend the message processing time.
     *
     * @param message     a message to extend its lock.
     * @param lockTimeout a locking timeout in milliseconds.
     * @throws ApplicationException when error occured.
     */
    void renewLock(MessageEnvelope message, long lockTimeout) throws ApplicationException;

    /**
     * Permanently removes a message from the queue. This method is usually used to
     * remove the message after successful processing.
     *
     * @param message a message to remove.
     * @throws ApplicationException when error occured.
     */
    void complete(MessageEnvelope message) throws ApplicationException;

    /**
     * Returns message into the queue and makes it available for all subscribers to
     * receive it again. This method is usually used to return a message which could
     * not be processed at the moment to repeat the attempt. Messages that cause
     * unrecoverable errors shall be removed permanently or/and send to dead letter
     * queue.
     *
     * @param message a message to return.
     * @throws ApplicationException when error occured.
     */
    void abandon(MessageEnvelope message) throws ApplicationException;

    /**
     * Permanently removes a message from the queue and sends it to dead letter
     * queue.
     *
     * @param message a message to be removed.
     * @throws ApplicationException when error occured.
     */
    void moveToDeadLetter(MessageEnvelope message) throws ApplicationException;

    /**
     * Listens for incoming messages and blocks the current thread until queue is
     * closed.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param receiver      a receiver to receive incoming messages.
     * @throws ApplicationException when error occured.
     * @see IMessageReceiver
     * @see #receive(IContext, long) 
     */
    void listen(IContext context, IMessageReceiver receiver) throws ApplicationException;

    /**
     * Listens for incoming messages without blocking the current thread.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param receiver      a receiver to receive incoming messages.
     * @see #listen(IContext, IMessageReceiver) 
     * @see IMessageReceiver
     */
    void beginListen(IContext context, IMessageReceiver receiver);

    /**
     * Ends listening for incoming messages. When this method is call listen()
     * unblocks the thread and execution continues.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @throws ApplicationException when error occured.
     */
    void endListen(IContext context) throws ApplicationException;
}
