package com.loliktest.ufit;

import com.loliktest.ufit.listeners.BrowserListener;
import io.qameta.allure.Allure;
import org.testng.ISuite;
import org.testng.ISuiteListener;

import java.util.HashMap;
import java.util.Map;

public class UFitListener implements ISuiteListener {

    static Map<String, String> testNgParameters = new HashMap<>();

    static String getParameter(String name) {
        return testNgParameters.get(name);
    }

    @Override
    public void onStart(ISuite suite) {
        Browser.setBrowserListener(new BrowserListener());
        testNgParameters.put("browser", suite.getParameter("browser"));
    }

    @Override
    public void onFinish(ISuite suite) {
        for (Browser browser : UFitBrowser.runtimeBrowsersList) {
            try {
                browser.quit();
            } catch (Exception e) {
                continue;
            }
        }
    }
}
