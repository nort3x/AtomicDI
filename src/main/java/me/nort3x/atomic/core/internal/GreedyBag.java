package me.nort3x.atomic.core.internal;

import me.nort3x.atomic.logger.AtomicLogger;
import me.nort3x.atomic.wrappers.AtomicAnnotation;
import me.nort3x.atomic.wrappers.AtomicType;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GreedyBag {

    AtomicLogger logger = AtomicLogger.getInstance();


    private final ConcurrentHashMap<Class<? extends Annotation>, AtomicAnnotation> annotationsToWrappers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<?>, AtomicType> typesToWrappers = new ConcurrentHashMap<>();


    protected void load(Class<?> type) {
        typesToWrappers.computeIfAbsent(type, AtomicType::new);
    }

    public AtomicAnnotation getAtomicAnnotation(Class<? extends Annotation> annotation) {
        return AtomicAnnotation.getOrCreate(annotation);
    }

    //// utility ////


    // will invert given RelationalMap
    public static <T, V> Map<T, Set<V>> invert(Map<V, Set<T>> src) {

        ConcurrentHashMap<T, Set<V>> answer = new ConcurrentHashMap<>();

        src.keySet().parallelStream()
                .forEach(key -> src.get(key).parallelStream()
                        .forEach(value ->
                                answer.computeIfAbsent(value, givenValue ->
                                        ConcurrentHashMap.newKeySet()).add(key)
                        )
                );

        return answer;
    }
}
