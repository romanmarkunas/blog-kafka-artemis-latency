package com.romanmarkunas.blog.queues.latency.latency_test;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.SlidingWindowReservoir;
import com.codahale.metrics.Snapshot;
import com.romanmarkunas.blog.queues.latency.Message;
import com.romanmarkunas.blog.queues.latency.MessageReceiver;
import com.romanmarkunas.blog.queues.latency.MessageSender;
import com.romanmarkunas.blog.queues.latency.StringGenerator;
import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.api.core.client.ActiveMQClient;
import org.apache.activemq.artemis.api.core.client.ServerLocator;
import org.apache.activemq.artemis.uri.schema.connector.InVMTransportConfigurationSchema;
import org.junit.Test;

import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class LatencyTest {

    private ServerLocator locator = ActiveMQClient
            .createServerLocatorWithoutHA(
                    new TransportConfiguration(
                            "org.apache.activemq.artemis.core.remoting.impl.invm.InVMConnectorFactory"));

    private MessageSender sender;
    private MessageReceiver receiver;
    private Executor executor = Executors.newFixedThreadPool(2);
    private Histogram latencies = new Histogram(new SlidingWindowReservoir(1_000_000));

    @Test
    public void send_10000_256bytes_unlimitedRate_fastConsumerDurableMessages() throws InterruptedException {
        AtomicBoolean execcutionFinished = new AtomicBoolean(false);
        this.executor.execute(() -> {
            int messagesReceived = 0;
            while (messagesReceived < 10_000) {
                Optional<Message> maybeMessage = this.receiver.receiveMessage();
                if (!maybeMessage.isPresent()) {
                    continue;
                }

                messagesReceived++;
                Message message = maybeMessage.get();

                long latency = System.currentTimeMillis() - message.getCreationTime();
                this.latencies.update(latency);
            }
            execcutionFinished.set(true); // TODO - this should be in finally
        });


        this.executor.execute(() -> {
            StringGenerator generator = new StringGenerator(
                    StringGenerator.ALPHANUM,
                    128);
            for (int i = 0; i < 10_000; i++) {
                Message message = new Message(generator.next());
                this.sender.sendMessage(message);
            }
        });

        while (!execcutionFinished.get()) {
            Thread.sleep(100);
        }
        Snapshot snap = this.latencies.getSnapshot();
        System.out.println("Test complete! Measurements: ");
        System.out.println("99 percentile - " + snap.get99thPercentile());
        System.out.println("75 percentile - " + snap.get75thPercentile());
        System.out.println("Max latency   - " + snap.getMax());
        System.out.println("Avg latency   - " + snap.getMean());
    }
}
