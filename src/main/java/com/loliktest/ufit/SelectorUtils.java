package com.loliktest.ufit;

import io.appium.java_client.MobileBy;
import org.openqa.selenium.By;

/**
 * Created by dbudim on 10.09.2020
 */

class SelectorUtils {

    static boolean isCss(String selector) {
        return !selector.startsWith("/") && !selector.startsWith("(") && !selector.startsWith("**");
    }

    static boolean isIOSClassChain(String selector) {
        return selector.startsWith("**") || selector.contains("`");
    }

    static boolean isXpath(String selector) {
        return (selector.startsWith("/") || selector.startsWith("(")) && !selector.contains("`");
    }

    static boolean isSimpleXpath(String selector) {
        return selector.startsWith("/") && selector.replaceAll("[a-zA-Z]", "").replaceAll("[0-9]", "").replace("]", "").replace("/", "").replace("[", "").equals("");
    }

    static By getBy(String selector) {
        return isCss(selector)
                ? By.cssSelector(selector)
                : isXpath(selector) ?
                By.xpath(selector)
                : MobileBy.iOSClassChain(selector);
    }

    static boolean isSelectorCompatibleTo(String selector1, String selector2) {
        return ((isCss(selector1) && isCss(selector2)) ||
                (isXpath(selector1) && isXpath(selector2)) ||
                (isIOSClassChain(selector1) && isIOSClassChain(selector2)) ||
                (isIOSClassChain(selector1) && isSimpleXpath(selector2))
        );
    }
}
