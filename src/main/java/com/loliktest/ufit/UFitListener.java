package com.loliktest.ufit;

import com.loliktest.ufit.listeners.BrowserListener;
import com.loliktest.ufit.listeners.ElemListener;
import org.testng.*;

import java.util.HashMap;
import java.util.Map;

import static com.loliktest.ufit.QuitPolicy.*;
import static com.loliktest.ufit.UFitBrowser.browser;

public class UFitListener implements ISuiteListener, ITestListener, IConfigurationListener2, IClassListener {

    static Map<String, String> testNgParameters = new HashMap<>();

    static String getParameter(String name) {
        return testNgParameters.get(name);
    }

    @Override
    public void onStart(ISuite suite) {
        Browser.setBrowserListener(new BrowserListener());
        Elem.setElemListener(new ElemListener());
        testNgParameters.put("browser", suite.getParameter("browser"));
        testNgParameters.put("ufit.quitpolicy", suite.getParameter("ufit.quitpolicy"));
    }

    @Override
    public void onFinish(ISuite suite) {
        UFitBrowser.quitAllBrowsers();
    }

    @Override
    public void onTestStart(ITestResult result) {
        TestNgThread.setCurrentThread(result);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        if (BrowserSession.getQuitPolicy().equals(METHOD)) browser().quit();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        if (BrowserSession.getQuitPolicy().equals(METHOD)) browser().quit();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        if (BrowserSession.getQuitPolicy().equals(METHOD)) browser().quit();
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {

    }

    @Override
    public void onStart(ITestContext context) {

    }

    @Override
    public void onFinish(ITestContext context) {
        if (BrowserSession.getQuitPolicy().equals(TEST)) browser().quit();
    }

    @Override
    public void onConfigurationSuccess(ITestResult result) {

    }

    @Override
    public void onConfigurationFailure(ITestResult result) {

    }

    @Override
    public void onConfigurationSkip(ITestResult result) {

    }

    @Override
    public void beforeConfiguration(ITestResult result) {
        TestNgThread.setCurrentThread(result);
    }

    @Override
    public void onBeforeClass(ITestClass testClass) {

    }

    @Override
    public void onAfterClass(ITestClass testClass) {
        if (BrowserSession.getQuitPolicy().equals(CLASS)) browser().quit();
    }
}
