package com.loliktest.ufit.devtools;

import org.openqa.selenium.devtools.v97.log.Log;
import org.openqa.selenium.devtools.v97.log.model.LogEntry;
import org.openqa.selenium.devtools.v97.network.Network;
import org.openqa.selenium.devtools.v97.network.model.RequestWillBeSent;
import org.openqa.selenium.devtools.v97.network.model.ResponseReceived;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class LogsUfit {

    private org.openqa.selenium.devtools.DevTools devTools;

    public LogsUfit(org.openqa.selenium.devtools.DevTools devTools) {
        this.devTools = devTools;
    }

    private final Logger logger = LoggerFactory.getLogger(LogsUfit.class);

    /**
     * Console Logs interceptor
     * @param logHandler - Сonsumer with intercepted LogEntry
     */
    public void addConsoleLogsListener(Consumer<LogEntry> logHandler) {
        devTools.send(Log.enable());
        devTools.addListener(Log.entryAdded(), logHandler);
    }

    /**
     * default log output
     */
    public void addConsoleLogsListener() {
        addConsoleLogsListener(logEntry -> {
            logger.info("log: "+logEntry.getText());
            logger.info("level: "+logEntry.getLevel());
        });
    }

    /**
     * you must disable Log listener manually
     */
    public void disableConsoleLogsListener() {
        devTools.send(Log.disable());
    }

    /**
     * Response interceptor
     * @param responseReceivedPredicate - Predicate to filter intercepted ResponseReceived
     * @param responseMessageConsumer - Сonsumer with intercepted ResponseReceived
     */
    public void logResponse(Predicate<ResponseReceived> responseReceivedPredicate, Consumer<ResponseReceived> responseMessageConsumer) {
        new NetworkUfit(devTools).enableNetwork();
        devTools.addListener(Network.responseReceived(),
                responseReceived -> {
                    if (responseReceivedPredicate.test(responseReceived)) {
                        responseMessageConsumer.accept(responseReceived);
                    }
                });
    }

    /**
     * Response interceptor with default logger Response output
     * @param responseReceivedPredicate - Predicate to filter intercepted ResponseReceived
     */
    public void logResponse(Predicate<ResponseReceived> responseReceivedPredicate) {
        logResponse(responseReceivedPredicate, responseReceived -> {
            StringBuffer responseConsole = new StringBuffer();
            responseConsole.append("[RESPONSE_CODE: " + responseReceived.getResponse().getStatus() + "] " + responseReceived.getResponse().getUrl());
            logger.info(responseConsole.toString());
        });
    }

    /**
     * Response interceptor with default logger Response output without filter
     */
    public void logResponse() {
        logResponse(responseReceived -> true);
    }

    /**
     * Failed response interceptor with default logger Response output
     * @param responseReceivedPredicate - Predicate to filter intercepted ResponseReceived
     */
    public void logFailedResponse(Predicate<ResponseReceived> responseReceivedPredicate) {
        Predicate<ResponseReceived> predicate = responseReceived -> responseReceived.getResponse().getStatus() >= 400;
        logResponse(predicate.and(responseReceivedPredicate));
    }

    /**
     * Request interceptor
     * @param requestWillBeSentPredicate - Predicate to filter intercepted RequestWillBeSent
     * @param requestWillBeSentConsumer - Сonsumer with intercepted RequestWillBeSent
     */
    public void logRequest(Predicate<RequestWillBeSent> requestWillBeSentPredicate, Consumer<RequestWillBeSent> requestWillBeSentConsumer) {
        new NetworkUfit(devTools).enableNetwork();
        devTools.addListener(Network.requestWillBeSent(),
                requestWillBeSent -> {
                    if (requestWillBeSentPredicate.test(requestWillBeSent)) {
                        requestWillBeSentConsumer.accept(requestWillBeSent);
                    }
                });
    }

    /**
     * Request interceptor with default logger Request output
     * @param requestWillBeSentPredicate - Predicate to filter intercepted RequestWillBeSent
     */
    public void logRequest(Predicate<RequestWillBeSent> requestWillBeSentPredicate) {
        logRequest(requestWillBeSentPredicate, requestWillBeSent -> {
            StringBuffer requestConsole = new StringBuffer();
            requestConsole.append("[" + requestWillBeSent.getRequest().getMethod() + "] --> " + requestWillBeSent.getRequest().getUrl());
            requestConsole.append("\n[HEADERS] --> ");
            requestWillBeSent.getRequest().getHeaders().forEach((headerKey, headerValue) -> requestConsole.append("[" + headerKey + " : " + headerValue + "], "));
            requestWillBeSent.getRequest().getPostData().ifPresent(postData -> requestConsole.append("\n[BODY]    --> " + postData));
            requestConsole.append("\n");
            logger.info(requestConsole.toString());
        });
    }

    /**
     * Request interceptor with default logger Request output without filter
     */
    public void logRequest() {
        logRequest(requestWillBeSent -> true);
    }
}
