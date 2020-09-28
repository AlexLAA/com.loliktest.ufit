package com.loliktest.ufit.logs;

import java.util.Map;

public class LogMessage {

    public Message message;

    public String getRequestId() {
        return message.params.requestId;
    }

    public String getRequestUrl() {
        return message.params.request.url;
    }

    public String getResponseUrl() {
        return message.params.response.url;
    }

    public String getRequestMethod() {
        return message.params.request.method;
    }

    public String getType() {
        return message.params.type;
    }

    public String getStatusCode() {
        return message.params.response.status;
    }

    public Map<String, String> getHeaders() {
        return message.params.request.headers;
    }

    public String getTimestamp() {
        return message.params.wallTime.split("\\.")[0];
    }

    public int getTime() {
        return (int) (message.params.response.timing.receiveHeadersEnd * 1000) / 100;
    }
}
