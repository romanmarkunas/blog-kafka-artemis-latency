package com.romanmarkunas.blog.queues.latency;

public interface MessageSender {
    void sendMessage(Message message);
}
