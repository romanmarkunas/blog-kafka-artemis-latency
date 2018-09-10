package com.romanmarkunas.blog.queues.latency.artemis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.romanmarkunas.blog.queues.latency.Message;
import com.romanmarkunas.blog.queues.latency.MessageSender;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

public class ArtemisMessageSender implements MessageSender, AutoCloseable {

    private static final ObjectMapper mapper = new ObjectMapper();

    private final MessageProducer producer;
    private final Session session;


    public ArtemisMessageSender(MessageProducer producer, Session session) {
        this.producer = producer;
        this.session = session;
    }


    @Override
    public void sendMessage(Message message) {
        try {
            TextMessage artemisMessage = this.session.createTextMessage();
            artemisMessage.setText(mapper.writeValueAsString(message));
            this.producer.send(artemisMessage);
        }
        catch (JMSException | JsonProcessingException e) {
            throw new RuntimeException("Failed to send message!", e);
        }
    }

    @Override
    public void close() throws Exception {
        this.producer.close();
    }
}
