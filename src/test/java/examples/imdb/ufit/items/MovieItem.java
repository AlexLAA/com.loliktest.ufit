package examples.imdb.ufit.items;

import com.loliktest.ufit.Elem;
import com.loliktest.ufit.Selector;

public class MovieItem {

    @Selector(".titleColumn > a")
    public Elem title;

    @Selector(".imdbRating > strong")
    public Elem rating;


    public String getTitle(){
        return title.getText();
    }
}
