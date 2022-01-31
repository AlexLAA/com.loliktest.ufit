package com.loliktest.ufit;

import io.appium.java_client.MobileBy;
import org.openqa.selenium.By;

/**
 * Created by dbudim on 10.09.2020
 */

class SelectorUtils {

    static boolean isCss(String selector) {
        return !selector.startsWith("/") && !selector.startsWith("(") && !selector.contains("**");
    }

    static boolean isIOSClassChain(String selector) {
        return
                selector.contains("**") ||
                        selector.contains("==") ||
                        selector.contains("$") ||
                        selector.contains("`") ||
                        selector.contains("CONTAINS") ||
                        selector.contains("BEGINSWITH");
    }

    static boolean isXpath(String selector) {
        return !isCss(selector) && !isIOSClassChain(selector);
    }

    static boolean isSimpleXpath(String selector) {
        return selector.startsWith("/") && selector.replaceAll("[a-zA-Z]", "").replaceAll("[0-9]", "").replace("]", "").replace("/", "").replace("[", "").equals("");
    }

    static By getBy(String selector) {
        return isCss(selector)
                ? By.cssSelector(selector)
                : isIOSClassChain(selector) ?
                MobileBy.iOSClassChain(selector)
                : By.xpath(selector);
    }

    static boolean isSelectorCompatibleTo(String selector1, String selector2) {
        return ((isCss(selector1) && isCss(selector2)) ||
                (isIOSClassChain(selector1) && isIOSClassChain(selector2)) ||
                (isIOSClassChain(selector1) && isSimpleXpath(selector2)) ||
                (isXpath(selector1) && isXpath(selector2))
        );
    }
}
