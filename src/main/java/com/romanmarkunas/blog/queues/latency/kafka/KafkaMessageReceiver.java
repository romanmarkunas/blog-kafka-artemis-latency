package com.romanmarkunas.blog.queues.latency.kafka;

import com.romanmarkunas.blog.queues.latency.Message;
import com.romanmarkunas.blog.queues.latency.MessageReceiver;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.MILLIS;

public class KafkaMessageReceiver implements MessageReceiver, AutoCloseable {

    private final Consumer<String, Message> consumer;
    private final String topic;
    private final List<Message> prefetchBuffer = new ArrayList<>();


    public KafkaMessageReceiver(Consumer<String, Message> consumer, String topic) {
        this.consumer = consumer;
        this.topic = topic;
    }


    @Override
    public Optional<Message> receiveMessage() {
        if (this.prefetchBuffer.isEmpty()) {
            ConsumerRecords<String, Message> consumerRecords
                    = this.consumer.poll(Duration.of(10, MILLIS));
            Iterator<ConsumerRecord<String, Message>> recordIterator
                    = consumerRecords.records(this.topic).iterator();

            if (!recordIterator.hasNext()) {
                return Optional.empty();
            }
            else {
                Message returningNow = recordIterator.next().value();
                while (recordIterator.hasNext()) {
                    this.prefetchBuffer.add(recordIterator.next().value());
                }
                return Optional.of(returningNow);
            }
        }
        else {
            return Optional.of(this.prefetchBuffer.remove(0));
        }
    }

    @Override
    public void close() throws Exception {
        this.consumer.close();
    }
}
