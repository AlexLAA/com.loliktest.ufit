package tests;

import com.loliktest.ufit.Elem;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import static com.loliktest.ufit.UFitBrowser.browser;

public class TestBrowserMethods {

    @Test
    public void notPresent(){
        browser().get("https://www.imdb.com/chart/top");
        browser().sleep(1000);
        System.out.println("COOKIES IMDB");
        browser().driver().manage().getCookies().forEach(c -> System.out.println(c.getDomain()+":"+c.getName()));
        browser().get("https://google.com");
        browser().sleep(1000);
        System.out.println("COOKIES GOOGLE");
        browser().driver().manage().getCookies().forEach(c -> System.out.println(c.getDomain()+":"+c.getName()));
        browser().deleteAllCookies();
        System.out.println("AFTER DELETE COOKIES");
        browser().driver().manage().getCookies().forEach(c -> System.out.println(c.getDomain()+":"+c.getName()));
    }
}
