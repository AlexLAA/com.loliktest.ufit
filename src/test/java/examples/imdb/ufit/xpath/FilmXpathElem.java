package examples.imdb.ufit.xpath;

import com.loliktest.ufit.Elem;
import com.loliktest.ufit.Selector;

/**
 * Created by dbudim on 10.09.2020
 */

public class FilmXpathElem {

    @Selector("//*[@class='titleColumn']")
    public Elem title;

    @Selector("//*[@class='ratingColumn imdbRating']")
    public Elem rating;

    @Selector("//*[@class='posterColumn']")
    public Elem poster;
}
