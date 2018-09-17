package com.romanmarkunas.blog.queues.latency.benchmark;

import com.romanmarkunas.blog.queues.latency.StringGenerator;
import org.junit.Ignore;
import org.junit.Test;

public class LatencyBenchmark {

    private static final int MESSAGE_SIZE_BYTES = 256;

    private static final StringGenerator generator
            = new StringGenerator(StringGenerator.ALPHANUM, MESSAGE_SIZE_BYTES / 2);

    private static final KafkaClientsFactory kafkaClientsLowLatency
            = new KafkaClientsFactory("localhost:9092");
    private static final ArtemisClientsFactory artemisClientLowLatency
            = new ArtemisClientsFactory("tcp://127.0.0.1:61616");


    @Test
    @Ignore
    public void send_200ps_lowestLatencyPossible_kafka() {
        new LatencyMeasurement(
                5000,
                200.0,
                kafkaClientsLowLatency.lowLatencyReceiver(),
                kafkaClientsLowLatency.lowLatencySender(),
                generator,
                "Kafka low latency 200 messages/s"
        ).run();
    }

    @Test
    @Ignore
    public void send_200ps_commitAfterEach_kafka() {
        new LatencyMeasurement(
                5000,
                200.0,
                kafkaClientsLowLatency.lowLatencyFaultTolerantReceiver(),
                kafkaClientsLowLatency.lowLatencySender(),
                generator,
                "Kafka commit after each message 200 messages/s"
        ).run();
    }

    @Test
    @Ignore
    public void send_4000ps_lowestLatencyPossible_kafka() {
        new LatencyMeasurement(
                50000,
                4000.0,
                kafkaClientsLowLatency.lowLatencyReceiver(),
                kafkaClientsLowLatency.lowLatencySender(),
                generator,
                "Kafka low latency 4000 messages/s"
        ).run();
    }

    @Test
    @Ignore
    public void send_5000_256bytes_200ps_artemis() {
        new LatencyMeasurement(
                5000,
                200.0,
                artemisClientLowLatency.defaultReceiver(),
                artemisClientLowLatency.defaultSender(),
                generator,
                "Artemis default 200 messages/s"
        ).run();
    }
}
