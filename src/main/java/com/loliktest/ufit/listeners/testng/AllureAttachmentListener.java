package com.loliktest.ufit.listeners.testng;

import io.qameta.allure.Allure;
import io.qameta.allure.listener.TestLifecycleListener;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.TestResult;

import java.util.stream.Collectors;

import static com.loliktest.ufit.UFitBrowser.browser;

public class AllureAttachmentListener implements TestLifecycleListener {

    public void beforeTestStop(TestResult result) {
        Status status = result.getStatus();
        if (status == Status.FAILED || status == Status.BROKEN) {
            Allure.parameter("Browser: Failed URL", browser().getCurrentUrl());
            browser().getScreenOnFail();
            Allure.addAttachment("Browser: Console Logs",  browser().devTools.getConsoleErrors().stream().map(logEntry -> logEntry.toJson() + "\n").collect(Collectors.joining()));
            Allure.addAttachment("Browser: Cookies", browser().driver().manage().getCookies().stream().map(cookie -> cookie.getName() + " : " + cookie.getValue() + "\n").collect(Collectors.joining()));
            //Allure.addAttachment("Browser: HTML Page", "text/html", browser().driver().getPageSource(), ".html");
        }
    }

}
