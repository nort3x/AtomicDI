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

    // load atomic into scope
    public synchronized static void loadAllLoadedAtomic(Collection<Class<?>> atomics, Class<? extends AtomicDIModule> moduleClazz) {
        atomics.forEach(x -> GreedyBag.getEveryTypeToFavorPoint().putIfAbsent(x, moduleClazz));
        GreedyBag.getEveryTypeLoadedInFavorOfPoint().computeIfAbsent(moduleClazz, clazzAsKey -> new ArrayList<>()).addAll(atomics);
        GreedyBag.getAllLoadedTypes().addAll(atomics);
    }


    public static List<Method> getAllMethodsFor(Class<?> clazz) {
        return GreedyBag.getEveryMethodScannedForAClass().computeIfAbsent(clazz, x -> Arrays.asList(x.getDeclaredMethods()))
                .stream().filter(x -> !x.isAnnotationPresent(Exclude.class)).peek(ReflectionUtils::setAccessible).collect(Collectors.toList());
    }

    @Deprecated
    public static List<Method> getAccessibleMethodsFor(Class<?> clazz, Object callerInstance) {
        return Arrays.stream(clazz.getDeclaredMethods()).filter(x -> !x.isAnnotationPresent(Exclude.class)).filter(AccessibleObject::isAccessible).collect(Collectors.toList());
    }


    public static List<Field> getAllFieldsFor(Class<?> clazz){
        return GreedyBag.getEveryFieldScannedForAClass().computeIfAbsent(clazz, x -> Arrays.asList(x.getDeclaredFields()))
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
        return GreedyBag.getEveryAnnotatedMethodOfClass().computeIfAbsent(src, x -> new ConcurrentHashMap<>()).computeIfAbsent(annotation, an -> getAllMethodsFor(src).stream().filter(x -> !x.isAnnotationPresent(Exclude.class)).filter(x -> x.isAnnotationPresent(annotation))
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

    public static List<Class<?>> getAllAtomicAnnotatedWith(Class<? extends Annotation> annotation) {
        return GreedyBag.getEveryAnnotatedType().computeIfAbsent(annotation, x -> GreedyBag.getAllLoadedTypes().stream().filter(q -> !q.isAnnotationPresent(Exclude.class)).filter(y -> y.isAnnotationPresent(annotation)).collect(Collectors.toList()));
    }

    public static List<Class<?>> getAllAtomicDerivedFrom(Class<?> clazz) {
        return GreedyBag.getEveryTypeDerivedFromKey().computeIfAbsent(clazz, x -> GreedyBag.getAllLoadedTypes().stream().filter(q -> !q.isAnnotationPresent(Exclude.class)).filter(clazz::isAssignableFrom).collect(Collectors.toList()));
    }


    public static List<Class<?>> getAllAtomicInModuleDerivedFrom(Class<?> clazz, Class<? extends AtomicDIModule> module) {
        Collection<Class<?>> atomics = GreedyBag.getEveryTypeLoadedInFavorOfPoint().getOrDefault(module, null);
        if (atomics == null)
            return new ArrayList<>();

        return atomics.stream()
                .filter(q -> !q.isAnnotationPresent(Exclude.class)).filter(clazz::isAssignableFrom)
                .collect(Collectors.toList());
    }

    public static Class<? extends AtomicDIModule> getCorrespondingModule(Class<?> atomic) {
        Class<? extends AtomicDIModule> ans = null;
        for (Class<? extends AtomicDIModule> atomicDIModule : GreedyBag.getEveryTypeLoadedInFavorOfPoint().keySet()) {
            if (GreedyBag.getEveryTypeLoadedInFavorOfPoint().get(atomicDIModule).contains(atomic)) {
                ans = atomicDIModule;
                break;
            }
        }
        return ans;
    }

}
