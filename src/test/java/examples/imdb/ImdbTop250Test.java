package examples.imdb;

import examples.imdb.ufit.items.MovieItem;
import examples.imdb.ufit.pages.Top250Page;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;

import java.util.List;

import static com.loliktest.ufit.UFitBrowser.browser;

public class ImdbTop250Test {

    Top250Page page = new Top250Page();

    @Test
    public void getAll(){
        browser().get("https://www.imdb.com/chart/top");
        List<MovieItem> movieItems = page.movieItems.get();

    }

}
