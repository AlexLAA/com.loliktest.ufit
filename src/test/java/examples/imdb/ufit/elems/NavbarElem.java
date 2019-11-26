package examples.imdb.ufit.elems;

import com.loliktest.ufit.Elem;
import com.loliktest.ufit.Selector;
import examples.imdb.ufit.pages.SearchPage;
import io.qameta.allure.Step;

public class NavbarElem {

    @Selector("input[name='q']")
    public Elem searchField;

    @Selector("button")
    public Elem searchButton;

    @Step
    public SearchPage search(String text) {
        searchField.sendKeys(text);
        searchField.isPresent(10);
        searchField.assertion().isPresent(5);
        searchField.isContainsText("Jack Sparrow", 0);
        searchButton.click();
        return new SearchPage();
    }


}
