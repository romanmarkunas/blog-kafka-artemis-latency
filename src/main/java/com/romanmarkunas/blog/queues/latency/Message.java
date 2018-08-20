package com.romanmarkunas.blog.queues.latency;

public class Message {

    private final String content;
    private final long creationTime;


    public Message(String randomString) {
        this.content = randomString;
        this.creationTime = System.currentTimeMillis();
    }


    public String getContent() {
        return content;
    }

    public long getCreationTime() {
        return creationTime;
    }
}
