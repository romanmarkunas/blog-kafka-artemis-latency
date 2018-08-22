package com.romanmarkunas.blog.queues.latency;

import java.util.Optional;

public interface MessageReceiver {
    Optional<Message> receiveMessage();
}
