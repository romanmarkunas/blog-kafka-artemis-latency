package com.romanmarkunas.blog.queues.latency.benchmark;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.SlidingWindowReservoir;
import com.codahale.metrics.Snapshot;
import com.romanmarkunas.blog.queues.latency.Message;
import com.romanmarkunas.blog.queues.latency.MessageReceiver;
import com.romanmarkunas.blog.queues.latency.MessageSender;
import com.romanmarkunas.blog.queues.latency.StringGenerator;

import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class LatencyMeasurement {

    private final int messagesToSend;
    private final MessageSender sender;
    private final MessageReceiver receiver;
    private final StringGenerator generator;
    // TODO - add rate limitation here as well

    private final Histogram latencies;
    private final Executor executor = Executors.newFixedThreadPool(2);
    private final AtomicBoolean executionFinished = new AtomicBoolean(false);


    public LatencyMeasurement(
            int messagesToSend,
            MessageSender sender,
            MessageReceiver receiver,
            StringGenerator generator) {
        this.messagesToSend = messagesToSend;
        this.sender = sender;
        this.receiver = receiver;
        this.generator = generator;
        this.latencies = new Histogram(new SlidingWindowReservoir(messagesToSend));
    }


    public void run() {
        this.executor.execute(send());
        this.executor.execute(receive());
        waitTillFinishedOrInterrupted();
        printResults();
    }


    private Runnable send() {
        return runUntilDoneOrFailed(() -> {
            for (int i = 0; i < this.messagesToSend; i++) {
                Message message = new Message(this.generator.next());
                this.sender.sendMessage(message);
            }
        });
    }

    private Runnable receive() {
        return runUntilDoneOrFailed(() -> {
            int messagesReceived = 0;
            while (messagesReceived < this.messagesToSend) {
                Optional<Message> maybeMessage = this.receiver.receiveMessage();
                if (!maybeMessage.isPresent()) {
                    continue;
                }

                messagesReceived++;
                Message message = maybeMessage.get();

                long latency = System.currentTimeMillis() - message.getCreationTime();
                this.latencies.update(latency);
            }
        });
    }

    private Runnable runUntilDoneOrFailed(Runnable code) {
        return () -> {
            try {
                code.run();
            } finally {
                this.executionFinished.set(true);
            }
        };
    }

    private void waitTillFinishedOrInterrupted() {
        try {
            while (!this.executionFinished.get()) {
                Thread.sleep(100);
            }
        }
        catch (InterruptedException e) {
            this.executionFinished.set(true);
        }
    }

    private void printResults() {
        Snapshot snap = this.latencies.getSnapshot();
        System.out.println("Test complete! Measurements: ");
        System.out.println("Total events  - " + snap.getValues().length);
        System.out.println("99 percentile - " + snap.get99thPercentile());
        System.out.println("75 percentile - " + snap.get75thPercentile());
        System.out.println("Max latency   - " + snap.getMax());
        System.out.println("Avg latency   - " + snap.getMean());
    }
}
