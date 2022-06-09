package ru.malygin.helper.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;

@Slf4j
@RequiredArgsConstructor
public class DefaultTempListenerContainerFactory {
    private final SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory;
    private final RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry;
    private final DefaultQueueDeclareService declareService;

    public void create(String queueName,
                       MessageListener messageListener) {
        log.info("[+] Create TempMessageListenerContainer for {}", queueName);
        declareService.createQueue(queueName, null);
        SimpleRabbitListenerEndpoint endpoint = new SimpleRabbitListenerEndpoint();
        endpoint.setQueueNames(queueName);
        endpoint.setId(queueName);
        endpoint.setMessageListener(messageListener);
        rabbitListenerEndpointRegistry.registerListenerContainer(endpoint, rabbitListenerContainerFactory, true);
    }

    public void remove(String queueName) {
        MessageListenerContainer container = rabbitListenerEndpointRegistry.unregisterListenerContainer(
                queueName);
        if (container != null) {
            container.stop();
            declareService.deleteQueue(queueName);
            log.info("[-] Remove TempMessageListenerContainer for {}", queueName);
        }
    }
}
