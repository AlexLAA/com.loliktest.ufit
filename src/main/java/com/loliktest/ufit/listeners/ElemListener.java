package com.loliktest.ufit.listeners;

import com.loliktest.ufit.Browser;
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

}
