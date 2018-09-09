package com.romanmarkunas.blog.queues.latency.benchmark;

import com.codahale.metrics.*;
import com.romanmarkunas.blog.queues.latency.*;

import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.String.format;
import static java.util.concurrent.Executors.newScheduledThreadPool;

public class LatencyMeasurement {

    private final int messagesToSend;
    private final double ratePerSecond;
    private final MessageSender sender;
    private final MessageReceiver receiver;
    private final StringGenerator generator;
    private final String label;

    private final Histogram latencies;
    private final ScheduledExecutorService producerPool = newScheduledThreadPool(1);

    // not volatile because it's initialized before other threads ever access it
    private Meter producerRateMeter;


    public LatencyMeasurement(
            int messagesToSend,
            double ratePerSecond,
            MessageReceiver receiver,
            MessageSender sender,
            StringGenerator generator,
            String label) {
        if (ratePerSecond < 0.5 || ratePerSecond > 20001.0) {
            throw new IllegalArgumentException(
                    "Measurement code was not designed for rates below 0,5Hz "
                  + "and above 20KHz. Please change code and remove this check"
                  + "to test with such unsupported rates.");
        }

        this.messagesToSend = messagesToSend;
        this.sender = sender;
        this.receiver = receiver;
        this.generator = generator;
        this.ratePerSecond = ratePerSecond;
        this.label = label;
        this.latencies = new Histogram(new SlidingWindowReservoir(2 * messagesToSend));
    }


    public void run() {
        // warm-up run
        sendAndReceive((int) (this.messagesToSend * 0.4), false);
        // actual measurement
        this.producerRateMeter = new Meter();
        sendAndReceive(this.messagesToSend, true);
        printResults();
    }


    private void sendAndReceive(int totalMessages, boolean metered) {
        AtomicInteger moreToSend = new AtomicInteger(totalMessages);
        ScheduledFuture future = this.producerPool.scheduleAtFixedRate(
                () -> sendRoutine(moreToSend, metered),
                0,
                nsDelayFromRate(),
                TimeUnit.NANOSECONDS
        );
        receiveRoutine(totalMessages, metered);
        future.cancel(true);
    }

    private void sendRoutine(AtomicInteger moreToSend, boolean metered) {
        if (moreToSend.getAndDecrement() > 0) {
            Message message = new Message(this.generator.next());
            this.sender.sendMessage(message);

            if (metered) {
                this.producerRateMeter.mark();
            }
        }
    }

    private long nsDelayFromRate() {
        return (long) ((1.0d / this.ratePerSecond) * 1_000_000_000);
    }

    private void receiveRoutine(int totalToReceive, boolean metered) {
        int messagesReceived = 0;
        while (messagesReceived < totalToReceive) {
            Optional<Message> maybeMessage = this.receiver.receiveMessage();
            if (!maybeMessage.isPresent()) {
                continue;
            }

            messagesReceived++;

            if (metered) {
                Message message = maybeMessage.get();
                this.latencies.update(message.timeSinceCreationNs());
            }
        }
    }

    private void printResults() {
        Snapshot snap = this.latencies.getSnapshot();
        double toMillis = 1_000_000;

        System.out.println("\n===============================================");
        System.out.println(format("Measurement of %s complete!\n", this.label));

        System.out.println(format("Total sent     - %d", this.producerRateMeter.getCount()));
        System.out.println(format("Total received - %d", snap.getValues().length));
        System.out.println(format("Send rate      - %.3f", this.producerRateMeter.getMeanRate()));
        System.out.println(format("99 percentile  - %.6f", snap.get99thPercentile() / toMillis));
        System.out.println(format("75 percentile  - %.6f", snap.get75thPercentile() / toMillis));
        System.out.println(format("Min latency    - %.6f", snap.getMin() / toMillis));
        System.out.println(format("Max latency    - %.6f", snap.getMax() / toMillis));
        System.out.println(format("Avg latency    - %.6f", snap.getMean() / toMillis));

        System.out.println("===============================================\n");
    }
}
