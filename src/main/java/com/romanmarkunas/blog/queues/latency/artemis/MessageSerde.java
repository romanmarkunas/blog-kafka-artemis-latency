package com.romanmarkunas.blog.queues.latency.artemis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.romanmarkunas.blog.queues.latency.Message;
import org.apache.kafka.common.errors.SerializationException;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.IOException;

public class MessageSerde {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static class MessageSerializer {

        public String serialize(Message data) {
            try {
                return mapper.writeValueAsString(data);
            }
            catch (JsonProcessingException e) {
                throw new SerializationException("Unable to serialize message!", e);
            }
        }
    }

    public static class MessageDeserializer {

        public Message deserialize(TextMessage artemisMessage) {
            try {
                return mapper.readValue(artemisMessage.getText(), Message.class);
            }
            catch (IOException | JMSException e) {
                throw new SerializationException("Unable to deserialize message!", e);
            }
        }
    }
}
