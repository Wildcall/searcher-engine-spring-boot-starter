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
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import ru.malygin.helper.config.SearchEngineProperties;
import ru.malygin.helper.senders.LogSender;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class DefaultLogSender implements LogSender {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper mapper;
    private final SearchEngineProperties.Common.Log logProperties;
    @Value(value = "${spring.application.name}")
    private String appName;

    @Override
    public void info(String s,
                     Object... o) {
        if (o != null && s != null) {
            String msg = String.format(s, o);
            prepareMessage(msg).ifPresent(
                    message -> rabbitTemplate.send(logProperties.getExchange(), logProperties.getInfoRoute(), message));
        }
    }

    @Override
    public void error(String s,
                      Object... o) {
        if (o != null && s != null) {
            String msg = String.format(s, o);
            prepareMessage(msg).ifPresent(
                    message -> rabbitTemplate.send(logProperties.getExchange(), logProperties.getInfoRoute(), message));
        }
    }

    private Optional<Message> prepareMessage(String msg) {
        try {
            byte[] body = mapper
                    .writeValueAsString(Map.of("message", msg))
                    .getBytes();

            return Optional.of(MessageBuilder
                                       .withBody(body)
                                       .setTimestamp(new Date(System.currentTimeMillis()))
                                       .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                                       .setHeader("app", appName)
                                       .build());
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return Optional.empty();
    }

    @EventListener(ContextRefreshedEvent.class)
    public void onStart(ContextRefreshedEvent event) {
        info("START");
    }

    @EventListener(ContextClosedEvent.class)
    public void onStart(ContextClosedEvent event) {
        info("CLOSE");
    }
}
