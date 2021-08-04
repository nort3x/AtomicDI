package me.nort3x.atomic.reactor;

/**
 * {@inheritDoc}
 * linear version of {@link ParallelReactor}
 */
public class LinearReactor<T> extends ParallelReactor<T>{

    @Override
    public T actOn(T t) {
        super.reactions.forEach(x->x.accept(t));
        return t;
    }
}
