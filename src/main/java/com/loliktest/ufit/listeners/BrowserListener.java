package com.loliktest.ufit.listeners;

import com.loliktest.ufit.Browser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrowserListener implements IBrowserListener {

    private Logger logger = LoggerFactory.getLogger(BrowserListener.class);


    @Override
    public void get(String url, Browser browser) {
        logger.info("Navigate: " + url);
    }

    @Override
    public void quit(Browser browser) {
        logger.info("Close Browser");
    }

    @Override
    public void open(Browser browser) {
        logger.info("Open Browser");
    }

}
