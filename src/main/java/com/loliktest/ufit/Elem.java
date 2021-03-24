package com.loliktest.ufit;

import com.loliktest.ufit.exceptions.UFitException;
import com.loliktest.ufit.listeners.IElemListener;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.loliktest.ufit.SelectorUtils.isCss;
import static com.loliktest.ufit.SelectorUtils.isSelectorCompatibleTo;
import static com.loliktest.ufit.UFitBrowser.browser;

/**
 * Created by lolik on 02.04.2019
 */
public class Elem {

    protected static List<IElemListener> listeners = new ArrayList<>();

    static void setElemListener(IElemListener listener) {
        listeners.add(listener);
    }

    protected String name;
    protected By by;
    protected int index;
    private String assertMessage = "";

    private boolean assertIt = false;
    private List<Class<? extends Throwable>> ignoredExceptions = new ArrayList<>();

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
        if (isSelectorCompatibleTo(elem.getSelector(), getSelector())) {
            By by = isCss(getSelector())
                    ? By.cssSelector(elem.getSelector() + " " + getSelector())
                    : By.xpath(elem.getSelector() + getSelector());
            setBy(by);
            this.name = elem.getName() + " -> " + name;
        } else {
            throw new UFitException("Selectors: " + elem.getSelector() + " and " + getSelector() + " are not compatible!");
        }
    }

    public Elem setIndex(int index) {
        // setBy();
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
        return new Elem(by, name);
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

    public Elem assertion(String message) {
        assertIt = true;
        assertMessage = message;
        return this;
    }

    public Elem ignoring(Class<? extends Throwable> exceptionType) {
        ignoredExceptions.add(exceptionType);
        return this;
    }

    public Elem ignoreAll(List<Class<? extends Throwable>> exceptionTypes) {
        ignoredExceptions.addAll(exceptionTypes);
        return this;
    }

    public void type(String text) {
        listeners.forEach(l -> l.type(text, this));
        Allure.step("Type test data into " + getName(), () -> {
                    find().clear();
                    find().sendKeys(text);
                    getWebDriverWait().until(ExpectedConditions.textToBePresentInElementValue(by, text));
                }
        );
    }

    public void typeClearDelete(String text) {
        Allure.step("Type test data into " + getName(), () -> {
            actions().sendKeys(Keys.chord(Keys.SHIFT, Keys.ARROW_UP))
                    .sendKeys(Keys.DELETE)
                    .sendKeys(text)
                    .sendKeys(Keys.ENTER)
                    .perform();
            }
        );
    }

    public void sendKeys(CharSequence... keysToSend) {
        find().sendKeys(keysToSend);
    }

    public void typeClearBackspace(String text) {
        Allure.step("Type test data into " + getName(), () -> {
            clearBackspace();
            find().sendKeys(text);
            find().sendKeys(Keys.ENTER);
        });
    }

    public void typeByChar(String text) {
        Allure.step("Type test data into " + getName(), () -> {
            find().clear();
            for (Character character : text.toCharArray()) {
                find().sendKeys(character.toString());
            }
        });
    }

    public void typeByCharAndCheck(String text) {
        typeByChar(text);
        until(ExpectedConditions.textToBePresentInElementValue(by, text), Timeout.getDefaultElem());
    }

    public void typeByJs(String text) {
        Allure.step("Type test data into " + getName(), () -> {
            browser().devTools.executeScript("arguments[0].textContent = \"" + text + "\";", find());
        });
    }

    /**
     * Field without input tag
     * HTML Elements with [contenteditable="true"]
     *
     * @param text
     */
    public void typeContentEditable(String text) {
        Allure.step("Type test data into " + getName(), () -> {
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
        });
    }


    public void click() {
        click(Timeout.getDefaultElem());
    }

    public void clickByJs() {
        Allure.step("Click: " + getName(), () -> {
            browser().devTools.executeScript("arguments[0].click();", find());
        });
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
            String text = getWebDriverWait(timeout).ignoreAll(ignoredExceptions).until(CustomConditions.getText(by));
            return text;
        } finally {
            assertIt = false;
            ignoredExceptions.clear();
        }
    }

    public void highlight() {
        browser().devTools.executeScript("arguments[0].setAttribute('style', 'border: 2px solid red;');", find());
    }

    public String getAttribute(String name) {
        return find().getAttribute(name);
    }

    public void dragAndDrop(Elem target) {
        Allure.step("Drag and Drop: " + getName(), () -> {
            actions()
                    .clickAndHold(find())
                    .moveByOffset(0, 10)
                    .moveByOffset(0, 10)
                    .release(target.find())
                    .perform();
        });
    }

    public void hoverOver() {
        hoverOver(Timeout.getDefaultElem());
    }

    public void hoverOver(long timeout) {
        actions().moveToElement(find(timeout)).perform();
    }

    public void hoverAndClick() {
        actions().moveToElement(find()).click().perform();
    }

    public void scrollTo() {
        Allure.step("Scroll to: " + getName(), () -> {
                    browser().devTools.executeScript("arguments[0].scrollIntoView(true);", find());
                }
        );
    }

    public void clickByCoordinates(int x, int y) {
        actions().moveToElement(find(), x, y).click().perform();
    }

    public String getInnerHtml() {
        return (String) browser().devTools.executeScript("return arguments[0].innerHTML", find());
    }

    // WAIT

    private void checkAssert(Object detailMessage) {
        if (assertIt) {
            assertIt = false;
            if (!assertMessage.isEmpty()) {
                detailMessage = assertMessage;
            }
            throw new AssertionError(detailMessage);
        }
    }

    private void checkAssert(String message, Throwable throwable) {
        if (assertIt) {
            assertIt = false;
            if (!assertMessage.isEmpty()) {
                message = assertMessage;
            }
            throw new AssertionError(message, throwable);
        }
    }

    public boolean isPresent() {
        return isPresent(Timeout.getDefaultElem());
    }

    public boolean isPresent(long timeout) {
        try {
            getWebDriverWait(timeout).ignoreAll(ignoredExceptions).until(ExpectedConditions.presenceOfElementLocated(by));
            return true;
        } catch (TimeoutException e) {
            checkAssert(toString() + " NOT PRESENT timeout: " + timeout, e);
            return false;
        } finally {
            assertIt = false;
            ignoredExceptions.clear();
        }
    }

    public boolean isVisible() {
        return isVisible(Timeout.getDefaultElem());
    }

    public boolean isVisible(long timeout) {
        try {
            getWebDriverWait(timeout).ignoreAll(ignoredExceptions).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(by));
            return true;
        } catch (TimeoutException e) {
            checkAssert(e);
            return false;
        } finally {
            assertIt = false;
            ignoredExceptions.clear();
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
            getWebDriverWait(timeout).ignoreAll(ignoredExceptions).until(ExpectedConditions.invisibilityOfElementLocated(by));
            return true;
        } catch (TimeoutException e) {
            checkAssert(e);
            return false;
        } finally {
            assertIt = false;
            ignoredExceptions.clear();
        }
    }

    public boolean isNotPresent(long timeout) {
        try {
            getWebDriverWait(timeout).ignoreAll(ignoredExceptions).until(ExpectedConditions.numberOfElementsToBe(by, 0));
            return true;
        } catch (TimeoutException e) {
            checkAssert(new TimeoutException("Element MUST BE NOT PRESENT: " + toString(), e));
            return false;
        } finally {
            assertIt = false;
            ignoredExceptions.clear();
        }
    }

    public boolean isNotPresent() {
        return isNotPresent(Timeout.getDefaultElem());
    }


    public boolean isContainsText(String text) {
        return isContainsText(text, Timeout.getDefaultElem());
    }


    public boolean isContainsText(String text, long timeout) {
       return allureStep("Assertion: " + getName() + " contains text - " + text, () -> {
            try {
                getWebDriverWait(timeout).ignoreAll(ignoredExceptions).until(ExpectedConditions.textToBePresentInElementLocated(by, text));
                return true;
            } catch (TimeoutException e) {
                checkAssert("\nText: '" + text + "' not found in element " + toString() + " timeout: " + timeout, e);
                return false;
            } finally {
                assertIt = false;
                ignoredExceptions.clear();
            }
        });
    }

    public boolean isEqualsText(String text) {
        return isEqualsText(text, Timeout.getDefaultElem());
    }


    public boolean isEqualsText(String text, long timeout) {
      return allureStep("Assertion: " + getName() + " text equals - " + text, () -> {
            try {
                getWebDriverWait(timeout).ignoreAll(ignoredExceptions).until(ExpectedConditions.textToBe(by, text));
                return true;
            } catch (TimeoutException e) {
                checkAssert("\nText: '" + text + "' not found in element " + toString() + " timeout: " + timeout, e);
                return false;
            } finally {
                assertIt = false;
                ignoredExceptions.clear();
            }
        });
    }

    public boolean isNotContainsText(String text) {
        return isNotContainsText(text, Timeout.getDefaultElem());
    }

    public boolean isNotContainsText(String text, long timeout) {
        try {
            getWebDriverWait(timeout).ignoreAll(ignoredExceptions).until(CustomConditions.textNotToBePresentInElementLocated(by, text));
            return true;
        } catch (TimeoutException e) {
            checkAssert("Text: '" + text + "' found in element " + toString() + " timeout: " + timeout, e);
            return false;
        } finally {
            assertIt = false;
            ignoredExceptions.clear();
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
            ignoredExceptions.add(StaleElementReferenceException.class);
            getWebDriverWait(timeout).ignoreAll(ignoredExceptions).until(ExpectedConditions.attributeContains(by, attribute, value));
            return true;
        } catch (TimeoutException e) {
            checkAssert("Value in Attribute '" + attribute + "': '" + value + "' not found in element " + toString() + " timeout: " + timeout, e);
            return false;
        } finally {
            assertIt = false;
            ignoredExceptions.clear();
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
            ignoredExceptions.clear();
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
            ignoredExceptions.clear();
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

    public boolean isInViewport(long timeout) {
        return until(CustomConditions.elementInViewport(by), timeout);
    }

    public boolean isInViewport() {
        return until(CustomConditions.elementInViewport(by), Timeout.getDefaultElem());
    }


    public <V> boolean is(Function<? super WebDriver, V> isTrue) {
        return is(isTrue, Timeout.getDefaultElem());
    }

    public <V> boolean is(Function<? super WebDriver, V> isTrue, long timeout) {
        return until(isTrue, timeout);
    }

    private <V> boolean until(Function<? super WebDriver, V> isTrue, long timeout) {
        try {
            ignoredExceptions.add(StaleElementReferenceException.class);
            getWebDriverWait(timeout).ignoreAll(ignoredExceptions).pollingEvery(Duration.ofMillis(200)).until(isTrue);
            return true;
        } catch (TimeoutException e) {
            checkAssert(e);
            return false;
        } finally {
            assertIt = false;
            ignoredExceptions.clear();
        }
    }

    private WebDriverWait getWebDriverWait() {
        return browser().wait.driverWait();
    }

   protected WebDriverWait getWebDriverWait(long seconds) {
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

    public Select select(){
        return new Select(find());
    }

    @Override
    public String toString() {
        return "'" + name + "'" + " (" + by + ")";
    }

    private boolean allureStep(String name, Allure.ThrowableRunnable<Boolean> runnable ) {
        if(assertIt) {
            return Allure.step(name, runnable);
        } else {
            try {
                return runnable.run();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                return false;
            }
        }
    }

   protected static class CustomConditions {
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

        public static ExpectedCondition<Boolean> elementInViewport(final By locator) {

            return new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver driver) {
                    try {
                        return (Boolean) ((JavascriptExecutor) driver).executeScript("return window.innerHeight > (arguments[0].getBoundingClientRect().y + arguments[0].getBoundingClientRect().height)", driver.findElement(locator));
                    } catch (JavascriptException e) {
                        return false;
                    }
                }

                @Override
                public String toString() {
                    return String.format("Element not in viewport %s", locator);
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
