package me.nort3x.atomic.core.internal;

import me.nort3x.atomic.logger.AtomicLogger;
import me.nort3x.atomic.logger.Priority;
import me.nort3x.atomic.wrappers.AtomicAnnotation;
import me.nort3x.atomic.wrappers.AtomicType;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GreedyBag {

    AtomicLogger logger = AtomicLogger.getInstance();

    protected void load(Class<?> type) {
        AtomicType at = AtomicType.getOrCreate(type);
        if (!at.isAtomic())
            logger.warning("NonAtomicTypeCalled to be loaded, you are facing a bug, please report this", Priority.VERY_IMPORTANT, GreedyBag.class);
    }

    public AtomicAnnotation getAtomicAnnotation(Class<? extends Annotation> annotation) {
        return AtomicAnnotation.getOrCreate(annotation);
    }

    public AtomicType getAtomicType(Class<?> type) {
        return AtomicType.getOrCreate(type);

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
