package org.pipservices4.messaging.queues;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.pipservices4.commons.convert.JsonConverter;
import org.pipservices4.commons.convert.StringConverter;
import org.pipservices4.data.keys.IdGenerator;
import org.pipservices4.components.context.ContextResolver;
import org.pipservices4.components.context.IContext;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Map;

/**
 * Allows adding additional information to messages. A trace id, message id, and a message type
 * are added to the data being sent/received. Additionally, a MessageEnvelope can reference a lock token.
 * <p>
 * Side note: a MessageEnvelope's message is stored as a buffer, so strings are converted
 * using utf8 conversions.
 */
public class MessageEnvelope {
    /**
     * The message's auto-generated ID.
     */
    private String _messageId;
    /**
     * String value that defines the stored message's type.
     */
    private String _messageType;
    /**
     * The unique business transaction id that is used to trace calls across
     * components.
     */
    private String _traceId;
    /**
     * The stored message.
     */
    private Object _message;
    /**
     * The stored reference.
     */
    private Object _reference;
    /**
     * The time at which the message was sent.
     */
    private ZonedDateTime _sentTime;

    /**
     * Creates a new MessageEnvelope.
     */
    public MessageEnvelope() {
    }

    /**
     * Creates a new MessageEnvelope, which adds a trace id, message id, and a
     * type to the data being sent/received.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param messageType   a string value that defines the message's type.
     * @param message       the data being sent/received.
     */
    public MessageEnvelope(IContext context, String messageType, Object message) {
        _traceId = context != null ? ContextResolver.getTraceId(context) : null;
        _messageType = messageType;
        _message = message;
        _messageId = IdGenerator.nextLong();
    }

    /**
     * Creates a new MessageEnvelope, which adds a trace id, message id, and a
     * type to the data being sent/received.
     *
     * @param context     (optional) a context to trace execution through call chain.
     * @param messageType   a string value that defines the message's type.
     * @param message       the data being sent/received.
     */
    public MessageEnvelope(IContext context, String messageType, String message) {
        _traceId = context != null ? ContextResolver.getTraceId(context) : null;
        _messageType = messageType;
        setMessage(message);
        _messageId = IdGenerator.nextLong();
    }

    /**
     * @return the lock token that this MessageEnvelope references.
     */
    @JsonIgnore
    public Object getReference() {
        return _reference;
    }

    /**
     * Sets a lock token reference for this MessageEnvelope.
     *
     * @param value the lock token to reference.
     */
    public void setReference(Object value) {
        _reference = value;
    }

    @JsonProperty("trace_id")
    public String getTraceId() {
        return _traceId;
    }

    public void setTraceId(String value) {
        _traceId = value;
    }

    @JsonProperty("message_id")
    public String getMessageId() {
        return _messageId;
    }

    public void setMessageId(String value) {
        _messageId = value;
    }

    @JsonProperty("message_type")
    public String getMessageType() {
        return _messageType;
    }

    public void setMessageType(String value) {
        _messageType = value;
    }

    @JsonProperty("sent_time")
    public ZonedDateTime getSentTime() {
        return _sentTime;
    }

    public void setSentTime(ZonedDateTime value) {
        _sentTime = value;
    }

    @JsonProperty("message")
    public Object getMessage() {
        return _message;
    }

    public void setMessage(Object value) {
        _message = value;
    }

    /**
     * Stores the given string.
     *
     * @param message the string to set. Will be converted to a buffer, using UTF-8
     *                encoding.
     */
    public void setMessageAsJson(Object message) throws JsonProcessingException {
        _message = JsonConverter.toJson(message);
    }

    /**
     * @return the value that was stored in this message as a JSON string.
     */
    public <T> T getMessageAsJson(Class<T> type) throws IOException {
        return JsonConverter.fromJson(type, _message.toString());
    }

    /**
     * @return the information stored in this message as a UTF-8 encoded string.
     */
    public String getMessageAsString() {
        return this.getMessage() != null ? this.getMessage().toString() : null;
    }

    /**
     * Stores the given string.
     *
     * @param value the string to set. Will be converted to
     *              a buffer, using UTF-8 encoding.
     */
    public void setMessageAsString(String value) {
        this.setMessage(value);
    }

    /**
     * @return the value that was stored in this message
     * as a JSON string.
     * @see [[setMessageAsJson]]
     */
    public <T> T getMessageAs(Class<T> type) throws IOException {
        if (this.getMessage() == null) return null;
        return JsonConverter.fromJson(type, _message.toString());
    }

    /**
     * Stores the given value as a object.
     *
     * @param value the value to convert to JSON and store in
     *              this message.
     * @see [[getMessageAsJson]]
     */
    public void setMessageAsObject(Object value) throws JsonProcessingException {
        if (value == null) {
            this.setMessage(null);
        } else {
            setMessageAsString(JsonConverter.toJson(value));
        }
    }

    /**
     * Convert's this MessageEnvelope to a string, using the following format:
     *
     * <code>"[trace_id, message_type, message.toString]"</code>.
     * <p>
     * If any of the values are <code>null</code>, they will be replaced with
     * <code>---</code>.
     *
     * @return the generated string.
     */
    @Override
    public String toString() {
        return "[" + (_traceId != null ? _traceId : "---") +
                "," + (_messageType != null ? _messageType : "---") + "," +
                (_message != null ? StringConverter.toString(_message) : "--") + "]";
    }

    /**
     * Converts this MessageEnvelop to a JSON string.
     * The message payload is passed as base64 string
     *
     * @return A JSON encoded representation is this object.
     */
    public Map<?, ?> toJSON() {
        var payload = this.getMessage() != null ? this.getMessage().toString() : null;
        return JsonConverter.toMap(payload);
    }

    /**
     * Converts a JSON string into a MessageEnvelop
     * The message payload is passed as base64 string
     *
     * @param value a JSON encoded string
     * @return a decoded Message Envelop.
     */
    public static MessageEnvelope fromJSON(String value) throws IOException {
        if (value == null) return null;

        return JsonConverter.fromJson(MessageEnvelope.class, value);
    }
}
