package tests;

import org.testng.annotations.Test;

import static com.loliktest.ufit.UFitBrowser.browser;

public class TestBrowserWaits {


    @Test
    public void isTitleEquals(){
        browser().get("https://google.com");
        browser().wait.assertion().isTitleEquals("Jack Sparrow");
    }




}
