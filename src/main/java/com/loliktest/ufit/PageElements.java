package com.loliktest.ufit;

import org.openqa.selenium.By;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by lolik on 17.07.2019
 */
@Deprecated
public class PageElements {

    private Elem setParentElem(Elem parent){
        //TODO
        return new Elem(By.cssSelector(""));
    }

    private Elem setIndex(int index){
        //TODO
        return new Elem(By.cssSelector(""));
    }


    public static void initItemElementsParent(Object elements, Elem parent){
        Field[] fields = elements.getClass().getFields();
        for (Field field : fields) {
            if(field.getType().equals(Elem.class)) {
                field.setAccessible(true);
                try {
                    ((Elem) field.get(elements)).setParent(parent);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else if(field.getType().toString().endsWith("Elem")){
                try{
                Field[] declaredFields = field.get(elements).getClass().getDeclaredFields();
                for (Field inner : declaredFields) {
                    if (inner.getName().equals("elements")) {
                        initItemElementsParent(inner.get(field.get(elements)), parent);
                    }
                }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    //TODO
    public static List initCollection(Elem elem, Class cl){
        return initCollection(elem, cl, 1);
    }

    public static List initCollection(Elem elem, Class cl, int initalIndex) {
       return initCollection(elem, cl, initalIndex, 1);
    }

    public static List initCollection(Elem elem, Class cl, int initalIndex, int delta) {
        AtomicInteger count = new AtomicInteger(initalIndex);
        return elem.finds().stream().map(e -> {
            try {
                Object o = cl.newInstance();
                Field[] declaredFields = o.getClass().getDeclaredFields();
                for (Field inner : declaredFields) {
                    if (inner.getName().equals("elements")) {
               /*         // TODO Make small method
                        Elem elemWithIndex;
                        int tries = 10;
                        do {
                            elemWithIndex = elem.setIndex(count.getAndAdd(delta));
                            tries--;
                            if(tries == 0){
                                break;
                            }
                        } while (elemWithIndex.isNotPresent(0));*/

                        initItemElementsParent(inner.get(o), elem.setIndex(count.getAndAdd(delta)));
                    }
                }
                return o;
            } catch (Exception e1) {
                e1.printStackTrace();
                return null;
            }
        }).collect(Collectors.toList());
    }

}
