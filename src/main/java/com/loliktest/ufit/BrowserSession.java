package com.loliktest.ufit;

import com.google.common.annotations.Beta;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class BrowserSession {

    public WebDriver driver;
    Map<String, Object> parameters = new HashMap<>();

    void setParameters(Map<String, Object> parameters){
        this.parameters = parameters;
    }

    public boolean isMobile(){
        return parameters.containsKey("isMobile") ? (Boolean) parameters.get("isMobile") : false;
    }

    public boolean isSelenoid(){
        return parameters.containsKey("isSelenoid") ? (Boolean) parameters.get("isSelenoid") : false;
    }

    public boolean is(String key){
        return parameters.containsKey(key) ? (Boolean) parameters.get(key) : false;
    }

    @Beta
    public String getRemoteWebDriverUrl(){
        return ((HttpCommandExecutor)((RemoteWebDriver)((EventFiringWebDriver) driver).getWrappedDriver()).getCommandExecutor()).getAddressOfRemoteServer().toString().replace("/wd/hub", "");
    }

    @Beta
    public String getSessionId() {
        return ((RemoteWebDriver) ((EventFiringWebDriver) driver).getWrappedDriver()).getSessionId().toString();
    }

}
