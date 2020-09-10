package com.loliktest.ufit;

/**
 * Created by dbudim on 10.09.2020
 */

public class SelectorUtils {

    public static boolean isCss(String selector) {
        return !selector.startsWith("/");
    }

    public static boolean isSelectorCompatibleTo(String selector1, String selector2) {
        if ((isCss(selector1) && isCss(selector2)) | (!isCss(selector1) && !isCss(selector2))) return true;
        return false;
    }

}
