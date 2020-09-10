package examples.imdb.ufit.xpath;

import com.loliktest.ufit.Elem;
import com.loliktest.ufit.Selector;
import com.loliktest.ufit.UFit;

/**
 * Created by dbudim on 10.09.2020
 */

public class FilmXpathPage {

    @Selector("//*[@class='title_wrapper']")
    public Elem title;

    public FilmXpathPage() {
        UFit.initPage(this);
    }
}
