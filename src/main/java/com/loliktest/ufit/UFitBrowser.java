package com.loliktest.ufit;

import org.openqa.selenium.WebDriver;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class UFitBrowser {

    private static ThreadLocal<List<Browser>> BROWSERS = ThreadLocal.withInitial(CopyOnWriteArrayList::new);
    private static ThreadLocal<Browser> CURRENT_BROWSER = new ThreadLocal<>();
    static List<Browser> runtimeBrowsersList = Collections.synchronizedList(new ArrayList<>());

    private UFitBrowser() {
    }

    public static Browser browser() {
        return BROWSERS.get().isEmpty() ? browser(0) : CURRENT_BROWSER.get();
    }

    public static boolean isBrowserStarted(){
        return !BROWSERS.get().isEmpty();
    }

    public static List<Browser> browsersList() {
        return BROWSERS.get();
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

    public static void quitAllBrowsers() {
        for (Browser browser : runtimeBrowsersList) {
            try {
                browser.quit();
            } catch (Exception e) {
                continue;
            }
        }
    }


    private static Browser registerNewBrowser(IBrowserConfig config) {
        Browser browser = new Browser(config);
        BROWSERS.get().add(browser);
        CURRENT_BROWSER.set(browser);
        runtimeBrowsersList.add(browser);
        return browser;
    }


}
