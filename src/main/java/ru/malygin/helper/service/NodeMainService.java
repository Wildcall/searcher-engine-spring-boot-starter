package ru.malygin.helper.service;

import ru.malygin.helper.model.NodeTask;

public interface NodeMainService<T extends NodeTask> {
    void start(T task);

    void stop(T task);
}
