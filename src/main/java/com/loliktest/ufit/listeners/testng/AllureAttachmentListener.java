package com.loliktest.ufit.listeners.testng;

import com.loliktest.ufit.UFitBrowser;
import io.qameta.allure.Allure;
import io.qameta.allure.listener.FixtureLifecycleListener;
import io.qameta.allure.listener.TestLifecycleListener;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.TestResult;
import org.json.JSONObject;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.logging.LogEntry;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.loliktest.ufit.UFitBrowser.browser;

public class AllureAttachmentListener implements TestLifecycleListener, FixtureLifecycleListener {

    public void beforeTestStop(TestResult result) {
        if (UFitBrowser.isBrowserStarted()) {
            Status status = result.getStatus();
            if (status == Status.FAILED || status == Status.BROKEN) {
                makeAttachmentsForEachWindow();
            }
        }
    }

    public void makeAttachmentsForEachWindow() {
        browser().driver().getWindowHandles().forEach(w -> {
                    browser().driver().switchTo().window(w);
                    makeAttachments(browser().driver().getTitle());
                }
        );
    }

    public void makeAttachments(String window) {
        Allure.addAttachment("Browser (" + window + "): Screen Failed", new ByteArrayInputStream(browser().makeScreenshot(OutputType.BYTES)));
        try {
            Allure.addAttachment("Browser (" + window + "): Console Logs", browser().devTools.getConsoleErrors().stream().map(logEntry -> logEntry.toJson() + "\n").collect(Collectors.joining()));

            List<LogEntry> createdRequests = browser().devTools.getWebSocketRequests();
            Map<String, String> hosts = browser().devTools.getWebSocketsHosts(createdRequests);
            hosts.forEach((key, value) -> Allure.addAttachment("Browser (" + window + "): WebSocket :" + value, "ID - " + key + " URL - " + value + "\n"
                    + browser().devTools.getWebSocketLogByConnectionId(createdRequests, key)
                    .stream()
                    .map(logEntry -> {
                        JSONObject messageJSON = new JSONObject(logEntry.getMessage());
                        String method = messageJSON.getJSONObject("message").getString("method");
                        String message = "";
                        if (method.equalsIgnoreCase("Network.webSocketFrameSent")) {
                            message = "\n--->  " + messageJSON.getJSONObject("message").getJSONObject("params").getJSONObject("response").getString("payloadData");
                        } else if (method.equalsIgnoreCase("Network.webSocketFrameReceived")) {
                            message = "\n<--- " + messageJSON.getJSONObject("message").getJSONObject("params").getJSONObject("response").getString("payloadData");
                        }
                        return message;
                    }).collect(Collectors.joining())));

        } catch (Exception e) {

        }
        Allure.parameter("Failed URL [Browser (" + window + ")]", browser().getCurrentUrl());
        Allure.addAttachment("Browser (" + window + "): Cookies", browser().driver().manage().getCookies().stream().map(cookie -> cookie.getName() + " : " + cookie.getValue() + "\n").collect(Collectors.joining()));
    }

 /*   public void beforeFixtureStop(FixtureResult result) {
        Status status = result.getStatus();
        if (status == Status.FAILED || status == Status.BROKEN) {
            Allure.parameter("Browser: Failed URL", browser().getCurrentUrl());
            browser().getScreenOnFail();
            attachConsoleErrors();
            Allure.addAttachment("Browser: Cookies", browser().driver().manage().getCookies().stream().map(cookie -> cookie.getName() + " : " + cookie.getValue() + "\n").collect(Collectors.joining()));
        }
    }*/

    public void attachConsoleErrors() {
        try {
            Allure.addAttachment("Browser: Console Logs", browser().devTools.getConsoleErrors().stream().map(logEntry -> logEntry.toJson() + "\n").collect(Collectors.joining()));
        } catch (Exception e) {
        }
    }

    public void attachScreen() {
        Allure.addAttachment("Browser: Screen Failed", new ByteArrayInputStream(browser().makeScreenshot(OutputType.BYTES)));
    }

}
