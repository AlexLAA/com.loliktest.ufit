package com.loliktest.ufit;

import com.loliktest.ufit.browser.BrowserSession;
import com.loliktest.ufit.browser.BrowserWait;
import com.loliktest.ufit.browser.DevTools;
import com.loliktest.ufit.listeners.IBrowserListener;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;

public class Browser {

    private List<IBrowserListener> listeners = new ArrayList<>();

    public DevTools devTools = new DevTools();
    public BrowserWait wait = new BrowserWait();
    private BrowserSession session = new BrowserSession();

    Browser(WebDriver driver) {
        session.driver = driver;
    }


    public WebDriver driver() {
        return session.driver;
    }

    public void get(String url) {
        listeners.forEach(l -> l.get(url, this));
        driver().get(url);
        wait.pageLoadComplete();
    }

    public void quit() {
        listeners.forEach(l -> l.quite(this));
        driver().quit();
        System.out.println("QUITE");
    }

    public String getCurrentUrl() {
        return driver().getCurrentUrl();
    }

    public void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
