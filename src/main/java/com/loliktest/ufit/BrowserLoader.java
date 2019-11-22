package com.loliktest.ufit;

import com.loliktest.ufit.browsers.DefaultLocalBrowser;
import com.loliktest.ufit.exceptions.UFitException;

import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

class BrowserLoader {


    public static IBrowserConfig loadBrowserConfig() {
        return StreamSupport.stream(ServiceLoader.load(IBrowserConfig.class).spliterator(), false)
                .filter(b -> b.getClass().getSimpleName().equals(getParameterBrowser())).findFirst()
                .orElse(new DefaultLocalBrowser());
    }

    /**
     * Get 'browser' property. Priorities: testng.xml parameter -> maven -> default
     * @return
     */
    private static String getParameterBrowser() {
        return Optional.ofNullable(UFitListener.getParameter("browser")) // XML
                .orElse(Optional.ofNullable(System.getProperty("browser")) // MAVEN
                        .orElse("DefaultBrowserConfig")); // DEFAULT
    }



}
