package com.loliktest.ufit;

public class Timeout {

    private static long SHORT_WAIT = 10;
    private static long DEFAULT_WAIT = 30;


    public static long getDefault() {
        return DEFAULT_WAIT;
    }

    public static long getDefaultElem() {
        return SHORT_WAIT;
    }


}
