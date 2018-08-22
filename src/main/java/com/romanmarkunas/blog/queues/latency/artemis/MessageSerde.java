package com.romanmarkunas.blog.queues.latency.artemis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.romanmarkunas.blog.queues.latency.Message;
import org.apache.activemq.artemis.api.core.ActiveMQBuffer;
import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.apache.kafka.common.errors.SerializationException;

import java.io.IOException;

public class MessageSerde {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static class MessageSerializer {

        public void storeToBuffer(Message message, ClientMessage artemisMessage) {
            byte[] binaryMessage;
            try {
                binaryMessage = mapper.writeValueAsBytes(message);
            }
            catch (JsonProcessingException e) {
                throw new SerializationException("Unable to serialize message!", e);
            }
            ActiveMQBuffer bodyBuffer = artemisMessage.getBodyBuffer();
            bodyBuffer.writeBytes(binaryMessage);
        }
    }

    public static class MessageDeserializer {

        public Message deserialize(ClientMessage artemisMessage) {
            ActiveMQBuffer bodyBuffer = artemisMessage.getBodyBuffer();
            int messageSize = bodyBuffer.readableBytes();
            byte[] messageBytes = new byte[messageSize];
            bodyBuffer.readBytes(messageBytes);

            try {
                return mapper.readValue(messageBytes, Message.class);
            }
            catch (IOException e) {
                throw new SerializationException("Unable to deserialize message!", e);
            }
        }
    }
}
