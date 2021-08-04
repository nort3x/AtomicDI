package me.nort3x.atomic.reactor;

import java.util.List;
import java.util.function.Consumer;

/**
 * entity such that can do some actions on given Object
 *
 * @param <T> reactor subject type
 */
public interface ReactorLike<T> {
    void addReaction(Consumer<T> o);

    void sealReactor();

    default void addReactions(List<Consumer<T>> reactions) {
        reactions.forEach(this::addReaction);
    }
}
