package com.loliktest.ufit.ChromeDevTools;

import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;

import static com.loliktest.ufit.UFitBrowser.browser;

/**
 * ChromeDevTools Facade
 */
public class ChromeDevTools {

    static ThreadLocal<DevTools> devToolsLocal = new ThreadLocal<>();

    public ChromeDevTools() {
        devToolsLocal.set(((HasDevTools)browser().driver()).getDevTools());
        devToolsLocal.get().createSessionIfThereIsNotOne();
    }

    public NetworkUfit network() {
        return new NetworkUfit();
    }

    public PerformanceUfit performance() {
        return new PerformanceUfit();
    }

    public LogsUfit logs() {
        return new LogsUfit();
    }

    public SimulateUfit simulate() {
        return new SimulateUfit();
    }

    public SecurityUfit security() {
        return new SecurityUfit();
    }

}
