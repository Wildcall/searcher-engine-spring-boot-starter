package ru.malygin.helper.model.requests;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DataRequest {
    private Long taskId;
    private Long siteId;
    private Long appUserId;
    private String requestExchange;
    private String requestQueue;
    private Long dataCount;
    private String dataQueue;

    public DataRequest(Long taskId,
                       Long siteId,
                       Long appUserId,
                       String requestExchange,
                       String requestQueue) {
        this.taskId = taskId;
        this.siteId = siteId;
        this.appUserId = appUserId;
        this.requestExchange = requestExchange;
        this.requestQueue = requestQueue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataRequest that = (DataRequest) o;

        if (!taskId.equals(that.taskId)) return false;
        if (!requestExchange.equals(that.requestExchange)) return false;
        return requestQueue.equals(that.requestQueue);
    }

    @Override
    public int hashCode() {
        int result = taskId.hashCode();
        result = 31 * result + requestExchange.hashCode();
        result = 31 * result + requestQueue.hashCode();
        return result;
    }
}
