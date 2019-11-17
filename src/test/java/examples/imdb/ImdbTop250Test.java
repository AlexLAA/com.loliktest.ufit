package examples.imdb;

import examples.imdb.ufit.items.MovieItem;
import examples.imdb.ufit.pages.SearchPage;
import examples.imdb.ufit.pages.Top250Page;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

import static com.loliktest.ufit.UFitBrowser.browser;

public class ImdbTop250Test {

    Top250Page page = new Top250Page();

    @Test
    public void getAll() {
        browser().get("https://www.imdb.com/chart/top");
        Top250Page page = new Top250Page();
        double rating = page.getMovieRatingByTitle("The Godfather");
        Assert.assertEquals(rating, 9.1, "Movie Rating Not as Expected");
        System.out.println(page.title.getBy());
        System.out.println(page.title.getName());
        List<MovieItem> movieItems = page.movieItems.get();
        movieItems.forEach(movie -> movie.title.assertion().isSelectionState(false, 0));

        // movieItems.forEach(i -> System.out.println(i.title.getText()));
        SearchPage searchPage = page.navigationBar.search("Star Wars");


    }

}
