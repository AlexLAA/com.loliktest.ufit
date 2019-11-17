# UFit
Created By LolikTest

With love for Automation team!

## Download

Maven Dependency

```
<dependency>
  <groupId>com.loliktest</groupId>
  <artifactId>ufit</artifactId>
  <version>0.0.6</version>
</dependency>
```

## General Info

`Elem` - Simple HTML Element with selector (button, link, input, etc.)

`CustomElem` - Complex Element that can contains inside `Elem, CustomElem, Elems<SomeItem>`

`Elems<SomeItem>` - Collection of Page Items

### Rules 

1. Do not use Thread.sleep()
2. Use Only CssSelectors


# How To Use

- [Elem](https://github.com/AlexLAA/com.loliktest.ufit#elem "Elem")
- [CustomElem](https://github.com/AlexLAA/com.loliktest.ufit#customelem "CustomElem")
- [Elems](https://github.com/AlexLAA/com.loliktest.ufit#elems "Elems")
- [Page Example](https://github.com/AlexLAA/com.loliktest.ufit#page-example "Page Example")


## Elem
Forget about waits and complex actions
#### Declaration
``` java
   @Selector("input[name='q']")
   public Elem searchField;
```
#### Usage
``` java
     searchField.sendKeys(text);
     searchField.isPresent(10); // true
     searchField.isContainsText("Jack Sparrow", 0);
     searchField.assertion().isPresent(5);
```
  

## CustomElem
#### Declaration
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

#### Usage
``` java
    SearchPage searchPage = page.navigationBar.search("Star Wars");
```

## Elems
#### Declaration
``` java
   @Selector(".lister-list > tr:nth-child(n)")
   public Elems<MovieItem> movieItems;
```

#### Usage
``` java
    List<MovieItem> movieItems = page.movieItems.get();
    movieItems.forEach(movie -> movie.title.assertion().isSelectionState(false, 0));
```

 
## Page Example
All Pages MUST have constructor to init Elements:
``` java
public MyPage() {
    UFit.initPage(this);
}
```

#### Declaration
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

    @Step
    public double getMovieRatingByTitle(String movieTitle){
        return Double.parseDouble(movieItems.get(movie -> movie.title.getText().equals(movieTitle)).rating.getText());
    }
}
```
#### Usage
``` java
    Top250Page page = new Top250Page();
    double rating = page.getMovieRatingByTitle("The Godfather");
    Assert.assertEquals(rating, 9.1, "Movie Rating Not as Expected");
 ```

