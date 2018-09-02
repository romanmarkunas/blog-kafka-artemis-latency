package com.romanmarkunas.blog.queues.latency;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Message {

    private final String content;
    private final long creationTime;


    public Message(String randomString) {
        this.content = randomString;
        this.creationTime = now();
    }

    @JsonCreator
    private Message(
            @JsonProperty("content") String content,
            @JsonProperty("creationTime") long creationTime) {
        this.content = content;
        this.creationTime = creationTime;
    }


    public String getContent() {
        return this.content;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public long timeSinceCreationNs() {
        return now() - this.creationTime;
    }


    private long now() {
        return System.nanoTime();
    }
}
