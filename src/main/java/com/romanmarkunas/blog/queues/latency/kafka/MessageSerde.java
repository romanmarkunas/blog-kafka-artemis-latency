package com.romanmarkunas.blog.queues.latency.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.romanmarkunas.blog.queues.latency.Message;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.io.IOException;
import java.util.Map;

public class MessageSerde extends EmptyCloseAndConfigure implements Serde<Message> {

    private static final ObjectMapper mapper = new ObjectMapper();


    @Override
    public Serializer<Message> serializer() {
        return new MessageSerializer();
    }

    @Override
    public Deserializer<Message> deserializer() {
        return new MessageDeserializer();
    }


    public static class MessageSerializer extends
            EmptyCloseAndConfigure implements
            Serializer<Message> {
        @Override
        public byte[] serialize(String topic, Message data) {
            try {
                return mapper.writeValueAsBytes(data);
            }
            catch (JsonProcessingException e) {
                throw new SerializationException("Unable to serialize message!", e);
            }
        }
    }

    public static class MessageDeserializer extends
            EmptyCloseAndConfigure implements
            Deserializer<Message> {
        @Override
        public Message deserialize(String topic, byte[] data) {
            try {
                return mapper.readValue(data, Message.class);
            }
            catch (IOException e) {
                throw new SerializationException("Unable to deserialize message!", e);
            }
        }
    }
}

class EmptyCloseAndConfigure {
    public void close() {}
    public void configure(Map<String, ?> configs, boolean isKey) {}
}
