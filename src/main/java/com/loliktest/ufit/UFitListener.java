package com.loliktest.ufit;

import com.loliktest.ufit.listeners.BrowserListener;
import com.loliktest.ufit.listeners.ElemListener;
import io.qameta.allure.Allure;
import org.testng.*;

import java.util.HashMap;
import java.util.Map;

public class UFitListener implements ISuiteListener, ITestListener {

    static Map<String, String> testNgParameters = new HashMap<>();

    static String getParameter(String name) {
        return testNgParameters.get(name);
    }

    @Override
    public void onStart(ISuite suite) {
        Browser.setBrowserListener(new BrowserListener());
        Elem.setElemListener(new ElemListener());
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

    @Override
    public void onTestStart(ITestResult result) {
        TestNgThread.setCurrentThread(result);
    }

    @Override
    public void onTestSuccess(ITestResult result) {

    }

    @Override
    public void onTestFailure(ITestResult result) {

    }

    @Override
    public void onTestSkipped(ITestResult result) {

    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {

    }

    @Override
    public void onStart(ITestContext context) {

    }

    @Override
    public void onFinish(ITestContext context) {

    }
}
