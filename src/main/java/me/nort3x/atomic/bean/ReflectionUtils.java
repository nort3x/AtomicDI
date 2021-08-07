package me.nort3x.atomic.bean;

import me.nort3x.atomic.annotation.Exclude;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ReflectionUtils {

    public synchronized static void loadAllLoadedAtomic(Collection<Class<?>> atomics) {
        GreedyBag.allLoadedAtomic.addAll(atomics);
    }

    // when we scan once we keep it, faster easier!
    private static class GreedyBag{

        private static final ConcurrentHashMap<Class<?>,List<Field>> everyFieldScanned = new ConcurrentHashMap<>();
        private static final ConcurrentHashMap<Class<?>,List<Method>> everyMethodScanned = new ConcurrentHashMap<>();

        private static final ConcurrentHashMap<Class<?>,List<Field>> everyAtomFieldScanned = new ConcurrentHashMap<>();
        private static final ConcurrentHashMap<Class<?>,ConcurrentHashMap<Class<?extends Annotation>,List<Method>>> everyAnnotatedMethodScanned = new ConcurrentHashMap<>();
        private static final ConcurrentHashMap<Class<?extends Annotation>,List<Class<?>>> everyAtomicAnnotatedClassScanned = new ConcurrentHashMap<>();
        private static final ArrayList<Class<?>> allLoadedAtomic = new ArrayList<>();
        private static final ConcurrentHashMap<Class<?>,List<Class<?>>> everyAtomicDerivedFromKey = new ConcurrentHashMap<>();

    }


    public static List<Method> getAllMethodsFor(Class<?> clazz){
        return GreedyBag.everyMethodScanned.computeIfAbsent(clazz, x -> Arrays.asList(x.getDeclaredMethods()))
                .stream().filter(x -> !x.isAnnotationPresent(Exclude.class)).peek(ReflectionUtils::setAccessible).collect(Collectors.toList());
    }

    @Deprecated
    public static List<Method> getAccessibleMethodsFor(Class<?> clazz,Object callerInstance){
        return Arrays.stream(clazz.getDeclaredMethods()).filter(x -> !x.isAnnotationPresent(Exclude.class)).filter(AccessibleObject::isAccessible).collect(Collectors.toList());
    }


    public static List<Field> getAllFieldsFor(Class<?> clazz){
        return GreedyBag.everyFieldScanned.computeIfAbsent(clazz, x -> Arrays.asList(x.getDeclaredFields()))
                .stream().filter(x -> !x.isAnnotationPresent(Exclude.class)).peek(ReflectionUtils::setAccessible).collect(Collectors.toList());
    }

    @Deprecated
    public static List<Field> getAccessibleFieldsFor(Class<?> clazz,Object callerInstance){
        return Arrays.stream(clazz.getDeclaredFields()).filter(x -> !x.isAnnotationPresent(Exclude.class)).filter(AccessibleObject::isAccessible).collect(Collectors.toList());
    }

    public static Optional<Constructor<?>> getNoArgsConstructor(Class<?> clazz) {
        try {
            Constructor<?> constructor = clazz.getConstructor();
            constructor.setAccessible(true);
            return Optional.of(constructor);
        } catch (NoSuchMethodException e) {
            return Optional.empty();
        }
    }

    public static List<Method> getMethodsAnnotatedWith(Class<?> src, Class<? extends Annotation> annotation){
        return GreedyBag.everyAnnotatedMethodScanned.computeIfAbsent(src, x -> new ConcurrentHashMap<>()).computeIfAbsent(annotation, an -> getAllMethodsFor(src).stream().filter(x -> !x.isAnnotationPresent(Exclude.class)).filter(x -> x.isAnnotationPresent(annotation))
                .collect(Collectors.toList()));

    }

    public static List<Constructor<?>> getAllConstructors(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredConstructors()).filter(x -> !x.isAnnotationPresent(Exclude.class)).peek(x -> x.setAccessible(true)).collect(Collectors.toList());
    }

    private static void setAccessible(Method m){
        m.setAccessible(true);
    }

    private static void setAccessible(Field f){
        f.setAccessible(true);
    }

    public static List<Class<?>> getAllAtomicAnnotatedWith(Class<? extends Annotation> annotation){
        return GreedyBag.everyAtomicAnnotatedClassScanned.computeIfAbsent(annotation, x -> GreedyBag.allLoadedAtomic.stream().filter(q -> !q.isAnnotationPresent(Exclude.class)).filter(y -> y.isAnnotationPresent(annotation)).collect(Collectors.toList()));
    }

    public static List<Class<?>> getAllAtomicDerivedFrom(Class<?> clazz){
        return GreedyBag.everyAtomicDerivedFromKey.computeIfAbsent(clazz, x -> GreedyBag.allLoadedAtomic.stream().filter(q -> !q.isAnnotationPresent(Exclude.class)).filter(clazz::isAssignableFrom).collect(Collectors.toList()));
    }

}
