package examples.imdb.ufit.pages;

import com.loliktest.ufit.Elem;
import com.loliktest.ufit.Elems;
import com.loliktest.ufit.Selector;
import examples.imdb.ufit.items.MovieItem;

public class Top250Page {

    @Selector(".title")
    public Elem pageTitle;

    @Selector(".asd")
    public Elems<MovieItem> movieItems;


}
