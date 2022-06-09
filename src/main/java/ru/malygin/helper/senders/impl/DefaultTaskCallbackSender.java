package ru.malygin.helper.senders.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import ru.malygin.helper.config.SearchEngineProperties;
import ru.malygin.helper.model.TaskCallback;
import ru.malygin.helper.senders.LogSender;
import ru.malygin.helper.senders.TaskCallbackSender;

import java.util.Date;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class DefaultTaskCallbackSender implements TaskCallbackSender {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper mapper;
    private final SearchEngineProperties.Common.Callback cProp;
    private final LogSender logSender;

    @Override
    public void send(TaskCallback callback) {
        logSender.info("TASK CALLBACK SEND / Id: %s / Task state: %s",
                       callback.getTaskId(),
                       callback
                               .getState()
                               .name());
        prepareMessage(callback).ifPresent(
                message -> rabbitTemplate.send(cProp.getExchange(), cProp.getRoute(), message));
    }

    private Optional<Message> prepareMessage(TaskCallback taskCallback) {
        try {
            byte[] body = mapper
                    .writeValueAsString(taskCallback)
                    .getBytes();
            return Optional.of(MessageBuilder
                                       .withBody(body)
                                       .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                                       .setTimestamp(new Date(System.currentTimeMillis()))
                                       .setHeader("__TypeId__", "TaskCallback")
                                       .build());
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return Optional.empty();
    }
}
