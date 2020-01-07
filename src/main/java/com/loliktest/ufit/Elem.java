package com.loliktest.ufit;

import com.loliktest.ufit.listeners.IElemListener;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.loliktest.ufit.UFitBrowser.browser;

/**
 * Created by lolik on 02.04.2019
 */
public class Elem {

    private static List<IElemListener> listeners = new ArrayList<>();

    static void setElemListener(IElemListener listener) {
        listeners.add(listener);
    }

    private String name;
    private By by;
    private int index;

    private boolean assertIt = false;

    public Elem(By by, String name) {
        this.by = by;
        this.name = name;
    }

    public Elem(By by) {
        this.by = by;
        this.name = "";
    }

    public Elem(By by, String name, int index) {
        this(by, name);
        this.index = index;
    }

    public Elem(Elem parent, By by, String name) {
        this(by, name);
        setParent(parent);
    }

    public String getName() {
        return name;
    }

    public By getBy() {
        return by;
    }

    public String getSelector() {
        return by.toString().replaceFirst("By.*: ", "");
    }

    public void setBy(By by) {
        this.by = by;
    }

    public Elem formatSelector(String... s) {
        return new Elem(By.cssSelector(String.format(getSelector(), s)), name);
    }

    public void setParent(Elem elem) {
        setBy(By.cssSelector(elem.getSelector() + " " + getSelector()));
        this.name = elem.getName() + " -> " + name;
    }

    public Elem setIndex(int index) {
        // setBy();
        String selector = getSelector();
        if (!selector.contains("(n)")) {
            selector += ":nth-child(n)";
        }
        return new Elem(By.cssSelector(selector.replace("(n)", "(" + index + ")")), name);
    }

    public Actions actions() {
        return new Actions(browser().driver());
    }

    /**
     * Next Action will be Asserted
     *
     * @return
     */
    public Elem assertion() {
        assertIt = true;
        return this;
    }


    public void type(String text) {
        listeners.forEach(l -> l.type(text, this));
        Allure.step("Type: " + text + " in " + getName(), () -> {
                    find().clear();
                    find().sendKeys(text);
                    getWebDriverWait().until(ExpectedConditions.textToBePresentInElementValue(by, text));
                }
        );
    }

    public void typeClearDelete(String text) {
        actions().sendKeys(Keys.chord(Keys.SHIFT, Keys.ARROW_UP))
                .sendKeys(Keys.DELETE)
                .sendKeys(text)
                .sendKeys(Keys.ENTER)
                .perform();
    }

    public void sendKeys(CharSequence... keysToSend) {
        find().sendKeys(keysToSend);
    }

    public void typeClearBackspace(String text) {
        clearBackspace();
        find().sendKeys(text);
        find().sendKeys(Keys.ENTER);
    }

    public void typeByChar(String text) {
        find().clear();
        for (Character character : text.toCharArray()) {
            find().sendKeys(character.toString());
        }
    }

    public void typeByCharAndCheck(String text) {
        typeByChar(text);
        until(ExpectedConditions.textToBePresentInElementValue(by, text), Timeout.getDefaultElem());
    }

    public void typeByJs(String text) {
        browser().devTools.executeScript("arguments[0].textContent = \"" + text + "\";", find());
    }

    /**
     * Field without input tag
     * HTML Elements with [contenteditable="true"]
     *
     * @param text
     */
    public void typeContentEditable(String text) {
        click();
        int times = 0;
        while (!getText().isEmpty()) {
            if (times > 1000) {
                throw new WebDriverException("Can't clear value: " + toString());
            }
            actions().sendKeys(Keys.DELETE).sendKeys(Keys.BACK_SPACE).perform();
            times++;
        }
        find().sendKeys(text);
        isContainsText(text);
    }


    public void click() {
        click(Timeout.getDefaultElem());
    }

    public void clickByJs() {
        browser().devTools.executeScript("arguments[0].click();", find());
    }

    public void click(long timeout) {
        listeners.forEach(l -> l.click(this));
        Allure.step("Click: " + getName(), () -> {
            find(timeout);
            getWebDriverWait(timeout).until(ExpectedConditions.elementToBeClickable(by));
            getWebDriverWait(timeout).until(CustomConditions.click(by));
        });
    }

    public void clickAndWaitPage() {
        WebElement element = find();
        click();
        getWebDriverWait(10).until(ExpectedConditions.stalenessOf(element));
        browser().wait.pageLoadComplete();
    }

    public WebElement find() {
        return find(Timeout.getDefaultElem());
    }

    public WebElement find(long timeout) {
        if (index > 0) {
            return finds(timeout).get(index);
        } else {
            return getWebDriverWait(timeout).withMessage(toString() + " not found on page: " + browser().getCurrentUrl()).until(ExpectedConditions.presenceOfElementLocated(by));
        }
    }

    public List<WebElement> finds() {
        return finds(Timeout.getDefaultElem());
    }

    public List<WebElement> finds(long timeout) {
        return findElements();
    }

    private List<WebElement> findElements() {
        return browser().driver().findElements(by);
    }

    public void clearBackspace() {
        clearBackspace("");
    }

    public void clearBackspace(String expectedValue) {
        clearByKey(expectedValue, Keys.BACK_SPACE);
    }

    public void clearDelete() {
        clearDelete("");
    }

    public void clearDelete(String expectedValue) {
        clearByKey(expectedValue, Keys.DELETE);
    }

    public void clearByKey(String expectedValue, Keys key) {
        int times = 0;
        while (!getAttribute("value").equals(expectedValue)) {
            if (times > 1000) {
                throw new WebDriverException("Can't clear value: " + toString());
            }
            find().sendKeys(key);
            times++;
        }
    }


    public String getText() {
        return getText(Timeout.getDefaultElem());
    }

    public String getText(long timeout) {
        try {
            String text = getWebDriverWait(timeout).until(CustomConditions.getText(by));
            return text;
        } finally {
            assertIt = false;
        }
    }

    public String getAttribute(String name) {
        return find().getAttribute(name);
    }

    public void dragAndDrop(Elem target) {
        actions()
                .clickAndHold(find())
                .moveByOffset(0, 10)
                .moveByOffset(0, 10)
                .release(target.find())
                .perform();
    }

    @Step
    public void hoverOver() {
        hoverOver(Timeout.getDefaultElem());
    }

    @Step
    public void hoverOver(long timeout) {
        actions().moveToElement(find(timeout)).perform();
    }

    @Step
    public void hoverAndClick() {
        actions().moveToElement(find()).click().perform();
    }

    @Step
    public void scrollTo() {
        browser().devTools.executeScript("arguments[0].scrollIntoView(true);", find());
    }

    @Step
    public void clickByCoordinates(int x, int y) {
        actions().moveToElement(find(), x, y).click().perform();
    }

    @Step
    public String getInnerHtml() {
        return (String) browser().devTools.executeScript("return arguments[0].innerHTML", find());
    }

    // WAIT

    private void checkAssert(Object detailMessage) {
        if (assertIt) {
            assertIt = false;
            throw new AssertionError(detailMessage);
        }
    }

    private void checkAssert(String message, Throwable throwable) {
        if (assertIt) {
            assertIt = false;
            throw new AssertionError(message, throwable);
        }
    }

    public boolean isPresent() {
        return isPresent(Timeout.getDefaultElem());
    }

    public boolean isPresent(long timeout) {
        try {
            getWebDriverWait(timeout).until(ExpectedConditions.presenceOfElementLocated(by));
            return true;
        } catch (TimeoutException e) {
            checkAssert(toString() + " NOT PRESENT timeout: " + timeout, e);
            return false;
        } finally {
            assertIt = false;
        }
    }

    public boolean isVisible() {
        return isVisible(Timeout.getDefaultElem());
    }

    public boolean isVisible(long timeout) {
        try {
            getWebDriverWait(timeout).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(by));
            return true;
        } catch (TimeoutException e) {
            checkAssert(e);
            return false;
        } finally {
            assertIt = false;
        }
    }

    public boolean isVisibleWithIgnore(long timeout, Class<? extends Throwable> exceptionType) {
        try {
            getWebDriverWait(timeout)
                    .ignoring(exceptionType)
                    .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(by));
            return true;
        } catch (TimeoutException e) {
            checkAssert(e);
            return false;
        }
        finally {
            assertIt = false;
        }
    }

    /**
     * Waits until element to be present in HTML, after that waits until this element will NOT Present (loaders workaround)
     *
     * @param timeoutPresent
     * @param timeoutNotPresent
     * @return
     */
    @Deprecated
    public boolean isDisappeared(long timeoutPresent, long timeoutNotPresent) {
        try {
            getWebDriverWait(timeoutPresent).pollingEvery(Duration.ofMillis(100)).until(ExpectedConditions.presenceOfElementLocated(by));
        } catch (TimeoutException e) {

        }
        return isNotPresent(timeoutNotPresent);
    }

    @Deprecated
    public boolean isDisappeared(long timeout) {
        return isDisappeared(5, timeout);
    }

    public boolean isNotVisible(long timeout) {
        try {
            getWebDriverWait(timeout).until(ExpectedConditions.invisibilityOfElementLocated(by));
            return true;
        } catch (TimeoutException e) {
            checkAssert(e);
            return false;
        } finally {
            assertIt = false;
        }
    }

    public boolean isNotPresent(long timeout) {
        try {
            getWebDriverWait(timeout).until(ExpectedConditions.numberOfElementsToBe(by, 0));
            return true;
        } catch (TimeoutException e) {
            checkAssert(e);
            return false;
        } finally {
            assertIt = false;
        }
    }

    public boolean isNotPresent() {
        return isNotPresent(Timeout.getDefaultElem());
    }


    public boolean isContainsText(String text) {
        return isContainsText(text, Timeout.getDefaultElem());
    }


    public boolean isContainsText(String text, long timeout) {
        try {
            getWebDriverWait(timeout).until(ExpectedConditions.textToBePresentInElementLocated(by, text));
            return true;
        } catch (TimeoutException e) {
            checkAssert("\nText: '" + text + "' not found in element " + toString() + " timeout: " + timeout, e);
            return false;
        } finally {
            assertIt = false;
        }
    }

    public boolean isEqualsText(String text) {
        return isEqualsText(text, Timeout.getDefaultElem());
    }


    public boolean isEqualsText(String text, long timeout) {
        try {
            getWebDriverWait(timeout).until(ExpectedConditions.textToBe(by, text));
            return true;
        } catch (TimeoutException e) {
            checkAssert("\nText: '" + text + "' not found in element " + toString() + " timeout: " + timeout, e);
            return false;
        } finally {
            assertIt = false;
        }
    }

    public boolean isNotContainsText(String text) {
        return isNotContainsText(text, Timeout.getDefaultElem());
    }

    public boolean isNotContainsText(String text, long timeout) {
        try {
            getWebDriverWait(timeout).until(CustomConditions.textNotToBePresentInElementLocated(by, text));
            return true;
        } catch (TimeoutException e) {
            checkAssert("Text: '" + text + "' found in element " + toString() + " timeout: " + timeout, e);
            return false;
        } finally {
            assertIt = false;
        }
    }

    public boolean isAttributeNotContainsValue(String attribute, String value) {
        return isAttributeNotContainsValue(attribute, value, Timeout.getDefaultElem());
    }

    public boolean isAttributeNotContainsValue(String attribute, String value, long timeout) {
        return until(ExpectedConditions.not(ExpectedConditions.attributeContains(by, attribute, value)), timeout);
    }


    public boolean isAttributeNotEqualsValue(String attribute, String value) {
        return isAttributeNotEqualsValue(attribute, value, Timeout.getDefaultElem());
    }

    public boolean isAttributeNotEqualsValue(String attribute, String value, long timeout) {
        return until(ExpectedConditions.not(ExpectedConditions.attributeToBe(by, attribute, value)), timeout);
    }

    public boolean isAttributeContainsValue(String attribute, String value) {
        return isAttributeContainsValue(attribute, value, Timeout.getDefaultElem());
    }

    public boolean isAttributeContainsValue(String attribute, String value, long timeout) {
        try {
            getWebDriverWait(timeout).until(ExpectedConditions.attributeContains(by, attribute, value));
            return true;
        } catch (TimeoutException e) {
            checkAssert("Value in Attribute '" + attribute + "': '" + value + "' not found in element " + toString() + " timeout: " + timeout, e);
            return false;
        } finally {
            assertIt = false;
        }
    }

    public boolean isContainsAttribute(String attribute) {
        return isContainsAttribute(attribute, Timeout.getDefaultElem());
    }

    public boolean isContainsAttribute(String attribute, long timeout) {
        return isAttributeContainsValue(attribute, "", timeout);
    }

    public boolean isSelected() {
        return isSelected(Timeout.getDefaultElem());
    }

    public boolean isSelected(long timeout) {
        return isSelectionState(true, timeout);
    }

    public boolean isNotSelected() {
        return isSelectionState(false);
    }

    public boolean isSelectionState(boolean state) {
        return isSelectionState(state, Timeout.getDefaultElem());
    }

    public boolean isParentOf(Elem child) {
        return isParentOf(child, Timeout.getDefaultElem());
    }

    public boolean isParentOf(Elem child, long timeout) {
        try {
            return new Elem(this, child.getBy(), name).assertion().isPresent(timeout);
        } catch (AssertionError e) {
            checkAssert("Element: " + toString() + " Not Parent of " + child.toString(), e);
            return false;
        } finally {
            assertIt = false;
        }
    }


    public boolean isChildOf(Elem parent) {
        return isChildOf(parent, Timeout.getDefaultElem());
    }

    public boolean isChildOf(Elem parent, long timeout) {
        try {
            return new Elem(parent, getBy(), name).assertion().isPresent(timeout);
        } catch (AssertionError e) {
            checkAssert("Element: " + toString() + " Not Child of " + parent.toString(), e);
            return false;
        } finally {
            assertIt = false;
        }
    }


    public boolean isSelectionState(boolean state, long timeout) {
        return until(ExpectedConditions.elementSelectionStateToBe(by, state), timeout);
    }

    public boolean isNumberOfElementsToBeMoreThan(int number) {
        return isNumberOfElementsToBeMoreThan(number, Timeout.getDefaultElem());
    }

    public boolean isNumberOfElementsToBeMoreThan(int number, long timeout) {
        return until(ExpectedConditions.numberOfElementsToBeMoreThan(by, number), timeout);
    }

    public boolean isNumberOfElementsToBeLessThan(int number) {
        return isNumberOfElementsToBeLessThan(number, Timeout.getDefaultElem());
    }

    public boolean isNumberOfElementsToBeLessThan(int number, long timeout) {
        return until(ExpectedConditions.numberOfElementsToBeLessThan(by, number), timeout);
    }

    public boolean isNumberOfElementsToBe(int number, long timeout) {
        return until(ExpectedConditions.numberOfElementsToBe(by, number), timeout);
    }

    public <V> boolean is(Function<? super WebDriver, V> isTrue) {
        return is(isTrue, Timeout.getDefaultElem());
    }

    public <V> boolean is(Function<? super WebDriver, V> isTrue, long timeout) {
        return until(isTrue, timeout);
    }

    private <V> boolean until(Function<? super WebDriver, V> isTrue, long timeout) {
        try {
            getWebDriverWait(timeout).pollingEvery(Duration.ofMillis(200)).until(isTrue);
            return true;
        } catch (TimeoutException e) {
            checkAssert(e);
            return false;
        } finally {
            assertIt = false;
        }
    }

    private WebDriverWait getWebDriverWait() {
        return browser().wait.driverWait();
    }

    private WebDriverWait getWebDriverWait(long seconds) {
        return browser().wait.driverWait(seconds);
    }

    @Deprecated
    public List<Elem> findList() {
        AtomicInteger integer = new AtomicInteger(1);
        return this.finds().stream().map(o -> new Elem(this.getBy(), this.getName()).setIndex(integer.getAndIncrement())).collect(Collectors.toList());
    }

    @Deprecated
    public Elem findsElemByText(String text) {
        return findList().stream().filter(o -> o.getText().equals(text)).findFirst().orElseThrow(() -> new AssertionError("Element with text: " + text + " NOT FOUND"));
    }


    public void switchToFrame(long timeout) {
        until(CustomConditions.frameToBeAvailableAndSwitchToIt(by), timeout);
    }

    public void switchToFrame() {
        switchToFrame(Timeout.getDefaultElem());
    }

    @Override
    public String toString() {
        return "'" + name + "'" + " (" + by + ")";
    }

    private static class CustomConditions {
        public static ExpectedCondition<String> getText(final By locator) {

            return new ExpectedCondition<String>() {
                @Override
                public String apply(WebDriver driver) {
                    try {
                        return driver.findElement(locator).getText();
                    } catch (StaleElementReferenceException e) {
                        return "";
                    }
                }

                @Override
                public String toString() {
                    return "Cannot get text from element: " + locator;
                }
            };
        }

        public static ExpectedCondition<Boolean> click(final By locator) {
            return new ExpectedCondition<Boolean>() {
                String message = "";

                @Override
                public Boolean apply(WebDriver driver) {
                    try {
                        driver.findElement(locator).click();
                        return true;
                    } catch (ElementClickInterceptedException | NoSuchElementException | StaleElementReferenceException e) {
                        message = e.getMessage();
                        return false;
                    }
                }


                @Override
                public String toString() {
                    return "Click Element: " + message;
                }
            };
        }

        public static ExpectedCondition<Boolean> textNotToBePresentInElementLocated(final By locator, final String text) {

            return new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver driver) {
                    try {
                        String elementText = driver.findElement(locator).getText();
                        return !elementText.contains(text);
                    } catch (StaleElementReferenceException e) {
                        return null;
                    }
                }

                @Override
                public String toString() {
                    return String.format("text ('%s') not to be present in element found by %s",
                            text, locator);
                }
            };
        }

        public static ExpectedCondition<Boolean> frameToBeAvailableAndSwitchToIt(final By locator) {
            return new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver driver) {
                    try {
                        driver.switchTo().frame(driver.findElement(locator));
                        return true;
                    } catch (NoSuchFrameException | StaleElementReferenceException e) {
                        return false;
                    }
                }

                @Override
                public String toString() {
                    return "frame to be available: " + locator;
                }
            };
        }

    }


}
