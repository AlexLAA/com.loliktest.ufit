package com.loliktest.ufit.listeners;

import com.loliktest.ufit.Browser;

public interface IBrowserListener {

    void get(String url, Browser browser);

    void quit(Browser browser);

    void beforeOpen(Browser browser);

    void afterOpen(Browser browser);

}
