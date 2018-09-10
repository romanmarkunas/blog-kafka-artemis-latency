package com.romanmarkunas.blog.queues.latency.artemis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.romanmarkunas.blog.queues.latency.Message;
import com.romanmarkunas.blog.queues.latency.MessageReceiver;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.TextMessage;
import java.io.IOException;
import java.util.Optional;

public class ArtemisMessageReceiver implements MessageReceiver, AutoCloseable {

    private static final ObjectMapper mapper = new ObjectMapper();

    private final MessageConsumer consumer;


    public ArtemisMessageReceiver(MessageConsumer consumer) {
        this.consumer = consumer;
    }


    @Override
    public Optional<Message> receiveMessage() {
        try {
            TextMessage artemisMessage = (TextMessage) this.consumer.receive(10);

            if (artemisMessage == null) {
                return Optional.empty();
            }
            else {
                String message = artemisMessage.getText();
                return Optional.of(mapper.readValue(message, Message.class));
            }
        }
        catch (JMSException | IOException e) {
            throw new RuntimeException("Called receive on closed client!", e);
        }
    }

    @Override
    public void close() throws Exception {
        this.consumer.close();
    }
}
