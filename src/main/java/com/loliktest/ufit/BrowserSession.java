package com.loliktest.ufit;

import com.google.common.annotations.Beta;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BrowserSession {

    public WebDriver driver;
    Map<String, Object> parameters = new HashMap<>();
    private int failId;

    void setFailedScreen(byte[] failedScreen) {
        this.failedScreen = failedScreen;
    }

    private byte[] failedScreen;

    void setParameters(Map<String, Object> parameters){
        this.parameters = parameters;
    }

    int getFailId(){
        return failId;
    }

    void setFailId(int failId){
        this.failId = failId;
    }

    public byte[] getFailedScreen() {
        return failedScreen;
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

    public String getParameterAsString(String key){
        return parameters.get(key).toString();
    }

    @Beta
    public String getRemoteWebDriverUrl(){
        return ((HttpCommandExecutor)((RemoteWebDriver) driver).getCommandExecutor()).getAddressOfRemoteServer().toString().replace("/wd/hub", "");
    }

    @Beta
    public String getSessionId() {
        return ((RemoteWebDriver) driver).getSessionId().toString();
    }

    public static QuitPolicy getQuitPolicy() {
        String quitProperty = Optional.ofNullable(UFitListener.getParameter("ufit.quitpolicy"))
                .orElse(Optional.ofNullable(System.getProperty("ufit.quitpolicy"))
                        .orElse("suite"));
        return QuitPolicy.valueOf(quitProperty.toUpperCase());
    }

}
