package com.romanmarkunas.blog.queues.latency.artemis;

import com.romanmarkunas.blog.queues.latency.Message;
import com.romanmarkunas.blog.queues.latency.MessageSender;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

public class ArtemisMessageSender implements MessageSender, AutoCloseable {

    private final MessageProducer producer;
    private final Session session;
    private final MessageSerde.MessageSerializer serializer;


    public ArtemisMessageSender(
            MessageProducer producer,
            Session session,
            MessageSerde.MessageSerializer serializer) {
        this.producer = producer;
        this.session = session;
        this.serializer = serializer;
    }


    @Override
    public void sendMessage(Message message) {
        try {
            TextMessage artemisMessage = this.session.createTextMessage();
            artemisMessage.setText(this.serializer.serialize(message));
            this.producer.send(artemisMessage);
        }
        catch (JMSException e) {
            throw new RuntimeException("Failed to send message!", e);
        }
    }

    @Override
    public void close() throws Exception {
        this.producer.close();
    }
}
