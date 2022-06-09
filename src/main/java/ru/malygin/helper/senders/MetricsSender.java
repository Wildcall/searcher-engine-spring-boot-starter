package ru.malygin.helper.senders;

import ru.malygin.helper.model.SearchEngineServiceMetrics;

public interface MetricsSender {
    void send(SearchEngineServiceMetrics metrics);
}
