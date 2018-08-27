package com.romanmarkunas.blog.queues.latency.latency_test;

import com.romanmarkunas.blog.queues.latency.Message;
import com.romanmarkunas.blog.queues.latency.kafka.KafkaMessageReceiver;
import com.romanmarkunas.blog.queues.latency.kafka.KafkaMessageSender;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.Properties;

public class KafkaClients {

    private static final String TOPIC = "latency-test-messages";
    private static final String BROKER = "localhost";

    public static KafkaMessageSender defaultSender() {
        Properties props = minimalConfig();
        KafkaProducer<String, Message> producer = new KafkaProducer<>(props);
        return new KafkaMessageSender(producer, TOPIC);
    }

    public static KafkaMessageReceiver defaultReceiver() {
        Properties props = new Properties();
        props.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, BROKER);
//        props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG);
        KafkaConsumer<String, Message> consumer = new KafkaConsumer<>(props);
        return new KafkaMessageReceiver(consumer, TOPIC);
    }

    public static Properties minimalConfig() {
        Properties props = new Properties();
        props.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, BROKER);
        return props;
    }
}
