package examples.imdb.ufit.xpath;

import com.loliktest.ufit.Elem;
import com.loliktest.ufit.Elems;
import com.loliktest.ufit.Selector;
import com.loliktest.ufit.UFit;

/**
 * Created by dbudim on 10.09.2020
 */

public class XpathPage {

    @Selector("//tbody//tr")
    public Elems<FilmXpathElem> films;

    public XpathPage() {
        UFit.initPage(this);
    }
}
