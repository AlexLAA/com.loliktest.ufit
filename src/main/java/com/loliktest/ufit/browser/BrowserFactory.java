package com.loliktest.ufit.browser;

import com.loliktest.ufit.IBrowserConfig;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.support.events.EventFiringWebDriver;

import java.util.logging.Level;

public class BrowserFactory {


    public static IBrowserConfig getDefaultBrowser() {
      return () -> {
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
      };
    }

}
