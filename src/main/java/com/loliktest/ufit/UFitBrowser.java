package com.loliktest.ufit;

import com.loliktest.ufit.browser.BrowserFactory;
import com.loliktest.ufit.browsers.DefaultLocalBrowser;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;

public class UFitBrowser {

    private static ThreadLocal<List<Browser>> BROWSERS = ThreadLocal.withInitial(ArrayList::new);
    private static ThreadLocal<Browser> CURRENT_BROWSER = new ThreadLocal<>();
    static List<Browser> runtimeBrowsersList = new ArrayList<>();
    private static IBrowserConfig browserConfig = new DefaultLocalBrowser();

    private UFitBrowser() {
    }

    public static Browser browser() {
        return BROWSERS.get().isEmpty() ? browser(0) : CURRENT_BROWSER.get();
    }

    public static Browser browser(WebDriver driver) {
        return registerNewBrowser(driver);
    }

    public static Browser browser(int instance) {
        int size = BROWSERS.get().size();
        if (size <= instance) {
            for (int i = size; i <= instance; i++) {
                registerNewBrowser(browserConfig.setupDriver());
            }
        }
        CURRENT_BROWSER.set(BROWSERS.get().get(instance));
        return browser();
    }


    private static Browser registerNewBrowser(WebDriver driver) {
        Browser browser = new Browser(driver);
        BROWSERS.get().add(browser);
        CURRENT_BROWSER.set(browser);
        runtimeBrowsersList.add(browser);
        return browser;
    }


}
