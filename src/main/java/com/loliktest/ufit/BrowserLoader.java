package com.loliktest.ufit;

import com.loliktest.ufit.exceptions.UFitException;

import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

class BrowserLoader {


    public static IBrowserConfig loadBrowserConfig() {
        IBrowserConfig browserConfig = StreamSupport.stream(ServiceLoader.load(IBrowserConfig.class).spliterator(), false)
                .filter(b -> b.name().equals(getParameterBrowser())).findFirst()
                .orElseThrow(() -> new UFitException("Browser with name: '" + getParameterBrowser() + "' NOT FOUND"));
        return browserConfig;
    }

    private static String getParameterBrowser() {
        return Optional.ofNullable(UFitListener.getParameter("browser")).orElse("default");
    }

}
