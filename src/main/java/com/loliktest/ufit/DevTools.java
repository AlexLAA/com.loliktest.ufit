package com.loliktest.ufit;

import com.google.gson.Gson;
import com.loliktest.ufit.logs.LogMessage;
import com.loliktest.ufit.logs.ParsedRequest;
import org.json.JSONObject;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.loliktest.ufit.UFitBrowser.browser;

public class DevTools {

    public Object executeScript(String script, Object... args) {
        return ((JavascriptExecutor) browser().driver()).executeScript(script, args);
    }

    public Object executeAsyncScript(String script, Object... args) {
        return ((JavascriptExecutor) browser().driver()).executeAsyncScript(script, args);
    }


    public List<LogEntry> getConsoleErrors() {
        return browser().driver().manage().logs().get(LogType.BROWSER).getAll();
    }

    public List<LogEntry> getRequests() {
        return browser().driver().manage().logs().get(LogType.PERFORMANCE).getAll();
    }

    private List<LogEntry> getSendRequests(List<LogEntry> entryList) {
        return entryList.stream().filter(l -> l.getMessage().contains("Network.requestWillBeSent\"")).collect(Collectors.toList());
    }

    private List<LogEntry> getReceivedResponses(List<LogEntry> entryList) {
        return entryList.stream().filter(l -> l.getMessage().contains("Network.responseReceived\"")).collect(Collectors.toList());
    }

    public List<LogEntry> getWebSocketRequests(List<LogEntry> entryList) {
        return entryList.stream().filter(logEntry -> logEntry.getMessage().contains("Network.webSocket")).sorted(Comparator.comparing(LogEntry::getTimestamp)).collect(Collectors.toList());
    }

    public List<ParsedRequest> getParsedRequests(List<LogEntry> entryList) {
        List<LogMessage> parsedRequests = getSendRequests(entryList).stream().map(log -> new Gson().fromJson(log.toJson().get("message").toString(), LogMessage.class)).filter(data -> data.getRequestUrl().startsWith("http")).collect(Collectors.toList());
        List<LogMessage> parsedResponses = getReceivedResponses(entryList).stream().map(log -> new Gson().fromJson(log.toJson().get("message").toString(), LogMessage.class)).filter(data -> data.getResponseUrl().startsWith("http")).collect(Collectors.toList());
        List<ParsedRequest> resultList = new ArrayList<>();
        for (LogMessage logMessage : parsedRequests) {
            LogMessage responseData;
            try {
                responseData = parsedResponses.stream().filter(response -> response.getRequestId().equals(logMessage.getRequestId())).findFirst().get();
                ParsedRequest parsedRequest = new ParsedRequest(responseData.getResponseUrl(), logMessage.getRequestMethod(), responseData.getStatusCode(), Long.valueOf(logMessage.getTimestamp()), logMessage.getRequestHeaders(), responseData.getResponseHeaders());
                resultList.add(parsedRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resultList.stream().filter(parsedRequest -> !parsedRequest.statusCode.startsWith("20")).sorted(Comparator.comparing(ParsedRequest::getTime)).collect(Collectors.toList());
    }

    public Map<String, String> getWebSocketsHosts(List<LogEntry> webSocketsLogs) {
        Map<String, String> result = new HashMap<>();
        List<LogEntry> creationLogs = webSocketsLogs.stream().filter(logEntry -> logEntry.getMessage().contains("Network.webSocketCreated")).sorted(Comparator.comparing(LogEntry::getTimestamp)).collect(Collectors.toList());
        creationLogs.forEach(logEntry -> {
            JSONObject messageJSON = new JSONObject(logEntry.getMessage());
            String id = messageJSON.getJSONObject("message").getJSONObject("params").getString("requestId");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(logEntry.getTimestamp()));
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("Europe/Kiev"));
            String name = " Host - " + messageJSON.getJSONObject("message").getJSONObject("params").getString("url") + ", Date - " + sdf.format(calendar.getTime());
            result.put(id, name);
        });
        return result;
    }

    public List<LogEntry> getWebSocketLogByConnectionId(List<LogEntry> webSocketsLogs, String requestId) {
        List<LogEntry> logs = webSocketsLogs.stream().filter(logEntry -> logEntry.getMessage().contains("Network.webSocketFrameSent") || logEntry.getMessage().contains("Network.webSocketFrameReceived")).collect(Collectors.toList());
        return logs.stream().filter(logEntry -> new JSONObject(logEntry.getMessage()).getJSONObject("message")
                .getJSONObject("params").getString("requestId")
                .equals(requestId)).sorted(Comparator.comparing(LogEntry::getTimestamp)).collect(Collectors.toList());
    }

    public Set<Cookie> getCookies() {
        return browser().driver().manage().getCookies();
    }

    public void clearDevTools() {
        getConsoleErrors();
        getRequests();
    }
}