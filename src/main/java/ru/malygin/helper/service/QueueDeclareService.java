package ru.malygin.helper.service;

import org.springframework.amqp.core.Queue;

public interface QueueDeclareService {
    Queue createQueue(String queueName,
                      String exchangeName);

    void removeQueue(String queueName);
}
