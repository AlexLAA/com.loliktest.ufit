package com.loliktest.ufit;

import com.loliktest.ufit.listeners.IBrowserListener;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.log4j.Logger;
import org.awaitility.core.ConditionTimeoutException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.loliktest.ufit.UFitBrowser.browser;
import static com.loliktest.ufit.UFitBrowser.getBrowsersList;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

public class Browser {

    private static List<IBrowserListener> listeners = new ArrayList<>();

    public DevTools devTools = new DevTools();
    public BrowserWait wait = new BrowserWait();
    private BrowserSession session = new BrowserSession();
    private static Logger logger = Logger.getLogger(Browser.class);

    Browser(WebDriver driver) {
        listeners.forEach(l -> l.open(this));
        session.driver = driver;
    }

    Browser(IBrowserConfig config) {
        listeners.forEach(l -> l.open(this));
        session.driver = config.setupDriver();
        session.setParameters(config.parameters());
    }

    public BrowserSession getSession() {
        return session;
    }

    public WebDriver driver() {
        return getSession().driver;
    }

    public Actions actions() {
        return new Actions(driver());
    }


    public void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void setBrowserListener(IBrowserListener browserListener) {
        listeners.add(browserListener);
    }

    //BROWSER ACTIONS
    @Step("Open: {0}")
    public void get(String url) {
        listeners.forEach(l -> l.get(url, this));
        driver().get(url);
        wait.pageLoadComplete();
    }

    public void quit() {
        listeners.forEach(l -> l.quite(this));
        getBrowsersList().clear(); // TODO Make for each instance
        driver().quit();
    }

    public String getCurrentUrl() {
        return driver().getCurrentUrl();
    }

    public String getCurrentFrame() {
        return (String) devTools.executeScript("return self.name");
    }

    //SWITCH TO
    @Step
    public WebDriver switchToWindow(int number) {
        return driver().switchTo().window(driver().getWindowHandles().toArray()[number].toString());
    }

    @Step
    public void switchToFrame(Elem iFrameElem) {
        iFrameElem.switchToFrame();
    }

    @Step
    public void switchToParentFrame() {
        driver().switchTo().parentFrame();
    }

    @Step
    public void switchToDefaultContent() {
        driver().switchTo().defaultContent();
    }

    //SCREENSHOTS
    public byte[] getScreenOnFail() {
        if (getSession().getFailId() != TestNgThread.currentThread().getResult().hashCode()) {
            getSession().setFailedScreen(makeScreenshot(OutputType.BYTES));
            getSession().setFailId(TestNgThread.currentThread().getResult().hashCode());
            Allure.addAttachment("Browser: Screen Failed", new ByteArrayInputStream(getSession().getFailedScreen()));
        }
        return getSession().getFailedScreen();
    }

    public <T> T makeScreenshot(OutputType<T> target) {
        return ((TakesScreenshot) browser().driver()).getScreenshotAs(target);
    }

    //DEPRECATED
    @Deprecated
    public String getClipboardContent() throws IOException, UnsupportedFlavorException {
        String content = null;
        if (getSession().isSelenoid()) {
            Response response = null;
            try {
                response = new OkHttpClient().newCall(new Request.Builder().url(getSession().getRemoteWebDriverUrl() + "/clipboard/" + getSession().getSessionId()).build()).execute();
                content = response.body().string();
            } catch (IOException e) {
                logger.error("Can't receive SELENOID clipboard content: " + e);
            } finally {
                response.close();
            }
        } else {
            content = Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor).toString();
        }
        return content;
    }

    @Deprecated
    public String getSelenoidLink(String fileName) throws IOException {
        String downloadLink = getSession().getRemoteWebDriverUrl() + "/download/" + getSession().getSessionId() + "/" + fileName.replace(" ", "%20");
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(downloadLink).openConnection();
            await().atMost(20, SECONDS)
                    .pollInterval(500, MILLISECONDS)
                    .pollInSameThread()
                    .until(() -> (con.getResponseCode() == HttpURLConnection.HTTP_OK));
        } catch (ConditionTimeoutException e) {
            Assert.fail("File [" + fileName + "] was not found in docker container");
        }
        return downloadLink;
    }


}
