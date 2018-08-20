package com.romanmarkunas.blog.queues.latency;

import org.junit.Test;

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
        long before = System.currentTimeMillis();
        Message testMessage = new Message("");
        long after = System.currentTimeMillis();
        assertThat(testMessage.getCreationTime(), greaterThanOrEqualTo(before));
        assertThat(testMessage.getCreationTime(), lessThanOrEqualTo(after));
    }
}