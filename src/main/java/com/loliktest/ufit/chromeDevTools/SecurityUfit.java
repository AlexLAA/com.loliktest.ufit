package com.loliktest.ufit.chromeDevTools;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.v97.security.Security;

import java.util.List;
import java.util.Map;

import static com.loliktest.ufit.UFitBrowser.browser;

public class SecurityUfit {

    org.openqa.selenium.devtools.DevTools devTools;

    public SecurityUfit(org.openqa.selenium.devtools.DevTools devTools) {
        this.devTools = devTools;
    }

    /**
     * @param permissionsList - as example "audioCapture"
     */
    public void grantPermissions(List<String> permissionsList, ChromeDriver driver) {
        driver.executeCdpCommand("Browser.grantPermissions", Map.of("permissions", permissionsList));
    }

    public void grantPermissions(List<String> permissionsList) {
        grantPermissions(permissionsList, (ChromeDriver)browser().driver());
    }

    public void ignoreSslCertificates() {
        devTools.send(Security.enable());
        devTools.send(Security.setIgnoreCertificateErrors(true));
    }

}
