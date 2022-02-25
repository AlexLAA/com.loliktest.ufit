package com.loliktest.ufit.devtools;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.v97.emulation.Emulation;
import org.openqa.selenium.devtools.v97.network.Network;
import org.openqa.selenium.devtools.v97.network.model.ConnectionType;

import java.util.Map;
import java.util.Optional;

import static com.loliktest.ufit.UFitBrowser.browser;

public class SimulateUfit {

    private org.openqa.selenium.devtools.DevTools devTools;

    public SimulateUfit(org.openqa.selenium.devtools.DevTools devTools) {
        this.devTools = devTools;
    }

    /**
     * call before browser starts
     * @param width - as example 600
     * @param height - as example 1000
     * @param deviceScaleFactor - as example 50
     */
    public void simulateMobileDevice(int width, int height, int deviceScaleFactor) {
        Map<String, Object> deviceMetrics = Map.of(
                "width", width,
                "height", height,
                "mobile", true,
                "deviceScaleFactor", deviceScaleFactor);
        ((ChromeDriver)browser().driver()).executeCdpCommand("Emulation.setDeviceMetricsOverride", deviceMetrics);
    }

    /**
     * @param isOffline - as example false
     * @param latency - as example 20
     * @param downloadThroughput - as example 20
     * @param uploadThroughput - as example 50
     * @param connectionType - as example CELLULAR3G
     */
    public void simulateNetworkSpeed(boolean isOffline, int latency, int downloadThroughput, int uploadThroughput, ConnectionType connectionType) {
        new NetworkUfit(devTools).enableNetwork();
        devTools.send(Network.emulateNetworkConditions(
                isOffline,
                latency,
                downloadThroughput,
                uploadThroughput,
                Optional.of(connectionType)
        ));
    }

    /**
     * @param timeZone - as example "America/New_York"
     */
    public void setTimezone(String timeZone) {
        devTools.send(Emulation.setTimezoneOverride(timeZone));
    }

    /**
     * @param latitude - as example 40.730610
     * @param longitude - as example -73.935242
     * @param accuracy - as example 1
     */
    public void setGeolocation(Number latitude, Number longitude, Number accuracy) {
        devTools.send(Emulation.setGeolocationOverride(Optional.ofNullable(latitude), Optional.ofNullable(longitude), Optional.ofNullable(accuracy)));
    }
}
