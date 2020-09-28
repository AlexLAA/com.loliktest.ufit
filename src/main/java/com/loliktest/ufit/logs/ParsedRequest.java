package com.loliktest.ufit.logs;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

public class ParsedRequest {

    public String url;
    public String method;
    public String statusCode;
    public String time;
    @SerializedName("request-headers")
    public Map<String, String> headers;

    public ParsedRequest(String url, String method, String statusCode, Long timestamp, Map<String, String> headers) {
        long time = timestamp * 1000;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Europe/Kiev"));

        this.url = url;
        this.method = method;
        this.statusCode = statusCode;
        this.time = sdf.format(calendar.getTime());
        this.headers = headers;
    }

    public ParsedRequest() {
    }

    public String getTime() {
        return time;
    }
}
