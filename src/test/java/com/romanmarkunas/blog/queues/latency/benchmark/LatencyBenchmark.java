package com.romanmarkunas.blog.queues.latency.benchmark;

import com.romanmarkunas.blog.queues.latency.StringGenerator;
import org.junit.Test;

public class LatencyBenchmark {

//    private ServerLocator locator = ActiveMQClient
//            .createServerLocatorWithoutHA(
//                    new TransportConfiguration(
//                            "org.apache.activemq.artemis.core.remoting.impl.invm.InVMConnectorFactory"));

    @Test
    public void send_10000_256bytes_unlimitedRate() {
        new LatencyMeasurement(
                1000,
                KafkaClients.defaultSender(),
                KafkaClients.defaultReceiver(),
                new StringGenerator(StringGenerator.ALPHANUM, 128)
        ).run();
    }
}
