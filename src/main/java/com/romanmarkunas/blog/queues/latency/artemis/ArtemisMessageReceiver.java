package com.romanmarkunas.blog.queues.latency.artemis;

import com.romanmarkunas.blog.queues.latency.Message;
import com.romanmarkunas.blog.queues.latency.MessageReceiver;
import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.client.ClientConsumer;
import org.apache.activemq.artemis.api.core.client.ClientMessage;

import java.util.Optional;

public class ArtemisMessageReceiver implements MessageReceiver, AutoCloseable {

    private final ClientConsumer consumer;
    private final MessageSerde.MessageDeserializer deserializer;
    private final MessageSerde.MessageSerializer serializer;


    public ArtemisMessageReceiver(
            ClientConsumer consumer,
            MessageSerde.MessageDeserializer deserializer,
            MessageSerde.MessageSerializer serializer) {
        this.consumer = consumer;
        this.deserializer = deserializer;
        this.serializer = serializer;
    }


    @Override
    public Optional<Message> receiveMessage() {
        ClientMessage artemisMessage;
        try {
            artemisMessage = this.consumer.receive(10);
        }
        catch (ActiveMQException e) {
            throw new RuntimeException("Called receive on closed client!", e);
        }
        return Optional.of(this.deserializer.deserialize(artemisMessage));
    }

    @Override
    public void close() throws Exception {
        this.consumer.close();
    }
}
