package com.romanmarkunas.blog.queues.latency.benchmark;

import com.romanmarkunas.blog.queues.latency.StringGenerator;
import org.junit.Test;

public class LatencyBenchmark {

//    private ServerLocator locator = ActiveMQClient
//            .createServerLocatorWithoutHA(
//                    new TransportConfiguration(
//                            "org.apache.activemq.artemis.core.remoting.impl.invm.InVMConnectorFactory"));

    @Test
    public void send_10000_256bytes_unlimitedRate_fastConsumerDurableMessages() throws InterruptedException {
        new LatencyMeasurement(
                10_000,
                KafkaClients.defaultSender(),
                KafkaClients.defaultReceiver(),
                new StringGenerator(StringGenerator.ALPHANUM, 128)
        ).run();
    }
}
