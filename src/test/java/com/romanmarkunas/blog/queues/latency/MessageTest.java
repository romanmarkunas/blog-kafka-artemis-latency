package com.romanmarkunas.blog.queues.latency;

import org.junit.Test;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class MessageTest {

    @Test
    public void getContent_sameAsSuppliedToConstructor() {
        String content = "Fgj1k!B00";
        Message testMessage = new Message(content);
        assertEquals(content, testMessage.getContent());
    }

    @Test
    public void getCreationTime_retrievedFromWallClock() {
        long before = System.nanoTime();
        Message testMessage = new Message("");
        long after = System.nanoTime();
        assertThat(testMessage.getCreationTime(), greaterThanOrEqualTo(before));
        assertThat(testMessage.getCreationTime(), lessThanOrEqualTo(after));
    }

    @Test
    public void timeSinceCreationNs_makesSense() throws InterruptedException {
        long before = System.nanoTime();
        Message testMessage = new Message("");
        Thread.sleep(100);
        long timeSinceCreationNs = testMessage.timeSinceCreationNs();
        long after = System.nanoTime();
        assertThat(timeSinceCreationNs, greaterThan(0L));
        assertThat(timeSinceCreationNs, lessThanOrEqualTo(after - before));
    }
}