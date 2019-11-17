package com.loliktest.ufit.browser;

import com.loliktest.ufit.Timeout;
import io.qameta.allure.Step;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
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

    public WebDriverWait driverWait(long seconds) {
        return new WebDriverWait(browser().driver(), seconds);
    }

    public WebDriverWait driverWait() {
        return driverWait(Timeout.getDefault());
    }

    @Step
    public void pageLoadComplete() {
        try {
            driverWait().until(d -> {
                String state = ((JavascriptExecutor) d).executeScript("return document.readyState").toString();
                return state.equalsIgnoreCase("complete");
            });
        } catch (TimeoutException e) {
            browser().devTools.executeScript("window.stop()");
        }
    }

    public boolean isUrlContains(String urlPart, long timeout){
        return until(ExpectedConditions.urlContains(urlPart), timeout);
    }

    public boolean isUrlContains(String urlPart){
        return isUrlContains(urlPart, Timeout.getDefault());
    }

    public boolean isUrlEquals(String url, long timeout){
        return until(ExpectedConditions.urlToBe(url), timeout);
    }

    public boolean isUrlEquals(String url){
        return isUrlEquals(url, Timeout.getDefault());
    }

    public <V> boolean is(Function<? super WebDriver, V> isTrue, long timeout){
        return until(isTrue, timeout);
    }

    public <V> boolean is(Function<? super WebDriver, V> isTrue){
        return is(isTrue, Timeout.getDefault());
    }

    public BrowserWait assertion(String message){
        assertNextCondition = true;
        assertMessage = message;
        return this;
    }

    public BrowserWait assertion(){
        return assertion(null);
    }


    private <V> boolean until(Function<? super WebDriver, V> isTrue, long timeout) {
        try {
            driverWait(timeout).pollingEvery(Duration.ofMillis(200)).until(isTrue);
            return true;
        } catch (TimeoutException e) {
            if(assertNextCondition){
                throw new AssertionError( Optional.ofNullable(assertMessage+" ").orElse("")+"Timeout: "+timeout+" seconds", e.getCause());
            }
            return false;
        } finally {
            assertNextCondition = false;
            assertMessage = null;
        }
    }

}
