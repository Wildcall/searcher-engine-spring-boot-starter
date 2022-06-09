package ru.malygin.helper.service;

import ru.malygin.helper.model.requests.DataRequest;
import ru.malygin.helper.senders.LogSender;

public interface DataTransceiver {
    Long dataRequestListen(DataRequest dataRequest);

    void send(Long itemCount,
              DataRequest dataRequest);

    default void dataRequestReceiveLog(LogSender logSender,
                                       DataRequest dataRequest) {
        logSender.info("DATA REQUEST RECEIVE / Task id: %s / Site id: %s / AppUser id: %s / Response: %s",
                       dataRequest.getTaskId(), dataRequest.getSiteId(), dataRequest.getAppUserId(),
                       dataRequest.getDataQueue());
    }

    default void dataSendLog(LogSender logSender,
                             DataRequest dataRequest,
                             Long itemCount) {
        logSender.info("DATA SEND / Count: %s / Task id: %s / Queue: %s ",
                       itemCount,
                       dataRequest.getTaskId(),
                       dataRequest.getDataQueue());
    }
}
