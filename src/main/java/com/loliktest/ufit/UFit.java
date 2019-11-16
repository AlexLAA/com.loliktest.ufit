package com.loliktest.ufit;

import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.ArrayUtils;
import org.openqa.selenium.By;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class UFit {

    public static void initPage(Object thisObject) {
        initElements(thisObject, null);
    }

    static void initElements(Object elements, Elem parent) {
        List<Field> superFields = Arrays.asList(elements.getClass().getSuperclass().getDeclaredFields());
        List<Field> thisFields = Arrays.asList(elements.getClass().getDeclaredFields());

        Field[] fields = ArrayUtils.addAll(elements.getClass().getSuperclass().getDeclaredFields(), elements.getClass().getDeclaredFields());
        for (Field field : fields) {
            if (field.isAnnotationPresent(Selector.class)) {
                String selector = field.getAnnotation(Selector.class).value();
                if (!superFields.isEmpty() && thisFields.stream().anyMatch(f -> f.getName().equals(field.getName()))) {
                    selector = thisFields.stream().filter(f -> f.getName().equals(field.getName())).findFirst().get().getAnnotation(Selector.class).value();
                }
                if (field.getType().equals(Elem.class)) {
                    initElem(elements, field, parent, selector);
                } else if (field.getType().equals(Elems.class)) {
                    initElems(elements, field, parent, selector);
                } else {
                    initCustomElem(elements, field, parent, selector);
                }
            }
        }
    }


    private static void initElem(Object object, Field field, Elem parent, String selector) {
        if (field.getType().equals(Elem.class)) {
            try {
                Elem elem = new Elem(By.cssSelector(selector), camelToText(field.getName()));
                if (parent != null) elem.setParent(parent);
                field.set(object, elem);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static void initCustomElem(Object object, Field field, Elem parent, String selector) {
        if (field.isAnnotationPresent(Selector.class) && !field.getType().equals(Elem.class)) {
            try {
                Elem elem = new Elem(By.cssSelector(selector), camelToText(field.getName()));
                if (parent != null) {
                    elem.setParent(parent);
                }
                parent = elem;
                Object inst = field.getType().newInstance();
                field.set(object, inst);
                initElements(inst, parent);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    static void initElems(Object object, Field field, Elem parent, String selector) {
        if (field.isAnnotationPresent(Selector.class)) {
            Elem elem = new Elem(By.cssSelector(selector), camelToText(field.getName()));
            if (parent != null) {
                elem.setParent(parent);
            }
            parent = elem;
            try {
                Object inst = field.getType().newInstance();
                ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                Class<?> typeClass = (Class<?>) parameterizedType.getActualTypeArguments()[0];

                Field type = ((Elems) inst).getClass().getDeclaredField("type");
                type.setAccessible(true);
                type.set(inst, typeClass);

                Field isUFit = ((Elems) inst).getClass().getDeclaredField("isUFit");
                isUFit.setAccessible(true);
                isUFit.set(inst, true);

                Field initialIndex = ((Elems) inst).getClass().getDeclaredField("initialIndex");
                initialIndex.setAccessible(true);
                initialIndex.set(inst, field.getAnnotation(Selector.class).initialIndex());

                Field delta = ((Elems) inst).getClass().getDeclaredField("delta");
                delta.setAccessible(true);
                delta.set(inst, field.getAnnotation(Selector.class).delta());

                field.set(object, inst);

                ((Elems) inst).elem = elem;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }

    static List initCollections(Elem elem, Class cl, int initialIndex, int delta) {
        AtomicInteger count = new AtomicInteger(initialIndex);
        List collection = new ArrayList<>();
        elem.finds().forEach(e -> {
            try {
                Object o = cl.newInstance();
                initElements(o, elem.setIndex(count.getAndAdd(delta)));
                collection.add(o);
            } catch (InstantiationException ex) {
                ex.printStackTrace();
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
        });
        return collection;
    }

    private static String camelToText(String text) {
        String str = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, text).replaceAll("_", " ");
        String cap = str.substring(0, 1).toUpperCase() + str.substring(1);
        return cap;
    }

}
