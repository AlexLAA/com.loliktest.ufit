package com.loliktest.ufit;

import com.loliktest.ufit.listeners.BrowserListener;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UFitBrowser {

    private static ThreadLocal<List<Browser>> BROWSERS = ThreadLocal.withInitial(ArrayList::new);
    private static ThreadLocal<Browser> CURRENT_BROWSER = new ThreadLocal<>();
    static List<Browser> runtimeBrowsersList = new ArrayList<>();

    private UFitBrowser() {
    }

    public static Browser browser() {
        return BROWSERS.get().isEmpty() ? browser(0) : CURRENT_BROWSER.get();
    }

    public static Browser browser(WebDriver driver) {
        return registerNewBrowser(new IBrowserConfig() {
            @Override
            public WebDriver setupDriver() {
                return driver;
            }

            @Override
            public Map<String, Object> parameters() {
                return new HashMap<>();
            }
        });
    }

    public static Browser browser(int instance) {
        int size = BROWSERS.get().size();
        if (size <= instance) {
            for (int i = size; i <= instance; i++) {
                registerNewBrowser(BrowserLoader.loadBrowserConfig());
            }
        }
        CURRENT_BROWSER.set(BROWSERS.get().get(instance));
        return browser();
    }

    public static List<Browser> getBrowsersList(){
        return BROWSERS.get();
    }


    private static Browser registerNewBrowser(IBrowserConfig config) {
        Browser browser = new Browser(config);
        BROWSERS.get().add(browser);
        CURRENT_BROWSER.set(browser);
        runtimeBrowsersList.add(browser);
        return browser;
    }


}
