package com.loliktest.ufit.browser;

import org.openqa.selenium.JavascriptExecutor;

import static com.loliktest.ufit.UFitBrowser.browser;

public class DevTools {

    public String executeScript(String script, Object... args) {
        return (String) ((JavascriptExecutor) browser().driver()).executeScript(script, args);
    }


}
