package me.neiizun.lightdrop.atomic.reactor;

import java.lang.annotation.Documented;
import java.util.function.Consumer;

/**
 {@inheritDoc}
 */
public class LinearReactor<T> extends ParallelReactor<T>{

    @Override
    public T actOn(T t) {
        super.reactions.forEach(x->x.accept(t));
        return t;
    }
}
