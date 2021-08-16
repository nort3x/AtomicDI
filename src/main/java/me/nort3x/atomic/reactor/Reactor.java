package me.nort3x.atomic.reactor;

import java.util.function.Consumer;

/**
 * any entity which can be named a Reactor should implement this
 */
public interface Reactor<T> {

    /**
     * inheritor should save this reactions
     *
     * @param reaction given reaction
     */
    void addReaction(Consumer<T> reaction);

    /**
     * @param t given object
     * @return given object such that all reaction have been formed on it
     */
    T reactOn(T t);

}
