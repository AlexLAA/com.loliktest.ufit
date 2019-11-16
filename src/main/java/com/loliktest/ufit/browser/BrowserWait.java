package com.loliktest.ufit.browser;

import com.loliktest.ufit.Timeout;
import io.qameta.allure.Step;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.WebDriverWait;

import static com.loliktest.ufit.UFitBrowser.browser;

public class BrowserWait {

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


}
