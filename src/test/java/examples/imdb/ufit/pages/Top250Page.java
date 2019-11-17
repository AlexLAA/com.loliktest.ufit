package examples.imdb.ufit.pages;

import com.loliktest.ufit.Elem;
import com.loliktest.ufit.Elems;
import com.loliktest.ufit.Selector;
import com.loliktest.ufit.UFit;
import examples.imdb.ufit.elems.NavbarElem;
import examples.imdb.ufit.items.MovieItem;

public class Top250Page {

    @Selector("h1.header")
    public Elem title;

    @Selector("#nb_search")
    public NavbarElem navigationBar;

    @Selector(".lister-list > tr")
    public Elems<MovieItem> movieItems;

    public Top250Page() {
        UFit.initPage(this);
    }

}
