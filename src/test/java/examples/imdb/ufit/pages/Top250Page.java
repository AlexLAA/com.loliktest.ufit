package examples.imdb.ufit.pages;

import com.loliktest.ufit.Elem;
import com.loliktest.ufit.Elems;
import com.loliktest.ufit.Selector;
import com.loliktest.ufit.UFit;
import examples.imdb.ufit.elems.NavbarElem;
import examples.imdb.ufit.items.MovieItem;
import io.qameta.allure.Step;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static com.loliktest.ufit.UFitBrowser.browser;

public class Top250Page {

    @Selector("h1.header")
    public Elem title;

    @Selector("#nb_search")
    public NavbarElem navigationBar;

    @Selector(".lister-list > tr")
    public Elems<MovieItem> movieItems;

    @Selector(".lister-list > tr")
    public Elems<Elem> elemItems;

    public Top250Page() {
        UFit.initPage(this);
    }

    public void assertIsOpened(){
        browser().wait.assertion("PAGE NOT OPENED").isUrlContains("/top22", 5);
    }

    @Step
    public double getMovieRatingByTitle(String movieTitle) {
        return Double.parseDouble(movieItems.get(movie -> movie.title.getText().equals(movieTitle)).rating.getText());
    }

}
