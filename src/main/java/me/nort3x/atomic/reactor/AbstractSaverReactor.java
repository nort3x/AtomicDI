package me.nort3x.atomic.reactor;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

// sorry for my bad naming!
public abstract class AbstractSaverReactor<T> implements Reactor<T> {

    private final Set<Consumer<T>> listOfReactions = ConcurrentHashMap.newKeySet();

    @Override
    public void addReaction(Consumer<T> reaction) {
        listOfReactions.add(reaction);
    }

    public Set<Consumer<T>> getSetOfReactions() {
        return listOfReactions;
    }
}
