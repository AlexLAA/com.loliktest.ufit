package com.loliktest.ufit;

import com.loliktest.ufit.listeners.IBrowserListener;
import io.qameta.allure.Step;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.awaitility.core.ConditionTimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.testng.Assert;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
        listeners.forEach(l -> l.open(this));
        session.driver = driver;
    }

    Browser(IBrowserConfig config){
        listeners.forEach(l -> l.open(this));
        session.driver = config.setupDriver();
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
        return devTools.executeScript("return self.name");
    }

    @Step
    public WebDriver switchToWindow(int number) {
        return driver().switchTo()
                .window(driver().getWindowHandles().toArray()[number].toString());
    }



    // DEPRECATED

    public String getRemoteSessionId() {
        return ((RemoteWebDriver) ((EventFiringWebDriver) driver()).getWrappedDriver()).getSessionId().toString();
    }

    public String getClipboardContent() throws IOException, UnsupportedFlavorException {
        return getSession().isSelenoid()
                ?
                new OkHttpClient().newCall(new Request.Builder().url("http://3.230.127.230:4444" + "/clipboard/" + getRemoteSessionId()).build()).execute().body().string()
                :
                Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor).toString();
    }

    public String getSelenoidLink(String fileName) throws IOException {
        String downloadLink = "http://3.230.127.230:4444" + "/download/" + getRemoteSessionId() + "/" + fileName.replace(" ", "%20");
        //logger.info("Selenoid download link: " + downloadLink);
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
