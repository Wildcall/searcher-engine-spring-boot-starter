package ru.malygin.helper.senders.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.malygin.helper.config.SearchEngineProperties;
import ru.malygin.helper.model.SearchEngineServiceMetrics;
import ru.malygin.helper.senders.MetricsSender;

@Slf4j
@RequiredArgsConstructor
public class DefaultMetricsSender implements MetricsSender {

    private final SearchEngineProperties.Common.Metrics metrics;

    @Override
    public void send(SearchEngineServiceMetrics metrics) {

    }
}
