package ru.malygin.helper.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.util.Assert;
import ru.malygin.helper.enums.TaskAction;
import ru.malygin.helper.model.NodeTask;
import ru.malygin.helper.senders.LogSender;

@Slf4j
@RequiredArgsConstructor
public class DefaultTaskReceiver<T extends NodeTask> implements TaskReceiver<T> {

    private final LogSender logSender;
    private final NodeMainService<T> nodeMainService;

    @Override
    @RabbitListener(queues = "#{properties.getCommon().getTask().getRoute()}")
    public void receiveTask(T t,
                            @Header("action") String action) {
        Assert.notNull(t, "Task map must not be null");
        Assert.notNull(action, "Task action header must not be null");
        try {
            TaskAction taskAction = TaskAction.valueOf(action);
            process(t, taskAction);
        } catch (IllegalArgumentException e) {
            log.error("Task action not available: {}", action);
        }
    }

    private void process(T t,
                         TaskAction action) {
        logSender.info("TASK RECEIVE / Id: %s / Action: %s", t.getId(), action.name());
        if (action.equals(TaskAction.START)) {
            nodeMainService.start(t);
        }
        if (action.equals(TaskAction.STOP)) {
            nodeMainService.stop(t);
        }
    }
}
