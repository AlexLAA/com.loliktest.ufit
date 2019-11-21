package com.loliktest.ufit.listeners.testng;

import io.qameta.allure.Allure;
import io.qameta.allure.listener.TestLifecycleListener;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.TestResult;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;

import static com.loliktest.ufit.UFitBrowser.browser;

public class AllureAttachmentListener implements TestLifecycleListener {

    private Logger logger = LoggerFactory.getLogger(AllureAttachmentListener.class);

    public void beforeTestStop(TestResult result) {
        logger.info("beforeTestStop");
        Status status = result.getStatus();
        if (status == Status.FAILED || status == Status.BROKEN) {
            Allure.addAttachment("(UFit AllureAttachmentListener) Browser Screen Failed", new ByteArrayInputStream(((TakesScreenshot) browser().driver()).getScreenshotAs(OutputType.BYTES)));
        }
    }

}
