package tests;

import examples.imdb.ufit.xpath.FilmXpathPage;
import examples.imdb.ufit.xpath.XpathPage;
import org.testng.annotations.Test;

import static com.loliktest.ufit.UFitBrowser.browser;

/**
 * Created by dbudim on 10.09.2020
 */

public class ImdbXpath {


    @Test
    public void xpathTest() {
        browser().get("https://www.imdb.com/chart/top");
        XpathPage xpathPage = new XpathPage();
        xpathPage.films.get(f -> f.title.getText().contains("The Godfather"))
                .poster
                .click();
        new FilmXpathPage().title.assertion().isContainsText("The Godfather");
    }
}
