package com.loliktest.ufit;

import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
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

    public boolean isUrlMatches(String regex, long timeout) {
        if (assertMessage == null) assertMessage = "URL NOT Matches: " + regex;
        return until(ExpectedConditions.urlMatches(regex), timeout);
    }

    public boolean isUrlMatches(String regex) {
        return isUrlMatches(regex, Timeout.getDefault());
    }


    public boolean isNumberOfWindowsToBe(int count, long timeout) {
        return until(ExpectedConditions.numberOfWindowsToBe(count), timeout);
    }

    public boolean isNumberOfWindowsToBe(int count) {
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

    public boolean iframeWithElementInside(Elem iframe, Elem insideElem, long timeout) {
        return until((ExpectedCondition<Boolean>) driver -> {
            try {
                driver.switchTo().defaultContent();
                driver.switchTo().frame(driver.findElement(iframe.getBy()));
                driver.findElement(insideElem.getBy());
                driver.switchTo().defaultContent();
                return true;
            } catch (NoSuchFrameException | StaleElementReferenceException | NoSuchElementException e) {
                e.printStackTrace();
                return false;
            }
        }, timeout);
    }

    public boolean iframeWithElementInside(Elem iframe, Elem insideElem) {
        return iframeWithElementInside(iframe, insideElem, Timeout.getDefault());
    }

    public boolean elementPresentInWindow(int window, Elem elem, long timeout) {
        return until((ExpectedCondition<Boolean>) driver -> {
            try {
                if (window > driver.getWindowHandles().size() - 1) return false;
                driver.switchTo().window(driver.getWindowHandles().toArray()[window].toString());
                driver.findElement(elem.getBy());
                return true;
            } catch (NoSuchWindowException | NoSuchElementException e) {
                return false;
            } catch (WebDriverException e) {
                if (!e.getMessage().contains("cannot determine loading status")) throw e;
                return false;
            }
        }, timeout);
    }

    public boolean elementPresentInWindow(int window, Elem elem) {
        return elementPresentInWindow(window, elem, Timeout.getDefault());
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
