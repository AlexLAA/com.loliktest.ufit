package tests;

import com.loliktest.ufit.Elem;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import static com.loliktest.ufit.UFitBrowser.browser;

public class TestElemMethods {

    @Test
    public void notPresent(){
        browser().get("https://www.imdb.com/chart/top");
        new Elem(By.cssSelector(".article")).assertion().isNotPresent(0);
    }
}
