package com.loliktest.ufit;

import org.awaitility.core.ConditionTimeoutException;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.awaitility.Awaitility.await;

public class Elems<T> {

    private Class<T> type;
    private boolean assertIt = false;
    private String assertMessage;
    private long timeout = 30;
    private int initialIndex = 1;
    private int delta = 1;
    private boolean complex;
    private boolean isUFit;
    public Elem elem;

    public Elems() {
    }

    public Elems(Elem elem, Class<T> type) {
        this.elem = elem;
        this.type = type;
    }

    public Elems(Elem elem, Class<T> type, int initalIndex) {
        this(elem, type);
        this.initialIndex = initalIndex;
    }

    public Elems(Elem elem, Class<T> type, int initalIndex, int delta) {
        this(elem, type);
        this.initialIndex = initalIndex;
        this.delta = delta;
    }

    public Elems<T> assertion(String message) {
        assertIt = true;
        assertMessage = message;
        return this;
    }

    public List<T> get() {
        return isUFit ? UFit.initCollections(elem, type, initialIndex, delta, complex) : PageElements.initCollection(elem, type, initialIndex, delta);
    }

    public T get(Predicate<T> p, String message) {
        return get(p, message, timeout);
    }

    public T get(Predicate<T> p) {
        return get(p, elem.toString() + " NOT FOUND by custom predicate", timeout);
    }

    public T get(Predicate<T> p, String message, long timeout) {
        assertion(message).isContains(p, timeout);
        return get().stream().filter(p).findFirst().orElseThrow(() -> new AssertionError("Item NOT FOUND: " + message));
    }

    public boolean isContains(Predicate<T> p, long timeout) {
        return until(timeout, () -> get().stream().filter(p).findFirst().orElse(null) != null);
    }

    public <R> boolean isMappedListEquals(Function<? super T, ? extends R> mapper, List expected) {
        return isMappedListEquals(mapper, expected, timeout);
    }

    public <R> boolean isMappedListEquals(Function<? super T, ? extends R> mapper, List expected, long timeout) {
        return until(timeout, () -> get().stream().map(mapper).collect(Collectors.toList()).equals(expected));
    }

    public <R> boolean isMappedListNotEquals(Function<? super T, ? extends R> mapper, List expected) {
        return isMappedListNotEquals(mapper, expected, timeout);
    }

    public <R> boolean isMappedListNotEquals(Function<? super T, ? extends R> mapper, List expected, long timeout) {
        return until(timeout, () -> !get().stream().map(mapper).collect(Collectors.toList()).equals(expected));
    }

    public boolean isContains(Predicate<T> p) {
        return isContains(p, timeout);
    }

    public boolean isAllMatch(Predicate<T> p, long timeout) {
        return until(timeout, () -> get().stream().allMatch(p));
    }

    public boolean isAllMatch(Predicate<T> p) {
        return isAllMatch(p, timeout);
    }

    public boolean isAnyMatch(Predicate<T> p, long timeout) {
        return until(timeout, () -> get().stream().anyMatch(p));
    }

    public boolean isAnyMatch(Predicate<T> p) {
        return isAnyMatch(p);
    }

    public boolean isNumberOfItemsToBe(int size, long timeout) {
        return until(timeout, () -> get().size() == size);
    }

    public boolean isNumberOfItemsToBe(int size) {
        return isNumberOfItemsToBe(size, timeout);
    }

    public boolean isNumberOfItemsToBeMoreThen(int size, long timeout) {
        return until(timeout, () -> get().size() > size);
    }

    public boolean isNumberOfItemsToBeMoreThen(int size) {
        return isNumberOfItemsToBeMoreThen(size, timeout);
    }

    public boolean isNumberOfItemsToBeLessThen(int size, long timeout) {
        return until(timeout, () -> get().size() < size);
    }

    public boolean isNumberOfItemsToBeLessThen(int size) {
        return isNumberOfItemsToBeLessThen(size, timeout);
    }

    private boolean until(long timeout, Callable<Boolean> conditionEvaluator) {
        try {
            await()
                    .pollInSameThread()
                    .timeout(timeout, TimeUnit.SECONDS)
                    .ignoreException(AssertionError.class)
                    .until(conditionEvaluator);
            return true;
        } catch (ConditionTimeoutException e) {
            if (assertIt) {
                String message = "";
                if (assertMessage != null) {
                    message = assertMessage;
                }
                throw new AssertionError(message + " Timeout: " + timeout + " seconds");
            }
            return false;
        } finally {
            assertIt = false;
            assertMessage = null;
        }
    }

}
