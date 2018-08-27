package com.romanmarkunas.blog.queues.latency.benchmark;

import com.romanmarkunas.blog.queues.latency.Message;
import com.romanmarkunas.blog.queues.latency.kafka.KafkaMessageReceiver;
import com.romanmarkunas.blog.queues.latency.kafka.KafkaMessageSender;
import com.romanmarkunas.blog.queues.latency.kafka.MessageSerde;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Properties;

import static java.util.Collections.singletonList;

public class ArtemisClients {

    public static void

    private ArtemisClients() {}

    private static final String TOPIC = "latency-test";
    private static final String GROUP = "latency-test-group";
    private static final String BROKER = "localhost";

    public static KafkaMessageSender defaultSender() {
        Properties props = minimalConfig();
        KafkaProducer<String, Message> producer = new KafkaProducer<>(props);
        return new KafkaMessageSender(producer, TOPIC);
    }

    public static KafkaMessageReceiver defaultReceiver() {
        Properties props = new Properties();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP);
        props.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        props.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                MessageSerde.MessageDeserializer.class.getName());
        KafkaConsumer<String, Message> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(singletonList(TOPIC));
        return new KafkaMessageReceiver(consumer, TOPIC);
    }

    public static Properties minimalConfig() {
        Properties props = new Properties();
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, BROKER);
        return props;
    }
}
