package com.loliktest.ufit;

import org.testng.ITestResult;

public class TestNgThread {

    private static ThreadLocal<TestNgThread> CURRENT_THREAD = new ThreadLocal<>();

    private ITestResult result;

    public TestNgThread(ITestResult result){
        this.result = result;
    }

    static void setCurrentThread(ITestResult result){
        CURRENT_THREAD.set(new TestNgThread(result));
    }

    public static TestNgThread currentThread(){
        return CURRENT_THREAD.get();
    }
}
