package com.loliktest.ufit;

import com.loliktest.ufit.exceptions.UFitException;
import io.appium.java_client.MobileBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static com.loliktest.ufit.SelectorUtils.isMobileSelectorCompatibleTo;
import static com.loliktest.ufit.SelectorUtils.isXpath;

/**
 * Created by dbudim on 08.09.2020
 * Elem for mobile automation with Appium etc.
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

    @Override
    public MobileElem formatSelector(String... s) {
        return isXpath(getSelector())
                ? new MobileElem(By.xpath(String.format(getSelector(), s)), getName())
                : new MobileElem(MobileBy.iOSClassChain(String.format(getSelector(), s)), getName());
    }

    @Override
    public MobileElem setIndex(int index) {
        String selector = getSelector();

        By by = isXpath(selector)
                ? By.xpath("(" + selector + ")[" + index + "]")
                : MobileBy.iOSClassChain(selector + "[" + index + "]");

        return new MobileElem(by, name);
    }

    @Override
    public void setParent(Elem elem) {
        String selector1 = elem.getSelector();
        String selector2 = getSelector();

        if (isMobileSelectorCompatibleTo(selector1, selector2)) {
            By by = isXpath(selector1)
                    ? By.xpath(selector1 + selector2)
                    : MobileBy.iOSClassChain(selector1 + selector2);
            setBy(by);
            this.name = elem.getName() + " -> " + name;
        } else {
            throw new UFitException("Selectors: " + selector1 + " and " + selector2 + " are not compatible!");
        }
    }
}
