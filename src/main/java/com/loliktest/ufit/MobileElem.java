package com.loliktest.ufit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static com.loliktest.ufit.SelectorUtils.isCss;

/**
 * Created by dbudim on 08.09.2020
 *
 * Elem for mobile automation with Appium etc.
 *
 */

public class MobileElem extends Elem {

    public MobileElem(By by, String name) {
        super(by, name);
    }

    public MobileElem(By by) {
        super(by);
    }

    public MobileElem(By by, String name, int index) {
        super(by, name, index);
    }

    public MobileElem(Elem parent, By by, String name) {
        super(parent, by, name);
    }

    public WebElement find(long timeout) {
        if (index > 0) {
            return finds(timeout).get(index);
        } else {
            return getWebDriverWait(timeout).withMessage(toString() + " not found!").until(ExpectedConditions.presenceOfElementLocated(by));
        }
    }

    public MobileElem formatSelector(String... s) {
        return new MobileElem(By.xpath(String.format(getSelector(), s)), getName());
    }

    @Override
    public MobileElem setIndex(int index) {
        String selector = getSelector();

        By by;
        if (isCss(selector)) {
            if (!selector.contains("(n)")) {
                selector += ":nth-child(n)";
            }
            by = By.cssSelector(selector.replace("(n)", "(" + index + ")"));
        } else {
            by = By.xpath("(" + selector + ")[" + index + "]");
        }
        return new MobileElem(by, name);
    }
}
