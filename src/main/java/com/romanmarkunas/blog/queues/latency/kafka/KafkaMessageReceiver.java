package com.romanmarkunas.blog.queues.latency.kafka;

import com.romanmarkunas.blog.queues.latency.Message;
import com.romanmarkunas.blog.queues.latency.MessageReceiver;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.MILLIS;

public class KafkaMessageReceiver implements MessageReceiver, AutoCloseable {

    private final Consumer<String, Message> consumer;
    private final String topic;


    public KafkaMessageReceiver(Consumer<String, Message> consumer, String topic) {
        this.consumer = consumer;
        this.topic = topic;
    }


    @Override
    public List<Message> receiveMessages() {
        ConsumerRecords<String, Message> consumerRecords
                = this.consumer.poll(Duration.of(1, MILLIS));

        List<Message> messages = new ArrayList<>();
        consumerRecords
                .records(this.topic)
                .forEach(record -> messages.add(record.value()));

        return messages;
    }

    @Override
    public void close() throws Exception {
        this.consumer.close();
    }
}
