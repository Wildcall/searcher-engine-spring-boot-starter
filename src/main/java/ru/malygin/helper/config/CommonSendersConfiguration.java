package ru.malygin.helper.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.malygin.helper.senders.LogSender;
import ru.malygin.helper.senders.MetricsSender;
import ru.malygin.helper.senders.NotificationSender;
import ru.malygin.helper.senders.TaskCallbackSender;
import ru.malygin.helper.senders.impl.DefaultLogSender;
import ru.malygin.helper.senders.impl.DefaultMetricsSender;
import ru.malygin.helper.senders.impl.DefaultNotificationSender;
import ru.malygin.helper.senders.impl.DefaultTaskCallbackSender;
import ru.malygin.helper.service.DefaultQueueDeclareService;
import ru.malygin.helper.service.TaskReceiver;

import static ru.malygin.helper.config.SearchEngineProperties.Common.Log;

@Slf4j
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
public class CommonSendersConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "spring.search-engine.common.log", name = "sender", havingValue = "true")
    @ConditionalOnMissingBean
    public LogSender logSender(RabbitTemplate r,
                               ObjectMapper objectMapper,
                               DefaultQueueDeclareService defaultQueueDeclareService,
                               SearchEngineProperties properties) {
        Log logProperties = properties
                .getCommon()
                .getLog();
        defaultQueueDeclareService.declareLogQueue();
        DefaultLogSender defaultLogSender = new DefaultLogSender(r, objectMapper, logProperties);
        log.info("[*] Create DefaultLogSender in starter");
        return defaultLogSender;
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.search-engine.common.notification", name = "sender", havingValue = "true")
    @ConditionalOnMissingBean
    public NotificationSender notificationSender(RabbitTemplate r,
                                                 ObjectMapper m,
                                                 DefaultQueueDeclareService defaultQueueDeclareService,
                                                 SearchEngineProperties properties) {
        SearchEngineProperties.Common.Notification notification = properties
                .getCommon()
                .getNotification();
        defaultQueueDeclareService.declareNotificationQueue();
        DefaultNotificationSender notificationSender = new DefaultNotificationSender(r, m, notification);
        log.info("[*] Create DefaultNotificationSender in starter");
        return notificationSender;
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.search-engine.common.metrics", name = "sender", havingValue = "true")
    @ConditionalOnMissingBean
    public MetricsSender metricsSender(DefaultQueueDeclareService defaultQueueDeclareService,
                                       SearchEngineProperties properties) {
        SearchEngineProperties.Common.Metrics metrics = properties
                .getCommon()
                .getMetrics();
        defaultQueueDeclareService.declareMetricsQueue();
        DefaultMetricsSender defaultMetricsSender = new DefaultMetricsSender(metrics);
        log.info("[*] Create DefaultMetricsSender in starter");
        return defaultMetricsSender;
    }

    @Bean
    @ConditionalOnBean(TaskReceiver.class)
    public TaskCallbackSender taskCallbackSender(RabbitTemplate r,
                                                 ObjectMapper m,
                                                 DefaultQueueDeclareService defaultQueueDeclareService,
                                                 SearchEngineProperties properties,
                                                 LogSender logSender) {
        SearchEngineProperties.Common.Callback callback = properties
                .getCommon()
                .getCallback();
        defaultQueueDeclareService.createQueue(callback.getRoute(), callback.getExchange());
        DefaultTaskCallbackSender defaultCallbackSender = new DefaultTaskCallbackSender(r, m, callback, logSender);
        log.info("[*] Create DefaultTaskCallbackSender in starter");
        return defaultCallbackSender;
    }
}
