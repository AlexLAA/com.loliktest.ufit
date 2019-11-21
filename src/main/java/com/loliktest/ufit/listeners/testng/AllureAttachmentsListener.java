package com.loliktest.ufit.listeners.testng;

import io.qameta.allure.Allure;
import io.qameta.allure.testng.AllureTestNg;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;
import java.util.UUID;

import static com.loliktest.ufit.UFitBrowser.browser;

public class AllureAttachmentsListener extends AllureTestNg {

    @Override
    public void onConfigurationFailure(ITestResult itr) {
        System.out.println("UFit Allure: onConfigurationFailure");
        final String uuid = UUID.randomUUID().toString();
        final String parentUuid = UUID.randomUUID().toString();
        startTestCase(itr, parentUuid, uuid);
        Allure.addAttachment("(UFit) Browser Screen Failed", new ByteArrayInputStream(((TakesScreenshot) browser().driver()).getScreenshotAs(OutputType.BYTES)));
        stopTestCase(uuid, itr.getThrowable(), getStatus(itr.getThrowable()));
    }


    @Override
    public void onTestFailure(final ITestResult result) {
        System.out.println("UFit Allure: onTestFailure");
        Allure.addAttachment("(UFit) Browser Screen Failed", new ByteArrayInputStream(((TakesScreenshot) browser().driver()).getScreenshotAs(OutputType.BYTES)));
        super.onTestFailure(result);
    }



}
