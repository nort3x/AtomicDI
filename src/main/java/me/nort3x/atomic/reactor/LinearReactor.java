package me.nort3x.atomic.reactor;

public class LinearReactor<T> extends AbstractSaverReactor<T> {
    @Override
    public T reactOn(T t) {
        super.getSetOfReactions().forEach(reaction -> reaction.accept(t));
        return t;
    }
}
