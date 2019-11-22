package com.loliktest.ufit.listeners;

import com.loliktest.ufit.Elem;

public interface IElemListener {

    void click(Elem elem);

    void find(Elem elem);

    void type(String text, Elem elem);

}
