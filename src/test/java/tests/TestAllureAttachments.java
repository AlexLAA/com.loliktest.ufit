package tests;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

public class TestAllureAttachments {


    @BeforeSuite
    public void beforeSuite(){
        throw new NullPointerException("BEFORE NOOOOOOOO!");
    }

    @BeforeClass
    public void before(){
        throw new NullPointerException("BEFORE NOOOOOOOO!");
    }

    @Test
    public void failedTest(){
      //  throw new NullPointerException("NOOOOOO!");
    }


}
