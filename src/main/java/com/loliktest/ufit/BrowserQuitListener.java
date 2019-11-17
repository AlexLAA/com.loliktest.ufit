package com.loliktest.ufit;

import org.testng.*;

public class BrowserQuitListener implements ISuiteListener {


    @Override
    public void onStart(ISuite suite) {

    }

    @Override
    public void onFinish(ISuite suite) {
        for (Browser browser : UFitBrowser.runtimeBrowsersList) {
            try {
                browser.quit();
            } catch (Exception e){
                continue;
            }
        }
    }
}
