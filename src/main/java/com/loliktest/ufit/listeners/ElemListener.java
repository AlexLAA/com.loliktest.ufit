package com.loliktest.ufit.listeners;

import com.loliktest.ufit.Elem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElemListener implements IElemListener {

    private Logger logger = LoggerFactory.getLogger(ElemListener.class);


    @Override
    public void click(Elem elem) {
        logger.info("Click: "+elem.toString());
    }

    @Override
    public void find(Elem elem) {

    }

    @Override
    public void type(String text, Elem elem) {
        logger.info("Type: "+text+" in Elem: "+elem.toString());
    }

}
