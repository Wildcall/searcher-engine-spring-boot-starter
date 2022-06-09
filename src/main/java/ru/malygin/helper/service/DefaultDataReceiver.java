package ru.malygin.helper.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import ru.malygin.helper.config.SearchEngineProperties;
import ru.malygin.helper.model.requests.DataRequest;
import ru.malygin.helper.senders.LogSender;

import java.io.IOException;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@RequiredArgsConstructor
public class DefaultDataReceiver implements DataReceiver {

    private final RabbitTemplate rabbitTemplate;
    private final LogSender logSender;
    private final DefaultTempListenerContainerFactory containerFactory;
    private final ObjectMapper mapper;
    private final SearchEngineProperties properties;
    private final Random random = new Random();
    private final Set<DataRequest> activeRequests = new CopyOnWriteArraySet<>();

    @Override
    public boolean requestData(DataRequest dataRequest) {
        //  @formatter:off
        Assert.notNull(dataRequest, "DataRequest must not be null");

        if (activeRequests.contains(dataRequest))
            return true;
        activeRequests.add(dataRequest);

        dataRequest.setDataQueue(createTempDataQueue(dataRequest));

        DataReceiver.super.sendDataRequestLog(logSender, dataRequest);
        Long count = (Long) rabbitTemplate.convertSendAndReceive(dataRequest.getRequestExchange(),
                                                                 dataRequest.getRequestQueue(),
                                                                 dataRequest);
        if (count == null || count == 0) {
            activeRequests.remove(dataRequest);
            DataReceiver.super.dataResponseEmptyCountLog(logSender, dataRequest);
            return false;
        }

        dataRequest.setDataCount(count);
        DataReceiver.super.dataResponseCountLog(logSender, dataRequest);

        return true;
        //  @formatter:on
    }

    @Override
    public <T> Flux<T> receiveData(DataRequest dataRequest,
                                   Class<T> aClass) {
        Assert.notNull(dataRequest, "DataRequest must not be null");
        Assert.notNull(aClass, "Class must not be null");

        if (!activeRequests.contains(dataRequest)) return Flux.empty();

        activeRequests.remove(dataRequest);
        AtomicLong receivedItemCount = new AtomicLong(0);
        DataReceiver.super.dataReceiveLog(logSender, dataRequest);
        return Flux.create(emmiter -> containerFactory.create(dataRequest.getDataQueue(), message -> {
            try {
                T item = mapper.readValue(message.getBody(), aClass);
                emmiter.next(item);
                receivedItemCount.incrementAndGet();
                if (receivedItemCount.get() == dataRequest.getDataCount()) {
                    emmiter.complete();
                    containerFactory.remove(dataRequest.getDataQueue());
                }
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }));
    }

    @Override
    public DataRequest createPageDataRequest(Long taskId,
                                             Long siteId,
                                             Long appUserId) {
        SearchEngineProperties.Common.Request request = properties
                .getCommon()
                .getRequest();
        return new DataRequest(taskId, siteId, appUserId, request.getExchange(), request.getPageRoute());
    }

    @Override
    public DataRequest createLemmaDataRequest(Long taskId,
                                              Long siteId,
                                              Long appUserId) {
        SearchEngineProperties.Common.Request request = properties
                .getCommon()
                .getRequest();
        return new DataRequest(taskId, siteId, appUserId, request.getExchange(), request.getLemmaRoute());
    }

    @Override
    public DataRequest createIndexDataRequest(Long taskId,
                                              Long siteId,
                                              Long appUserId) {
        SearchEngineProperties.Common.Request request = properties
                .getCommon()
                .getRequest();
        return new DataRequest(taskId, siteId, appUserId, request.getExchange(), request.getIndexRoute());
    }

    private String createTempDataQueue(DataRequest dataRequest) {
        return String.format("data-response-queue.%s.%s.%s.%s", dataRequest.getTaskId(), dataRequest.getSiteId(),
                             dataRequest.getAppUserId(), random.nextLong());
    }
}
