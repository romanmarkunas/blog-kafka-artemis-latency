package com.romanmarkunas.blog.queues.latency.artemis;

import com.romanmarkunas.blog.queues.latency.Message;
import com.romanmarkunas.blog.queues.latency.MessageReceiver;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.TextMessage;
import java.util.Optional;

public class ArtemisMessageReceiver implements MessageReceiver, AutoCloseable {

    private final MessageConsumer consumer;
    private final MessageSerde.MessageDeserializer deserializer;


    public ArtemisMessageReceiver(
            MessageConsumer consumer,
            MessageSerde.MessageDeserializer deserializer) {
        this.consumer = consumer;
        this.deserializer = deserializer;
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
                return Optional.of(this.deserializer.deserialize(message));
            }
        }
        catch (JMSException e) {
            throw new RuntimeException("Called receive on closed client!", e);
        }
    }

    @Override
    public void close() throws Exception {
        this.consumer.close();
    }
}
