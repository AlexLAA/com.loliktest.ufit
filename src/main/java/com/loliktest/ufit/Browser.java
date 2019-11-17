package com.loliktest.ufit;

import com.loliktest.ufit.listeners.IBrowserListener;
import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;

public class Browser {

    private static List<IBrowserListener> listeners = new ArrayList<>();

    public DevTools devTools = new DevTools();
    public BrowserWait wait = new BrowserWait();
    private BrowserSession session = new BrowserSession();

    Browser(WebDriver driver) {
        listeners.forEach(l -> l.open(this));
        session.driver = driver;
    }


    public WebDriver driver() {
        return session.driver;
    }

    public void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void setBrowserListener(IBrowserListener browserListener) {
        listeners.add(browserListener);
    }

    //BROWSER ACTIONS

    public void get(String url) {
        listeners.forEach(l -> l.get(url, this));
        driver().get(url);
        wait.pageLoadComplete();
    }

    public void quit() {
        listeners.forEach(l -> l.quite(this));
        driver().quit();
    }

    public String getCurrentUrl() {
        return driver().getCurrentUrl();
    }

    @Step
    public WebDriver switchToWindow(int number) {
        return driver().switchTo()
                .window(driver().getWindowHandles().toArray()[number].toString());
    }


}
