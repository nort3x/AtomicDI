package me.nort3x.atomic.reactor;

public class ParallelReactor<T> extends AbstractSaverReactor<T> {
    @Override
    public T reactOn(T t) {
        super.getSetOfReactions().parallelStream().forEach(x -> x.accept(t));
        return t;
    }
}
