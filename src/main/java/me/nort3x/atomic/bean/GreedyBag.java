package me.nort3x.atomic.bean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

// when we scan once we keep it, faster easier!
class GreedyBag {


    // only use this when policies are loaded already
    private static final ArrayList<Class<?>> allLoadedTypes = new ArrayList<>();
    private static final ConcurrentHashMap<Class<? extends Annotation>, List<Class<?>>> everyAnnotatedType = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Class<?>, List<Field>> everyFieldScannedForAClass = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Class<?>, List<Method>> everyMethodScannedForAClass = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Class<?>, ConcurrentHashMap<Class<? extends Annotation>, List<Method>>> everyAnnotatedMethodOfClass = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Class<?>, List<Class<?>>> everyTypeDerivedFromKey = new ConcurrentHashMap<>();



    protected static ArrayList<Class<?>> getAllLoadedTypes() {
        return allLoadedTypes;
    }

    protected static ConcurrentHashMap<Class<? extends Annotation>, List<Class<?>>> getEveryAnnotatedType() {
        return everyAnnotatedType;
    }

    protected static ConcurrentHashMap<Class<?>, List<Field>> getEveryFieldScannedForAClass() {
        return everyFieldScannedForAClass;
    }

    protected static ConcurrentHashMap<Class<?>, List<Method>> getEveryMethodScannedForAClass() {
        return everyMethodScannedForAClass;
    }

    protected static ConcurrentHashMap<Class<?>, ConcurrentHashMap<Class<? extends Annotation>, List<Method>>> getEveryAnnotatedMethodOfClass() {
        return everyAnnotatedMethodOfClass;
    }

    protected static ConcurrentHashMap<Class<?>, List<Class<?>>> getEveryTypeDerivedFromKey() {
        return everyTypeDerivedFromKey;
    }
}
