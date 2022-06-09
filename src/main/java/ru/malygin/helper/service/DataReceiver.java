package ru.malygin.helper.service;

import reactor.core.publisher.Flux;
import ru.malygin.helper.model.requests.DataRequest;
import ru.malygin.helper.senders.LogSender;

public interface DataReceiver {

    boolean requestData(DataRequest dataRequest);

    <T> Flux<T> receiveData(DataRequest dataRequest,
                            Class<T> aClass);

    DataRequest createPageDataRequest(Long taskId,
                                      Long siteId,
                                      Long appUserId);

    DataRequest createLemmaDataRequest(Long taskId,
                                       Long siteId,
                                       Long appUserId);

    DataRequest createIndexDataRequest(Long taskId,
                                       Long siteId,
                                       Long appUserId);

    default void sendDataRequestLog(LogSender logSender,
                                    DataRequest dataRequest) {
        logSender.info(
                "DATA REQUEST / Task id: %s / Site id: %s / AppUser id: %s / Request: %s / Response: %s",
                dataRequest.getTaskId(),
                dataRequest.getSiteId(),
                dataRequest.getAppUserId(),
                dataRequest.getRequestQueue(),
                dataRequest.getDataQueue());
    }

    default void dataResponseCountLog(LogSender logSender,
                                      DataRequest dataRequest) {
        logSender.info("DATA RESPONSE / Task id: %s / Count: %s / Request: %s / Response: %s",
                       dataRequest.getTaskId(),
                       dataRequest.getDataCount(),
                       dataRequest.getRequestQueue(),
                       dataRequest.getDataQueue());
    }

    default void dataResponseEmptyCountLog(LogSender logSender,
                                           DataRequest dataRequest) {
        logSender.info("DATA RESPONSE EMPTY / Task id: %s / Request: %s / Response: %s",
                       dataRequest.getTaskId(),
                       dataRequest.getRequestQueue(),
                       dataRequest.getDataQueue());
    }

    default void dataReceiveLog(LogSender logSender,
                                DataRequest dataRequest) {
        logSender.info("DATA RECEIVE / Task id: %s / Count: %s / Request: %s / Response: %s",
                       dataRequest.getTaskId(),
                       dataRequest.getDataCount(),
                       dataRequest.getRequestQueue(),
                       dataRequest.getDataQueue());
    }
}
