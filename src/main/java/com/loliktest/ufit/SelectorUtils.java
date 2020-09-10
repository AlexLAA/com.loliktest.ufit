package com.loliktest.ufit;

import org.openqa.selenium.By;

/**
 * Created by dbudim on 10.09.2020
 */

public class SelectorUtils {

    public static boolean isCss(String selector) {
        return !selector.contains("/");
    }

    public static By getBy(String selector) {
        return isCss(selector)
                ? By.cssSelector(selector)
                : By.xpath(selector);
    }

    public static boolean isSelectorCompatibleTo(String selector1, String selector2) {
        return (isCss(selector1) && isCss(selector2)) | (!isCss(selector1) && !isCss(selector2));
    }
}
