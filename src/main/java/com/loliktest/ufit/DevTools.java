package com.loliktest.ufit;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

import java.util.List;
import java.util.Set;

import static com.loliktest.ufit.UFitBrowser.browser;

public class DevTools {

    public String executeScript(String script, Object... args) {
        return (String) ((JavascriptExecutor) browser().driver()).executeScript(script, args);
    }

    public String executeAsyncScript(String script, Object... args){
        return (String) ((JavascriptExecutor) browser().driver()).executeAsyncScript(script, args);
    }


    public List<LogEntry> getConsoleErrors(){
        return browser().driver().manage().logs().get(LogType.BROWSER).getAll();
    }

    public List<LogEntry> getRequests(){
        return browser().driver().manage().logs().get(LogType.PERFORMANCE).getAll();
    }
    public Set<Cookie> getCookies(){
        return browser().driver().manage().getCookies();
    }

    public void clearDevTools(){
        getConsoleErrors();
        getRequests();
    }


}
