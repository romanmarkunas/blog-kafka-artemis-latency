package com.romanmarkunas.blog.queues.latency.benchmark;

import com.romanmarkunas.blog.queues.latency.StringGenerator;
import org.junit.Ignore;
import org.junit.Test;

public class LatencyBenchmark {

//    private ServerLocator locator = ActiveMQClient
//            .createServerLocatorWithoutHA(
//                    new TransportConfiguration(
//                            "org.apache.activemq.artemis.core.remoting.impl.invm.InVMConnectorFactory"));

    @Test
    @Ignore
    public void send_1000_256bytes_200ps_kafka() {
        new LatencyMeasurement(
                1000,
                200.0,
                KafkaClients.defaultReceiver(),
                KafkaClients.defaultSender(),
                new StringGenerator(StringGenerator.ALPHANUM, 128),
                "Kafka default 200 messages/s"
        ).run();
    }

    @Test
    @Ignore
    public void send_1000_256bytes_200ps_artemis() {
        new LatencyMeasurement(
                1000,
                200.0,
                ArtemisClients.defaultReceiver(),
                ArtemisClients.defaultSender(),
                new StringGenerator(StringGenerator.ALPHANUM, 128),
                "Artemis default 200 messages/s"
        ).run();
    }
}
