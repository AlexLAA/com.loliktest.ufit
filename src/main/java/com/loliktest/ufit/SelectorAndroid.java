package com.loliktest.ufit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by dbudim on 02.10.2020
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface SelectorAndroid {

    String value();

    int initialIndex() default 1; //FOR COLLECTIONS

    int delta() default 1; //STEP

    boolean searchIndex() default false;

}
