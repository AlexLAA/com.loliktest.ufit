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

    public final String getUrl() {
        return url;
    }

    public final String getMethod() {
        return method;
    }

    public final String getPostData() {
        return postData;
    }

    public final List<HeaderEntry> getHeaders() {
        return headers;
    }

    public final Boolean getInterceptResponse() {
        return interceptResponse;
    }
    /**
     * init FetchBuilder for modify url, method, postData and headers
     */
    public FetchBuilder builder() {
        return new FetchBuilder();
    }

    public class FetchBuilder {
        /**
         * use set methods for modify request url, method, postData and headers
         */
        public FetchBuilder setUrl(String url) {
            FetchUfit.this.url = url;
            return this;
        }

        public FetchBuilder setUrl(Function<RequestPaused, String> urlFunction) {
            return setUrl(urlFunction.apply(requestPaused));
        }

        public FetchBuilder setMethod(String method) {
            FetchUfit.this.method = method;
            return this;
        }

        public FetchBuilder setMethod(Function<RequestPaused, String> methodFunction) {
            return setMethod(methodFunction.apply(requestPaused));
        }

        public FetchBuilder setPostData(String postData) {
            FetchUfit.this.postData = postData;
            return this;
        }

        public FetchBuilder setPostData(Function<RequestPaused, String> functionPostData) {
            return setPostData(functionPostData.apply(requestPaused));
        }

        public FetchBuilder setHeaders(List<HeaderEntry> headers) {
            FetchUfit.this.headers = headers;
            return this;
        }

        public FetchBuilder setHeaders(Function<RequestPaused, List<HeaderEntry>> functionPostData) {
            return setHeaders(functionPostData.apply(requestPaused));
        }

        public FetchBuilder setInterceptResponse(Boolean interceptResponse) {
            FetchUfit.this.interceptResponse = interceptResponse;
            return this;
        }
        /**
         * return modified FetchUfit object
         */
        public FetchUfit build() {
            return FetchUfit.this;
        }
    }
}
