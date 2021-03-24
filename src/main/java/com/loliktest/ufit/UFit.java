package com.loliktest.ufit;

import com.google.common.base.CaseFormat;
import com.loliktest.ufit.exceptions.UFitException;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import static com.loliktest.ufit.SelectorUtils.getBy;


public class UFit {

    private static final Class<? extends Annotation> SELECTOR_TYPE;
    private static Logger logger = LoggerFactory.getLogger(UFit.class);

    static {

        switch (getSelectorStrategy()) {
            case "ios":
                SELECTOR_TYPE = SelectorIOS.class;
                break;

            case "android":
                SELECTOR_TYPE = SelectorAndroid.class;
                break;

            default:
                SELECTOR_TYPE = Selector.class;
                break;
        }

        logger.info("Selector type: " + SELECTOR_TYPE.getSimpleName());
    }

    public static void initPage(Object thisObject) {
        initElements(thisObject, null);
    }

    static void initElements(Object elements, Elem parent) {
        List<Field> superFields = Arrays.asList(elements.getClass().getSuperclass().getDeclaredFields());
        List<Field> thisFields = Arrays.asList(elements.getClass().getDeclaredFields());

        Field[] fields = ArrayUtils.addAll(elements.getClass().getSuperclass().getDeclaredFields(), elements.getClass().getDeclaredFields());
        for (Field field : fields) {
            if (field.isAnnotationPresent(SELECTOR_TYPE)) {
                String selector = getSelectorParam("value", field.getAnnotation(SELECTOR_TYPE)).toString();
                if (!superFields.isEmpty() && thisFields.stream().anyMatch(f -> f.getName().equals(field.getName()))) {
                    selector = getSelectorParam("value", thisFields.stream().filter(f -> f.getName().equals(field.getName())).findFirst().get().getAnnotation(SELECTOR_TYPE)).toString();
                }
                if (field.getType().equals(Elem.class) || field.getType().equals(MobileElem.class)) {
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
        if (field.getType().equals(Elem.class) || field.getType().equals(MobileElem.class)) {
            try {
                Elem elem = field.getType().equals(Elem.class)
                        ? new Elem(getBy(selector), camelToText(field.getName()))
                        : new MobileElem(getBy(selector), camelToText(field.getName()));
                if (parent != null) elem.setParent(parent);
                field.set(object, elem);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static void initCustomElem(Object object, Field field, Elem parent, String selector) {
        if (field.isAnnotationPresent(SELECTOR_TYPE) && !(field.getType().equals(Elem.class) || field.getType().equals(MobileElem.class))) {
            try {
                Elem elem = field.getType().equals(Elem.class)
                        ? new Elem(getBy(selector), camelToText(field.getName()))
                        : new MobileElem(getBy(selector), camelToText(field.getName()));
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
        if (field.isAnnotationPresent(SELECTOR_TYPE)) {
            Elem elem = SELECTOR_TYPE.equals(Selector.class)
                    ? new Elem(getBy(selector), camelToText(field.getName()))
                    : new MobileElem(getBy(selector), camelToText(field.getName()));

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

                Field complex = ((Elems) inst).getClass().getDeclaredField("complex");
                complex.setAccessible(true);
                complex.set(inst, getSelectorParam("searchIndex", field.getAnnotation(SELECTOR_TYPE)));

                Field initialIndex = ((Elems) inst).getClass().getDeclaredField("initialIndex");
                initialIndex.setAccessible(true);
                initialIndex.set(inst, getSelectorParam("initialIndex", field.getAnnotation(SELECTOR_TYPE)));

                Field delta = ((Elems) inst).getClass().getDeclaredField("delta");
                delta.setAccessible(true);
                delta.set(inst, getSelectorParam("delta", field.getAnnotation(SELECTOR_TYPE)));
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

    static List initCollections(Elem elem, Class cl, int initialIndex, int delta, boolean complex) {
        AtomicInteger count = new AtomicInteger(initialIndex);
        List collection = new ArrayList<>();
        if (complex) {
            return initComplexCollection(elem, cl, initialIndex, delta, complex);
        }
        if (cl.getSimpleName().equals("Elem")) {
            elem.finds().forEach(e -> collection.add(elem.setIndex(count.getAndAdd(delta))));
        } else if (cl.getSimpleName().equals("MobileElem")) {
            MobileElem mobileElem = new MobileElem(elem.getBy());
            mobileElem.finds().forEach(e -> collection.add(mobileElem.setIndex(count.getAndAdd(delta))));
        } else {
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
        }
        return collection;
    }

    static List initComplexCollection(Elem elem, Class cl, int initialIndex, int delta, boolean complex) {
        AtomicInteger count = new AtomicInteger(initialIndex);
        List collection = new ArrayList<>();
        int size = elem.finds().size();
        while (size != collection.size()) {
            int index = count.get();
            if (count.get() > 1000) {
                return collection;
            }
            if (elem.setIndex(index).isPresent(0)) {
                if (cl.getSimpleName().equals("Elem")) {
                    collection.add(elem.setIndex(index));
                } else if (cl.getSimpleName().equals("MobileElem")) {
                    collection.add(new MobileElem(elem.getBy()).setIndex(index));
                } else {
                    try {
                        Object o = cl.newInstance();
                        initElements(o, elem.setIndex(index));
                        collection.add(o);
                    } catch (InstantiationException ex) {
                        ex.printStackTrace();
                    } catch (IllegalAccessException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            count.incrementAndGet();
        }
        return collection;
    }

    private static String camelToText(String text) {
        String str = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, text).replaceAll("_", " ");
        String cap = str.substring(0, 1).toUpperCase() + str.substring(1);
        return cap;
    }

    private static Object getSelectorParam(String name, Annotation selector) {
        try {
            return selector.annotationType().getDeclaredMethod(name).invoke(selector);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new UFitException("Can't read selector parameter: " + name + e);
        }
    }

    private static String getSelectorStrategy() {
        String strategy = null;
        if (System.getProperty("ufit.selector.strategy") != null) {
            strategy = System.getProperty("ufit.selector.strategy");

        } else if (ClassLoader.getSystemResource("TestParameters.properties") != null) {
            Properties properties = new Properties();
            try {
                properties.load(ClassLoader.getSystemResource("TestParameters.properties").openStream());
                strategy = properties.getProperty("ufit.selector.strategy");
            } catch (IOException e) {
                logger.error("Can't load properties file. " + e);
            }
        }

        return strategy != null ? strategy : "";
    }

}
