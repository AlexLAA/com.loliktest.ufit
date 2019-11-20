package com.loliktest.ufit;

import org.openqa.selenium.WebDriver;

import java.util.Map;

public interface IBrowserConfig {

    WebDriver setupDriver();

    Map<String, Object> parameters();

}
