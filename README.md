# UFit
Created By LolikTest

With love for team!

# General Info

`Elem` - Simple HTML Element with selector (button, link, input, etc.)

`CustomElem` - Complex Element that can contains inside `Elem, CustomElem, Elems<SomeItem>`

`Elems<SomeItem>` - Collection of Page Items

# Page Example

``` java
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
```

