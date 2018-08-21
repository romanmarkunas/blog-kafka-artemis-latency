package com.romanmarkunas.blog.queues.latency.kafka;

import com.romanmarkunas.blog.queues.latency.Message;
import com.romanmarkunas.blog.queues.latency.MessageSender;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.concurrent.TimeUnit;

public class KafkaMessageSender implements MessageSender, AutoCloseable {

    private final Producer<String, Message> producer;
    private final String topic;

    // TODO - test these 2 guys
    public KafkaMessageSender(Producer<String, Message> producer, String topic) {
        this.producer = producer;
        this.topic = topic;
    }


    @Override
    public void sendMessage(Message message) {
        ProducerRecord<String, Message> record
                = new ProducerRecord<>(this.topic, "", message);
        this.producer.send(record);
        this.producer.flush();
    }

    @Override
    public void close() {
        this.producer.close(2, TimeUnit.SECONDS);
    }
}
