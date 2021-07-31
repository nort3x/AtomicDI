package me.neiizun.lightdrop.atomic.reactor;

import java.util.function.Consumer;

public interface ReactorLike<T> {
    void addReaction(Consumer<T> o);
    void sealReactor();
}
