package ru.malygin.helper.senders.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import ru.malygin.helper.config.SearchEngineProperties;
import ru.malygin.helper.model.Notification;
import ru.malygin.helper.senders.NotificationSender;

import java.util.Date;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class DefaultNotificationSender implements NotificationSender {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper mapper;
    private final SearchEngineProperties.Common.Notification nProp;
    @Value(value = "${spring.application.name}")
    private String appName;

    @Override
    public void send(Notification n) {
        prepareMessage(n).ifPresent(
                message -> rabbitTemplate.send(nProp.getExchange(), nProp.getNotificationRoute(), message));
    }

    private Optional<Message> prepareMessage(Notification n) {
        try {
            byte[] body = mapper
                    .writeValueAsString(n)
                    .getBytes();
            return Optional.of(MessageBuilder
                                       .withBody(body)
                                       .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                                       .setTimestamp(new Date(System.currentTimeMillis()))
                                       .setHeader("__TypeId__", "Notification")
                                       .setHeader("app", appName)
                                       .build());
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return Optional.empty();
    }
}
