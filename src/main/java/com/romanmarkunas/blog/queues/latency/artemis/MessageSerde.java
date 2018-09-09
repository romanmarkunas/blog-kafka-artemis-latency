package com.romanmarkunas.blog.queues.latency.artemis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.romanmarkunas.blog.queues.latency.Message;
import org.apache.kafka.common.errors.SerializationException;

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

        public Message deserialize(String artemisMessage) {
            try {
                return mapper.readValue(artemisMessage, Message.class);
            }
            catch (IOException e) {
                throw new SerializationException("Unable to deserialize message!", e);
            }
        }
    }
}
