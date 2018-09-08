package com.romanmarkunas.blog.queues.latency.benchmark;

import com.codahale.metrics.*;
import com.romanmarkunas.blog.queues.latency.*;

import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
    private final ScheduledExecutorService producerPool = newScheduledThreadPool(2);

    private volatile Meter producerRateMeter = null;


    public LatencyMeasurement(
            int messagesToSend,
            double ratePerSecond,
            MessageReceiver receiver,
            MessageSender sender,
            StringGenerator generator,
            String label) {
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
        sendAndReceive(200, false);
        // actual measurement
        sendAndReceive(this.messagesToSend, true);
        printResults();
    }


    private void sendAndReceive(int totalMessages, boolean metered) {
        scheduleNextAndSend(totalMessages, metered);
        receiveRoutine(totalMessages, metered);
    }

    private void scheduleNextAndSend(int moreToSend, boolean metered) {
        if (moreToSend > 1) {
            this.producerPool.schedule(
                    () -> scheduleNextAndSend(moreToSend - 1, metered),
                    nsDelayFromRate(),
                    TimeUnit.NANOSECONDS);
        }

        Message message = new Message(this.generator.next());
        this.sender.sendMessage(message);

        if (metered) {
            markProducerRate();
        }

        if (moreToSend <= 1) {
            System.out.println("COUNT: " + this.producerRateMeter.getCount());
            System.out.println("RATE: " + this.producerRateMeter.getMeanRate());
            System.out.println("RATE: " + this.producerRateMeter.getOneMinuteRate());
        }
    }

    private long nsDelayFromRate() {
        return (long) ((1.0d / this.ratePerSecond) * 1_000_000_000);
    }

    private void markProducerRate() {
        if (this.producerRateMeter == null) {
            this.producerRateMeter = new Meter();
        }

        this.producerRateMeter.mark();
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
