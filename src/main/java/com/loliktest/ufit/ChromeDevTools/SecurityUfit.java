package com.loliktest.ufit.ChromeDevTools;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.v97.security.Security;

import java.util.List;
import java.util.Map;

import static com.loliktest.ufit.UFitBrowser.browser;

public class SecurityUfit {

    /**
     * @param permissionsList - as example "audioCapture"
     */
    public void grantPermissions(List<String> permissionsList) {
        ((ChromeDriver)browser().driver())
                .executeCdpCommand("Browser.grantPermissions", Map.of("permissions", permissionsList));
    }

    public void ignoreSslCertificates() {
        ChromeDevTools.devToolsLocal.get().send(Security.enable());
        ChromeDevTools.devToolsLocal.get().send(Security.setIgnoreCertificateErrors(true));
    }

}
