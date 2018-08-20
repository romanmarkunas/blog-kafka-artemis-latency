package com.romanmarkunas.blog.queues.latency;

import org.junit.Test;

import static com.romanmarkunas.blog.queues.latency.StringGenerator.ALPHANUM;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class StringGeneratorTest {

    @Test
    public void next_nextInCharset_stringSize1() {
        StringGenerator testGenerator = new StringGenerator("AB", 1);
        assertEquals("A", testGenerator.next());
        assertEquals("B", testGenerator.next());
    }

    @Test
    public void next_wrapToCharsetStart_charsetExhausted() {
        StringGenerator testGenerator = new StringGenerator("AB1", 2);
        assertEquals("AB", testGenerator.next());
        assertEquals("1A", testGenerator.next());
    }

    @Test
    public void next_generatedStringsAreNotSame_lengthSameAsCharset() {
        StringGenerator testGenerator = new StringGenerator("AB", 2);
        assertNotEquals(testGenerator.next(), testGenerator.next());
    }

    @Test
    public void next_latencyMeasurement() {
        StringGenerator testGenerator = new StringGenerator(ALPHANUM, 1024);
        for (int i = 0; i < 1000; i++) {
            testGenerator.next();
        }

        long start = System.nanoTime();
        testGenerator.next();
        long stop = System.nanoTime();

        System.out.println(String.format("Generations took %d ns", stop - start));
    }

    @Test
    public void next_emptyString_stringSize0() {
        StringGenerator testGenerator = new StringGenerator("AB", 0);
        assertEquals("", testGenerator.next());
    }
}