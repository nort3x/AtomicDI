package me.nort3x.atomic.utility;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Utils {
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
