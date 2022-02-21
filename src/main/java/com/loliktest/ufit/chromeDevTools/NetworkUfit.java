package com.loliktest.ufit.chromeDevTools;

import org.openqa.selenium.devtools.v97.fetch.Fetch;
import org.openqa.selenium.devtools.v97.fetch.model.RequestPattern;
import org.openqa.selenium.devtools.v97.fetch.model.RequestPaused;
import org.openqa.selenium.devtools.v97.fetch.model.RequestStage;
import org.openqa.selenium.devtools.v97.network.Network;
import org.openqa.selenium.devtools.v97.network.model.Cookie;
import org.openqa.selenium.devtools.v97.network.model.Headers;
import org.openqa.selenium.devtools.v97.network.model.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;

public class NetworkUfit {

    org.openqa.selenium.devtools.DevTools devTools;

    public NetworkUfit(org.openqa.selenium.devtools.DevTools devTools) {
        this.devTools = devTools;
    }

    private final Logger logger = LoggerFactory.getLogger(NetworkUfit.class);

    void enableNetwork(Integer maxTotalBufferSize, Integer maxResourceBufferSize, Integer maxPostDataSize) {
        java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.WARNING);
        devTools.send(Network.enable(Optional.ofNullable(maxTotalBufferSize), Optional.ofNullable(maxResourceBufferSize), Optional.ofNullable(maxPostDataSize)));
    }

    /**
     * network must be enabled before send CDP Network commands
     * method calls in all other NetworkUfit methods, where needed
     */
    void enableNetwork() {
        enableNetwork(null, null, null);
    }

    /**
     * network must be disabled manually
     */
    public void disableNetwork() {
        devTools.send(Network.disable());
    }

    /**
     * fetch must be enabled before send CDP Fetch commands
     * method calls in all other NetworkUfit methods, where needed
     */
    void enableModifyFetch(List<RequestPattern> requestPattern) {
        devTools.send(Fetch.enable(Optional.ofNullable(requestPattern), Optional.empty()));
    }

    /**
     * fetch must be disabled manually
     */
    public void disableModifyFetch() {
        devTools.send(Fetch.disable());
    }

    /**
     * @param requestPattern - List<RequestPattern> - use for specify request pattern (https://chromedevtools.github.io/devtools-protocol/tot/Fetch/#type-RequestPattern)
     * @param fetchBuilderFunction - Function<RequestPaused, FetchUfit> - function for get FetchUfit object with modify parameters from intercepted RequestPaused object.
     *                             FetchUfit construct with request parameters (url, method, postData, headers),
     *                             which you can change by FetchBuilder
     */
    public void modifyRequest(List<RequestPattern> requestPattern, Function<RequestPaused, FetchUfit> fetchBuilderFunction) {
        enableModifyFetch(requestPattern);
        devTools.addListener(Fetch.requestPaused(), requestPaused -> {
                FetchUfit fetchUfit = fetchBuilderFunction.apply(requestPaused);
                devTools.send(Fetch.continueRequest(
                        requestPaused.getRequestId(),
                        Optional.ofNullable(fetchUfit.getUrl()),
                        Optional.ofNullable(fetchUfit.getMethod()),
                        Optional.ofNullable(fetchUfit.getPostData()),
                        Optional.ofNullable(fetchUfit.getHeaders()),
                        Optional.ofNullable(fetchUfit.getInterceptResponse())));
            }
        );
    }

    public void modifyRequest(Function<RequestPaused, FetchUfit> fetchBuilderFunction) {
        modifyRequest(List.of(new RequestPattern(Optional.of("*"), Optional.of(ResourceType.XHR), Optional.of(RequestStage.REQUEST))), fetchBuilderFunction);
    }

    public void updateUserAgent(String userAgent) {
        devTools.send(Network.setUserAgentOverride(userAgent, Optional.empty(), Optional.empty(), Optional.empty()));
    }

    /**
     * add Http header to requests
     * @param headersMap - as example ["aqa-header", "John Wick"]
     */
    public void setHttpHeaders(Map<String, Object> headersMap) {
        enableNetwork();
        devTools.send(Network.setExtraHTTPHeaders(new Headers(headersMap))); //Map.of("aqa-header", "John Wick")
    }

    public List<Cookie> getAllCookies() {
        enableNetwork();
        var cookiesList = devTools.send(Network.getAllCookies());
        disableNetwork();
        return cookiesList;
    }


    public void clearBrowserCache() {
        enableNetwork();
        devTools.send(Network.clearBrowserCache());
        disableNetwork();
    }

    /**
     * @param blockedURLsList - List of blocked URLs, as example "*.svg"
     */
    public void setBlockedURLs(List<String> blockedURLsList) {
        enableNetwork();
        devTools.send(Network.setBlockedURLs(blockedURLsList));
        disableNetwork();
    }


}
