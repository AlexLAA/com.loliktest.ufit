package tests;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

public class TestAllureAttachments {


    @BeforeSuite
    public void beforeSuite(){
        System.out.println("BEFORE SUITE");
    }

    @BeforeClass
    public void before(){
        System.out.println("BEFORE TEST");
    }

    @Test
    public void failedTest(){
      //  throw new NullPointerException("NOOOOOO!");
    }


}
