package com.loliktest.ufit;

import io.qameta.allure.Step;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Function;

import static com.loliktest.ufit.UFitBrowser.browser;

public class BrowserWait {

    private boolean assertNextCondition;
    private String assertMessage;

    WebDriverWait driverWait(long seconds) {
        return new WebDriverWait(browser().driver(), seconds);
    }

    WebDriverWait driverWait() {
        return driverWait(Timeout.getDefault());
    }

    @Step
    public void pageLoadComplete() {
        try {
            driverWait().until(d ->
                    ((JavascriptExecutor) d).executeScript("return document.readyState").toString().equalsIgnoreCase("complete")
            );
        } catch (TimeoutException e) {
            browser().devTools.executeScript("window.stop()");
        }
    }

    public boolean isUrlContains(String urlPart, long timeout) {
        if (assertMessage == null) assertMessage = "URL NOT CONTAINS: " + urlPart;
        return until(ExpectedConditions.urlContains(urlPart), timeout);
    }

    public boolean isUrlContains(String urlPart) {
        return isUrlContains(urlPart, Timeout.getDefault());
    }

    public boolean isUrlEquals(String url, long timeout) {
        if (assertMessage == null) assertMessage = "URL NOT EQUALS: " + url;
        return until(ExpectedConditions.urlToBe(url), timeout);
    }

    public boolean isUrlEquals(String url) {
        return isUrlEquals(url, Timeout.getDefault());
    }

    public boolean isNumberOfWindowsToBe(int count, long timeout) {
        return until(ExpectedConditions.numberOfWindowsToBe(count), timeout);
    }

    public boolean isNumberOfWindowsToBe(int count){
        return isNumberOfWindowsToBe(count, Timeout.getDefault());
    }

    public void javaScriptReturnStringContains(String script, String expected, int timeout) {
        driverWait(timeout)
                .ignoring(JavascriptException.class)
                .ignoring(NullPointerException.class)
                .until(d -> ((JavascriptExecutor) d).executeScript(script).toString().contains(expected));
    }

    public <V> boolean is(Function<? super WebDriver, V> isTrue, long timeout) {
        return until(isTrue, timeout);
    }

    public <V> boolean is(Function<? super WebDriver, V> isTrue) {
        return is(isTrue, Timeout.getDefault());
    }

    public BrowserWait assertion(String message) {
        assertNextCondition = true;
        assertMessage = message;
        return this;
    }

    public BrowserWait assertion() {
        return assertion(null);
    }


    private <V> boolean until(Function<? super WebDriver, V> isTrue, long timeout) {
        try {
            driverWait(timeout).pollingEvery(Duration.ofMillis(200)).until(isTrue);
            return true;
        } catch (TimeoutException e) {
            if (assertNextCondition) {
                throw new AssertionError(Optional.ofNullable(assertMessage + " ").orElse("") + "Timeout: " + timeout + " seconds. Current URL: " + browser().getCurrentUrl(), e.getCause());
            }
            return false;
        } finally {
            assertNextCondition = false;
            assertMessage = null;
        }
    }

}
