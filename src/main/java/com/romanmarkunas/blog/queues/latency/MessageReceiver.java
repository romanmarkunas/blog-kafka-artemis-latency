package com.romanmarkunas.blog.queues.latency;

import java.util.List;

public interface MessageReceiver {
    List<Message> receiveMessages();
}
