package com.loliktest.ufit.browsers;

import com.loliktest.ufit.Browser;
import com.loliktest.ufit.IBrowserConfig;
import com.loliktest.ufit.listeners.BrowserListener;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;

import java.util.logging.Level;

public class MyAwsomeBrowser implements IBrowserConfig {

    @Override
    public WebDriver setupDriver() {
        System.out.println("MY AWSOME BROWSER");
        Browser.setBrowserListener(new BrowserListener());
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.setExperimentalOption("useAutomationExtension", false);
        options.setPageLoadStrategy(PageLoadStrategy.NONE);
        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
        options.setCapability("goog:loggingPrefs", logPrefs);
        WebDriver driver = new ChromeDriver(options);
        return driver;
    }

    @Override
    public String name() {
        return "mobile";
    }

}
