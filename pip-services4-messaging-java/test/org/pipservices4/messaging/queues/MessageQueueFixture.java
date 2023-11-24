package org.pipservices4.messaging.queues;

import org.junit.Test;
import org.pipservices4.commons.errors.ApplicationException;
import org.pipservices4.components.context.Context;
import org.pipservices4.components.context.IContext;
import org.pipservices4.messaging.test.TestMessageReceiver;

import static org.junit.Assert.*;

public class MessageQueueFixture {
    private IMessageQueue _queue;
    private IContext _context = Context.fromTraceId("123");

    public MessageQueueFixture(IMessageQueue queue) {
        _queue = queue;
    }

    public void testSendReceiveMessage() throws Exception {
        MessageEnvelope envelop1 = new MessageEnvelope(_context, "Test", "Test message");
        _queue.send(null, envelop1);

        int count = _queue.readMessageCount();
        assertTrue(count > 0);

        MessageEnvelope envelop2 = _queue.receive(null, 10000);
        assertNotNull(envelop2);
        assertEquals(envelop1.getMessageType(), envelop2.getMessageType());
        assertEquals(envelop1.getMessage(), envelop2.getMessage());
        assertEquals(envelop1.getTraceId(), envelop2.getTraceId());
    }

    public void testReceiveSendMessage() throws Exception {
        MessageEnvelope envelop1 = new MessageEnvelope(_context, "Test", "Test message");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                    _queue.send(null, envelop1);
                } catch (Exception ex) {
                    // Ignore...
                }
            }
        }).start();

        MessageEnvelope envelop2 = _queue.receive(null, 10000);
        assertNotNull(envelop2);
        assertEquals(envelop1.getMessageType(), envelop2.getMessageType());
        assertEquals(envelop1.getMessage(), envelop2.getMessage());
        assertEquals(envelop1.getTraceId(), envelop2.getTraceId());
    }

    public void testMoveToDeadMessage() throws Exception {
        MessageEnvelope envelop1 = new MessageEnvelope(_context, "Test", "Test message");
        _queue.send(null, envelop1);

        MessageEnvelope envelop2 = _queue.receive(null, 10000);
        assertNotNull(envelop2);
        assertEquals(envelop1.getMessageType(), envelop2.getMessageType());
        assertEquals(envelop1.getMessage(), envelop2.getMessage());
        assertEquals(envelop1.getTraceId(), envelop2.getTraceId());

        _queue.moveToDeadLetter(envelop2);
    }

    public void testReceiveAndCompleteMessage() throws Exception {
        MessageEnvelope envelop1 = new MessageEnvelope(_context, "Test", "Test message");
        _queue.send(null, envelop1);

        MessageEnvelope envelop2 = _queue.receive(null, 10000);
        assertNotNull(envelop2);
        assertEquals(envelop1.getMessageType(), envelop2.getMessageType());
        assertEquals(envelop1.getMessage(), envelop2.getMessage());
        assertEquals(envelop1.getTraceId(), envelop2.getTraceId());

        _queue.complete(envelop2);
        //envelop2 = _queue.peek(null);
        //assertNull(envelop2);
    }

    public void testReceiveAndAbandonMessage() throws Exception {
        MessageEnvelope envelop1 = new MessageEnvelope(_context, "Test", "Test message");
        _queue.send(null, envelop1);

        MessageEnvelope envelop2 = _queue.receive(null, 10000);
        assertNotNull(envelop2);
        assertEquals(envelop1.getMessageType(), envelop2.getMessageType());
        assertEquals(envelop1.getMessage(), envelop2.getMessage());
        assertEquals(envelop1.getTraceId(), envelop2.getTraceId());

        _queue.abandon(envelop2);

        envelop2 = _queue.receive(null, 10000);
        assertNotNull(envelop2);
        assertEquals(envelop1.getMessageType(), envelop2.getMessageType());
        assertEquals(envelop1.getMessage(), envelop2.getMessage());
        assertEquals(envelop1.getTraceId(), envelop2.getTraceId());
    }

    public void testSendPeekMessage() throws Exception {
        MessageEnvelope envelop1 = new MessageEnvelope(_context, "Test", "Test message");
        _queue.send(null, envelop1);

        try {
            Thread.sleep(200);
        } catch (InterruptedException ex) {
            // Ignore...
        }

        MessageEnvelope envelop2 = _queue.peek(null);
        assertNotNull(envelop2);
        assertEquals(envelop1.getMessageType(), envelop2.getMessageType());
        assertEquals(envelop1.getMessage(), envelop2.getMessage());
        assertEquals(envelop1.getTraceId(), envelop2.getTraceId());
    }

    public void testPeekNoMessage() throws Exception {
        MessageEnvelope envelop = _queue.peek(null);
        assertNull(envelop);
    }

    public void testOnMessage() throws ApplicationException {
        var messageReceiver = new TestMessageReceiver();
        this._queue.beginListen(null, messageReceiver);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            // Ignore...
        }

        MessageEnvelope envelope1 = new MessageEnvelope(_context, "Test", "Test message");
        this._queue.send(null, envelope1);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            // Ignore...
        }

        var envelope2 = messageReceiver.getMessages().get(0);
        assertNotNull(envelope2);
        assertEquals(envelope1.getMessageType(), envelope2.getMessageType());
        assertEquals(envelope1.getMessage(), envelope2.getMessage());
        assertEquals(envelope1.getTraceId(), envelope2.getTraceId());

        this._queue.endListen(null);
    }

    public void testListen() throws Exception {
        MessageEnvelope envelop1 = new MessageEnvelope(_context, "Test", "Test message");
        MessageEnvelope envelop2 = new MessageEnvelope();

        _queue.beginListen(null, new IMessageReceiver() {
            @Override
            public void receiveMessage(MessageEnvelope envelop, IMessageQueue queue) {
                envelop2.setMessageId(envelop.getMessageId());
                envelop2.setTraceId(envelop.getTraceId());
                envelop2.setMessageType(envelop.getMessageType());
                envelop2.setMessage(envelop.getMessage());
            }
        });

        _queue.send(null, envelop1);

        try {
            Thread.sleep(200);
        } catch (InterruptedException ex) {
            // Ignore...
        }

        assertNotNull(envelop2);
        assertEquals(envelop1.getMessageType(), envelop2.getMessageType());
        assertEquals(envelop1.getMessage(), envelop2.getMessage());
        assertEquals(envelop1.getTraceId(), envelop2.getTraceId());

        _queue.endListen(null);
    }

}
