package ru.malygin.helper.senders;

import ru.malygin.helper.model.TaskCallback;

public interface TaskCallbackSender {
    void send(TaskCallback callback);
}
