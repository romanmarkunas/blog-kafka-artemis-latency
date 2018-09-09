package com.romanmarkunas.blog.queues.latency;

import java.util.HashMap;
import java.util.Map;

public class StringGenerator {

    public static final String ALPHANUM
            = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            + "abcdefghijklmnopqrstuvwxyz"
            + "1234567890";

    private final String charset;
    private final int stringSize;

    private final Map<String, Integer> offsets = new HashMap<>();


    public StringGenerator(String charset, int stringSize) {
        this.charset = charset;
        this.stringSize = stringSize;
    }


    /**
     * Generates string from given charset. Thread-safe, if all accessing
     * threads have unique names
     */
    public String next() {
        String currentThread = Thread.currentThread().getName();
        int offset = getOffsetFor(currentThread);

        StringBuilder stringBuilder = new StringBuilder();
        int moreCharacters = this.stringSize;

        while (moreCharacters > 0) {
            stringBuilder.append(this.charset.charAt(offset));
            moreCharacters--;
            offset = incremented(offset);
        }

        offset = maybeIncrementToAvoidCopies(offset);
        storeOffsetFor(currentThread, offset);

        return stringBuilder.toString();
    }


    private int getOffsetFor(String threadName) {
        Integer offset = this.offsets.get(threadName);
        return offset == null ? 0 : offset;
    }

    private void storeOffsetFor(String threadName, int offset) {
        this.offsets.put(threadName, offset);
    }

    private int incremented(int offset) {
        offset += 1;
        return offset >= this.charset.length() ? 0 : offset;
    }

    private int maybeIncrementToAvoidCopies(int offset) {
        return this.stringSize == this.charset.length()
                ? incremented(offset)
                : offset;
    }
}
