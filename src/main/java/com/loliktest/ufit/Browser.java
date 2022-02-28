package com.loliktest.ufit;

import com.loliktest.ufit.listeners.IBrowserListener;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.awaitility.core.ConditionTimeoutException;
import org.openqa.selenium.*;
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

    Browser(WebDriver driver) {
        listeners.forEach(l -> l.beforeOpen(this));
        session.driver = driver;
        listeners.forEach(l -> l.afterOpen(this));
    }

    Browser(IBrowserConfig config){
        this(config.setupDriver());
        session.setParameters(config.parameters());
    }

    public BrowserSession getSession(){
        return session;
    }

    public WebDriver driver() {
        return getSession().driver;
    }

    public Actions actions(){
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
        listeners.forEach(l -> l.quit(this));
        try {
            getBrowsersList().forEach(b -> b.driver().quit());
        } finally {
            getBrowsersList().clear(); // TODO Make for each instance

        }
    }

    public void deleteAllCookies(){
        driver().manage().deleteAllCookies();
    }

    public String getCurrentUrl() {
        return driver().getCurrentUrl();
    }

    public String getCurrentFrame() {
        return (String) devTools.executeScript("return self.name");
    }

    //SWITCH TO
    public WebDriver switchToWindow(int number) {
        return driver().switchTo().window(driver().getWindowHandles().toArray()[number].toString());
    }

    public void switchToFrame(Elem iFrameElem) {
        iFrameElem.switchToFrame();
    }

    public void switchToParentFrame() {
        driver().switchTo().parentFrame();
    }

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

    public <T> T makeScreenshot(OutputType<T> target){
        return ((TakesScreenshot) browser().driver()).getScreenshotAs(target);
    }

    /**
     * open new window and switch to it
     */
    public void openNewWindow() {
        driver().switchTo().newWindow(WindowType.WINDOW);
    }

    /**
     * open new tab and switch to it
     */
    public void openNewTab() {
        driver().switchTo().newWindow(WindowType.TAB);
    }

    //DEPRECATED -> MOVE TO ANOTHER CLASS
    public String getClipboardContent() throws IOException, UnsupportedFlavorException {
        if (getSession().isSelenoid()) {
            try (Response response = new OkHttpClient().newCall(new Request.Builder().url(getSession().getRemoteWebDriverUrl() + "/clipboard/" + getSession().getSessionId()).build()).execute()) {
                return response.body().string();
            } catch (IOException e) {
                throw new RuntimeException("Can't get clipboard content from selenoid container: " + e);
            }
        } else {
            return Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor).toString();
        }
    }

    //DEPRECATED -> MOVE TO ANOTHER CLASS
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
