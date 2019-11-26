package examples.imdb.ufit.elems;

import com.loliktest.ufit.Elem;
import com.loliktest.ufit.Selector;

public class NavbarElemAlt extends NavbarElem {

    @Selector("input[name='name']")
    public Elem searchField;

}
