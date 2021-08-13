package me.nort3x.atomic.bean;

import me.nort3x.atomic.reactor.Factory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * acts as a public API between {@link DependencyGrapher}  and  {@link AtomicDISubModule}
 */
public class Provider {

    private final DependencyGrapher dependencyGrapher;

    public Provider(DependencyGrapher dependencyGrapher) {
        this.dependencyGrapher = dependencyGrapher;
    }

    String[] args;

    public void setArgs(String[] args) {
        this.args = args;
    }

    public String[] getArgs() {
        return args;
    }

    /**
     * @param annotation annotation type
     * @return list of all graphed types annotated with given annotation
     */
    public List<Class<?>> getAllAtomicAnnotatedWith(Class<? extends Annotation> annotation) {
        return ReflectionUtils.getAllAtomicAnnotatedWith(annotation);
    }

    /**
     * @param clazz superclass
     * @return all Atomic types derived from superclass
     */
    public List<Class<?>> getAllAtomicTypesDerivedFrom(Class<?> clazz) {
        return ReflectionUtils.getAllAtomicDerivedFrom(clazz);
    }

    /**
     * @param clazz superclass Class
     * @param <T>   superclass as Generic parameter
     * @return instances (either shared or created at calling upon this function) of all derived types from superclass
     */
    public <T> List<T> getAllAtomicInstancesDerivedFrom(Class<T> clazz) {
        return getAllAtomicTypesDerivedFrom(clazz).stream()
                .map(x -> dependencyGrapher.getFactoryOf(x).generate())
                .flatMap((Function<Optional<?>, Stream<?>>) o -> o.isPresent() ? Stream.of(o.get()) : Stream.empty())
                .map(clazz::cast)
                .collect(Collectors.toList());
    }

    /**
     * @param clazz whichclass
     * @return return corresponding factory associated with clazz or null if its not Atomic or scanned yet
     */
    public Factory<?> getFactoryOf(Class<?> clazz) {
        return dependencyGrapher.getFactoryOf(clazz);
    }

    /**
     * @param atomicToScanMethodsAt
     * @param annot
     * @return list of all methods annotated with given annot at given Atomic type
     */
    public List<Method> getMethodsAnnotatedWith(Class<?> atomicToScanMethodsAt, Class<? extends Annotation> annot) {
        return ReflectionUtils.getMethodsAnnotatedWith(atomicToScanMethodsAt, annot);
    }
}
