package me.nort3x.atomic.reactor;

import java.util.List;
import java.util.function.Consumer;

public interface ReactorLike<T> {
    void addReaction(Consumer<T> o);
    void sealReactor();

    default void addReactions(List<Consumer<T>> reactions) {
        reactions.forEach(this::addReaction);
    }
}
