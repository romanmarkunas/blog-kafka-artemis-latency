package com.romanmarkunas.blog.queues.latency.artemis;

import com.romanmarkunas.blog.queues.latency.Message;
import com.romanmarkunas.blog.queues.latency.MessageSender;
import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.apache.activemq.artemis.api.core.client.ClientProducer;
import org.apache.activemq.artemis.api.core.client.ClientSession;

public class ArtemisMessageSender implements MessageSender, AutoCloseable {

    private final ClientProducer producer;
    private final ClientSession session;
    private final MessageSerde.MessageSerializer serializer;


    public ArtemisMessageSender(
            ClientProducer producer,
            ClientSession session,
            MessageSerde.MessageSerializer serializer) {
        this.producer = producer;
        this.session = session;
        this.serializer = serializer;
    }


    @Override
    public void sendMessage(Message message) {
        ClientMessage artemisMessage = this.session.createMessage(true);
        this.serializer.storeToBuffer(message, artemisMessage);
        try {
            this.producer.send(artemisMessage);
        }
        catch (ActiveMQException e) {
            throw new RuntimeException("Failed to send message!", e);
        }
    }

    @Override
    public void close() throws Exception {
        this.producer.close();
    }
}
