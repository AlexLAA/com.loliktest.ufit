package tests;


public class Example {

    public static SomePage page = new SomePage();

    public static void main(String[] args) {
        System.out.println(page.elem.getBy());
        System.out.println(page.elem.getName());
    }
}
