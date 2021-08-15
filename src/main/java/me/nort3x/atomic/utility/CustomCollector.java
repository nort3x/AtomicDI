package me.nort3x.atomic.utility;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class CustomCollector {

    private CustomCollector() {
    }


    public static <T> ConcurrentSet<T> concurrentSet() {
        return new ConcurrentSet<>();
    }

    static private class ConcurrentSet<T> implements Collector<T, Set<T>, Set<T>> {

        // Custom Collector ID for ConcurrentSet made of ConcurrentHashMap
        static private final Set<Collector.Characteristics> ID = Collections.unmodifiableSet(
                EnumSet.of(
                        Collector.Characteristics.UNORDERED,
                        Collector.Characteristics.IDENTITY_FINISH,
                        Collector.Characteristics.CONCURRENT
                )
        );

        // generator of Custom Collector

        @Override
        public Supplier<Set<T>> supplier() {
            return ConcurrentHashMap::newKeySet;
        }

        @Override
        public BiConsumer<Set<T>, T> accumulator() {
            return Set::add;
        }

        @Override
        public BinaryOperator<Set<T>> combiner() {
            return (ts, ts2) -> {
                ts.addAll(ts2);
                return ts;
            };
        }

        @Override
        public Function<Set<T>, Set<T>> finisher() {
            return Function.identity();
        }

        @Override
        public Set<Characteristics> characteristics() {
            return ID;
        }

    }
}
