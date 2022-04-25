package com.loliktest.ufit.devtools;

import org.openqa.selenium.devtools.v97.fetch.model.HeaderEntry;
import org.openqa.selenium.devtools.v97.fetch.model.RequestPaused;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Wrapper class for modify RequestPaused, which you must send in constructor
 */
public class FetchUfit {

    private String url;
    private String method;
    private String postData;
    private List<HeaderEntry> headers;
    private Boolean interceptResponse;
    private final RequestPaused requestPaused;

    /**
     * Constructor uses for init FetchUfit object with default intercepted request parameters
     */
    public FetchUfit(RequestPaused requestPaused) {
        this.requestPaused = requestPaused;
        this.url = requestPaused.getRequest().getUrl();
        this.method = requestPaused.getRequest().getMethod();
        this.postData = requestPaused.getRequest().getPostData().toString();
        this.headers = requestPaused.getRequest().getHeaders() != null
                ? requestPaused.getRequest().getHeaders().entrySet().stream().map(elem -> new HeaderEntry(elem.getKey(), elem.getValue().toString())).collect(Collectors.toList())
                : new ArrayList<>();
        this.interceptResponse = false;
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    public String getPostData() {
        return postData;
    }

    public List<HeaderEntry> getHeaders() {
        return headers;
    }

    public Boolean getInterceptResponse() {
        return interceptResponse;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUrl(Function<RequestPaused, String> urlFunction) {
        setUrl(urlFunction.apply(requestPaused));
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setMethod(Function<RequestPaused, String> methodFunction) {
        setMethod(methodFunction.apply(requestPaused));
    }

    public void setPostData(String postData) {
        this.postData = postData;
    }

    public void setPostData(Function<RequestPaused, String> functionPostData) {
        setPostData(functionPostData.apply(requestPaused));
    }

    public void setHeaders(List<HeaderEntry> headers) {
        this.headers = headers;
    }

    public void setHeaders(Function<RequestPaused, List<HeaderEntry>> functionPostData) {
        setHeaders(functionPostData.apply(requestPaused));
    }

    public void setInterceptResponse(Boolean interceptResponse) {
        this.interceptResponse = interceptResponse;
    }

}
