package com.romanmarkunas.blog.queues.latency.artemis;

import com.romanmarkunas.blog.queues.latency.Message;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MessageSerdeTest {

    @Test
    public void serializeAndDeserializeBack_sameValueAsSupplied() {
        String testContent = "Hello!";
        MessageSerde.MessageSerializer testSerializer
                = new MessageSerde.MessageSerializer();
        MessageSerde.MessageDeserializer testDeserializer
                = new MessageSerde.MessageDeserializer();

        String data = testSerializer.serialize(new Message(testContent));
        Message pipedMessage = testDeserializer.deserialize(data);

        assertEquals(testContent, pipedMessage.getContent());
    }
}