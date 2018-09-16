package com.romanmarkunas.blog.queues.latency.kafka;

import com.romanmarkunas.blog.queues.latency.Message;
import com.romanmarkunas.blog.queues.latency.MessageReceiver;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.time.Duration;
import java.util.*;

import static java.time.temporal.ChronoUnit.MICROS;
import static java.time.temporal.ChronoUnit.MILLIS;

public class KafkaMessageReceiver implements MessageReceiver, AutoCloseable {

    private final Consumer<String, Message> consumer;
    private final String topic;
    private final List<Message> prefetchBuffer = new ArrayList<>();
    private final int timeoutMs;
    private final boolean commitAfterPoll;


    public KafkaMessageReceiver(
            Consumer<String, Message> consumer,
            String topic,
            int timeoutMs,
            boolean commitAfterPoll) {
        this.consumer = consumer;
        this.topic = topic;
        this.timeoutMs = timeoutMs;
        this.commitAfterPoll = commitAfterPoll;
        consumer.poll(Duration.of(2000, MILLIS));
        consumer.seekToEnd(Collections.emptyList());
        consumer.commitSync();
    }


    @Override
    public Optional<Message> receiveMessage() {
        if (this.prefetchBuffer.isEmpty()) {
            ConsumerRecords<String, Message> consumerRecords
                    = this.consumer.poll(Duration.of(this.timeoutMs, MILLIS));
            Iterator<ConsumerRecord<String, Message>> recordIterator
                    = consumerRecords.records(this.topic).iterator();

            if (!recordIterator.hasNext()) {
                return Optional.empty();
            }
            else {
                if (this.commitAfterPoll) {
                    this.consumer.commitSync();
                }

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
