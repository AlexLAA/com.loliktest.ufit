package tests;

import examples.imdb.ufit.pages.Top250Page;
import org.testng.annotations.Test;

import static com.loliktest.ufit.UFitBrowser.browser;

public class TestElems {

    Top250Page page = new Top250Page();

    @Test
    public void complexElems(){
        browser().get("https://www.imdb.com/chart/top");
        page.elemsRating.get().forEach(e -> System.out.println(e.title.getSelector()));
    }
}
