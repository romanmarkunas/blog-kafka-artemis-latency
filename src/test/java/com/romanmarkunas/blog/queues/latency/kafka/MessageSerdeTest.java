package com.romanmarkunas.blog.queues.latency.kafka;

import com.romanmarkunas.blog.queues.latency.Message;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MessageSerdeTest {

    @Test
    public void serializeAndDeserializeBack_sameValueAsSupplied() {
        String testContent = "Hello!";

        Serializer<Message> testSerializer = new MessageSerde().serializer();
        byte[] bytes = testSerializer.serialize("", new Message(testContent));
        Deserializer<Message> testDeserializer = new MessageSerde().deserializer();
        Message pipedMessage = testDeserializer.deserialize("", bytes);

        assertEquals(testContent, pipedMessage.getContent());
    }
}