package ru.malygin.helper.service;

import org.springframework.messaging.handler.annotation.Header;
import ru.malygin.helper.model.NodeTask;

public interface TaskReceiver<T extends NodeTask> {

    void receiveTask(T t,
                     @Header("action") String action);
}
