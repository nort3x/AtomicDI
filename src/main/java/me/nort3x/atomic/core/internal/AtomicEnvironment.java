package me.nort3x.atomic.core.internal;

import me.nort3x.atomic.utility.CustomCollector;
import me.nort3x.atomic.wrappers.AtomicAnnotation;
import me.nort3x.atomic.wrappers.AtomicMethod;
import me.nort3x.atomic.wrappers.AtomicType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * will provide basic inspection API to {@link me.nort3x.atomic.basic.AtomicModule}
 */
public class AtomicEnvironment {

    private final static ConcurrentHashMap<AtomicAnnotation, Set<AtomicType>> typesAnnotatedWith = new ConcurrentHashMap<>();
    private final static ConcurrentHashMap<AtomicType, Set<AtomicType>> subTypedFrom = new ConcurrentHashMap<>();
    private final static ConcurrentHashMap<AtomicType, ConcurrentHashMap<AtomicAnnotation, Set<AtomicMethod>>> type_method_annot = new ConcurrentHashMap<>();

    public Set<AtomicType> getAllAtomicTypesAnnotatedWith(AtomicAnnotation atomicAnnotation) {
        return typesAnnotatedWith.computeIfAbsent(atomicAnnotation,
                annotation ->
                        AtomicType.getAlreadyScannedTypesAtomicTypes().values().stream()
                                .parallel()
                                .filter(atomicType -> atomicType.isAnnotationPresent(annotation))
                                .collect(CustomCollector.concurrentSet())
        );

    }

    public Set<AtomicType> getAllAtomicTypesDerivedFromAtomicType(AtomicType atomicType) {
        return subTypedFrom.computeIfAbsent(atomicType,
                atomicType1 ->
                        AtomicType.getAlreadyScannedTypesAtomicTypes().values().stream()
                                .parallel()
                                .filter(type -> type.isSubOf(atomicType1))
                                .collect(CustomCollector.concurrentSet())
        );
    }

    /**
     * can be expensive, very expensive
     *
     * @param atomicAnnotation
     * @return
     */
    public Set<AtomicMethod> getAllInteractionAnnotatedWith(AtomicAnnotation atomicAnnotation) {
        return AtomicType.getAlreadyScannedTypesAtomicTypes().values().parallelStream().map(x -> getAllInteractionOfTypeAnnotatedWith(atomicAnnotation, x))
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    public Set<AtomicMethod> getAllInteractionOfTypeAnnotatedWith(AtomicAnnotation atomicAnnotation, AtomicType atomicType) {
        return type_method_annot.computeIfAbsent(atomicType, type -> new ConcurrentHashMap<>())
                .computeIfAbsent(atomicAnnotation, annotation -> {
                    return atomicType.getMethodSet().parallelStream().filter(x -> x.getCorrespondingMethod().isAnnotationPresent(annotation.getCorrespondingAnnotation()))
                            .collect(CustomCollector.concurrentSet());
                });
    }

    public static <T extends Annotation> Optional<T> getAnnotationIfExist(Class<T> annotation, Field f) {
        Optional<T> ans = Optional.ofNullable(f.getAnnotation(annotation)); // its either annotated itself
        if (ans.isPresent())
            return ans;

        // or its annotations
        return Arrays.stream(f.getDeclaredAnnotations()).parallel()
                .map(Annotation::annotationType)
                .map(AtomicAnnotation::of)
                .map(AtomicAnnotation::getAnnotationSet)// unwrap fields annotations
                .flatMap(Set::stream)
                .map(x -> x.getAnnotation(annotation))
                .filter(Objects::nonNull)
                .findFirst();

    }

    public static <T extends Annotation> Optional<T> getAnnotationIfExist(Class<T> annotation, Method f) {
        Optional<T> ans = Optional.ofNullable(f.getAnnotation(annotation)); // its either annotated itself
        if (ans.isPresent())
            return ans;

        // or its annotations
        return Arrays.stream(f.getDeclaredAnnotations()).parallel()
                .map(Annotation::annotationType)
                .map(AtomicAnnotation::of)
                .map(AtomicAnnotation::getAnnotationSet)// unwrap fields annotations
                .flatMap(Set::stream)
                .map(x -> x.getAnnotation(annotation))
                .filter(Objects::nonNull)
                .findFirst();

    }

    public static <T extends Annotation> Optional<T> getAnnotationIfExist(Class<T> annotation, Class<?> f) {
        Optional<T> ans = Optional.ofNullable(f.getAnnotation(annotation)); // its either annotated itself
        if (ans.isPresent())
            return ans;

        // or its annotations
        return Arrays.stream(f.getDeclaredAnnotations()).parallel()
                .map(Annotation::annotationType)
                .map(AtomicAnnotation::of)
                .map(AtomicAnnotation::getAnnotationSet)// unwrap fields annotations
                .flatMap(Set::stream)
                .map(x -> x.getAnnotation(annotation))
                .filter(Objects::nonNull)
                .findFirst();

    }

    public static <T extends Annotation> Optional<T> getAnnotationIfExist(Class<T> annotation, Parameter f) {
        Optional<T> ans = Optional.ofNullable(f.getAnnotation(annotation)); // its either annotated itself
        if (ans.isPresent())
            return ans;

        // or its annotations
        return Arrays.stream(f.getDeclaredAnnotations()).parallel()
                .map(Annotation::annotationType)
                .map(AtomicAnnotation::of)
                .map(AtomicAnnotation::getAnnotationSet)// unwrap fields annotations
                .flatMap(Set::stream)
                .map(x -> x.getAnnotation(annotation))
                .filter(Objects::nonNull)
                .findFirst();

    }


    public static <T extends Annotation> Optional<T> getAnnotationIfExist(Class<T> annotation, Constructor<?> f) {
        Optional<T> ans = Optional.ofNullable(f.getAnnotation(annotation)); // its either annotated itself
        if (ans.isPresent())
            return ans;

        // or its annotations
        return Arrays.stream(f.getDeclaredAnnotations()).parallel()
                .map(Annotation::annotationType)
                .map(AtomicAnnotation::of)
                .map(AtomicAnnotation::getAnnotationSet)// unwrap fields annotations
                .flatMap(Set::stream)
                .map(x -> x.getAnnotation(annotation))
                .filter(Objects::nonNull)
                .findFirst();

    }

}
