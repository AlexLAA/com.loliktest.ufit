# UFit
Created By LolikTest

With love for team!



## General Info

`Elem` - Simple HTML Element with selector (button, link, input, etc.)

`CustomElem` - Complex Element that can contains inside `Elem, CustomElem, Elems<SomeItem>`

`Elems<SomeItem>` - Collection of Page Items

### Rules 

1. Do not use Thread.sleep()
2. Use Only CssSelectors


# How To Use
- [Elem](https://github.com/AlexLAA/com.loliktest.ufit "Elem")
- [CustomElem](https://github.com/AlexLAA/com.loliktest.ufit "CustomElem")
- [Elems](https://github.com/AlexLAA/com.loliktest.ufit "Elems")


## Elem

``` java
   @Selector("input[name='q']")
   public Elem searchField;
```

## CustomElem
Declaration in page or another CustomElem
``` java
    @Selector("#nb_search") // Parent Selector
    public NavbarElem navigationBar;
```
NavbarElem.class
``` java
public class NavbarElem {

    @Selector("input[name='q']") //child
    public Elem searchField;

    @Selector("button") //child
    public Elem searchButton;

    @Step
    public SearchPage search(String text) {
        searchField.sendKeys(text);
        searchButton.click();
        return new SearchPage();
    }
}
```

## Elems

``` java
   @Selector(".lister-list > tr:nth-child(n)")
   public Elems<MovieItem> movieItems;
```

## Page Example

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
