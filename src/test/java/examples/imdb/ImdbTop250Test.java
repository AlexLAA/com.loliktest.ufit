package examples.imdb;

import com.loliktest.ufit.Elem;
import com.loliktest.ufit.TestNgThread;
import examples.imdb.ufit.items.MovieItem;
import examples.imdb.ufit.pages.SearchPage;
import examples.imdb.ufit.pages.Top250Page;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

import static com.loliktest.ufit.UFitBrowser.browser;

public class ImdbTop250Test {

    Top250Page page = new Top250Page();

    @Test
    public void asda(){
        browser().get("https://www.imdb.com/chart/top");
        List<WebElement> finds = new Elem(By.cssSelector(".lister-list > tr:nth-child(n)")).finds();
        System.out.println(finds.get(2).toString());
       /* List<Elem> elems = page.elemItems.get();

        //
        elems.forEach(elem -> System.out.println(elem.getBy()));
        System.out.println("");*/

    }

    @Test
    public void getAll() {
       // browser().get("https://www.imdb.com/chart/top");
        Top250Page page = new Top250Page();


        System.out.println(page.navigationBar.searchField.getBy());
        System.out.println(page.navigationBar.searchField.getName());
      /*
        page.assertIsOpened();
        double rating = page.getMovieRatingByTitle("The Godfather");
        Assert.assertEquals(rating, 9.1, "Movie Rating Not as Expected");
        System.out.println(page.title.getBy());
        System.out.println(page.title.getName());
        List<MovieItem> movieItems = page.movieItems.get();
        MovieItem movieItem = page.movieItems.get(p -> p.rating.getText().equals("9.1"));
        MovieItem godfatherItem = page.movieItems.get(p -> p.title.getText().equals("Godfather"));




        movieItems.forEach(movie -> movie.title.assertion().isSelectionState(false, 0));

        // movieItems.forEach(i -> System.out.println(i.title.getText()));
        SearchPage searchPage = page.navigationBar.search("Star Wars");*/


    }

}
