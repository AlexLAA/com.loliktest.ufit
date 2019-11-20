package com.loliktest.ufit;

import org.openqa.selenium.WebDriver;

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

}
