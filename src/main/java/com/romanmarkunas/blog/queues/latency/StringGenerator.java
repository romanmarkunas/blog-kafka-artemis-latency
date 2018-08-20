package com.romanmarkunas.blog.queues.latency;

public class StringGenerator {

    public static final String ALPHANUM
            = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            + "abcdefghijklmnopqrstuvwxyz"
            + "1234567890";

    private final String charset;
    private final int stringSize;

    private int offset = 0;


    public StringGenerator(String charset, int stringSize) {
        this.charset = charset;
        this.stringSize = stringSize;
    }


    public String next() {
        StringBuilder stringBuilder = new StringBuilder();
        int moreCharacters = this.stringSize;

        while (moreCharacters > 0) {
            stringBuilder.append(this.charset.charAt(this.offset));
            moreCharacters--;
            incrementOffset();
        }

        avoidCharsetCopies();

        return stringBuilder.toString();
    }


    private void incrementOffset() {
        this.offset++;

        if (this.offset >= this.charset.length()) {
            this.offset = 0;
        }
    }

    private void avoidCharsetCopies() {
        if (this.stringSize == this.charset.length()) {
            incrementOffset();
        }
    }
}
