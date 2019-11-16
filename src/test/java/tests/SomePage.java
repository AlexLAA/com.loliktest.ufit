package tests;

import com.loliktest.ufit.Elem;
import com.loliktest.ufit.Selector;
import com.loliktest.ufit.UFit;

public class SomePage {

    @Selector(".lolik")
    public Elem elem;




    public SomePage(){
        UFit.initPage(this);
    }


}
