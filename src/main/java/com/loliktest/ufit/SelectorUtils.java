package com.loliktest.ufit;

import org.openqa.selenium.By;

/**
 * Created by dbudim on 10.09.2020
 */

class SelectorUtils {

    static boolean isCss(String selector) {
        return !selector.startsWith("/") && !selector.startsWith("(");
    }

    static By getBy(String selector) {
        return isCss(selector)
                ? By.cssSelector(selector)
                : By.xpath(selector);
    }

    static boolean isSelectorCompatibleTo(String selector1, String selector2) {
        return (isCss(selector1) && isCss(selector2)) | (!isCss(selector1) && !isCss(selector2));
    }
}
