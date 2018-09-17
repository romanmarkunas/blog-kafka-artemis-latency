package com.romanmarkunas.blog.queues.latency.benchmark;

import com.romanmarkunas.blog.queues.latency.Message;
import com.romanmarkunas.blog.queues.latency.kafka.KafkaMessageReceiver;
import com.romanmarkunas.blog.queues.latency.kafka.KafkaMessageSender;
import com.romanmarkunas.blog.queues.latency.kafka.MessageSerde;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

import static java.util.Collections.singletonList;

class KafkaClientsFactory {

    private static final String TOPIC = "latency-test-42";
    private static final String GROUP = "latency-test-group";

    private final String broker;


    KafkaClientsFactory(String broker) {
        this.broker = broker;
    }


    KafkaMessageSender lowLatencySender() {
        Properties props = lowLatencyProducerConfig();
        KafkaProducer<String, Message> producer = new KafkaProducer<>(props);
        return new KafkaMessageSender(producer, TOPIC);
    }

    KafkaMessageSender lowLatencyFaultTolerantSender() {
        Properties props = lowLatencyProducerConfig();
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        KafkaProducer<String, Message> producer = new KafkaProducer<>(props);
        return new KafkaMessageSender(producer, TOPIC);
    }

    KafkaMessageReceiver lowLatencyReceiver() {
        Properties props = lowLatencyConsumerConfig();
        KafkaConsumer<String, Message> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(singletonList(TOPIC));
        return new KafkaMessageReceiver(consumer, TOPIC, 5, false);
    }

    KafkaMessageReceiver lowLatencyFaultTolerantReceiver() {
        Properties props = lowLatencyConsumerConfig();
        KafkaConsumer<String, Message> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(singletonList(TOPIC));
        return new KafkaMessageReceiver(consumer, TOPIC, 5, true);
    }


    private Properties minimalClientConfig() {
        Properties props = new Properties();
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, this.broker);
        return props;
    }

    private Properties lowLatencyProducerConfig() {
        Properties props = minimalClientConfig();
        props.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        props.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                MessageSerde.MessageSerializer.class.getName());
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 1048576);
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "none");
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 0);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 0);
        props.put(ProducerConfig.ACKS_CONFIG, "1");
        return props;
    }

    private Properties lowLatencyConsumerConfig() {
        Properties props = minimalClientConfig();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP);
        props.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        props.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                MessageSerde.MessageDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_uncommitted");

        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 50);
        props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 500);
        return props;
    }
}
