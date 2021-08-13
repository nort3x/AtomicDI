package me.nort3x.atomic.bean;

import me.nort3x.atomic.annotation.Atomic;

import java.util.function.Consumer;

/**
 * inheritors of this class will act as extension and will be called after primary creation of graph
 */
@Atomic
public abstract class AtomicDISubModule implements Consumer<Provider> {
    public abstract void whenAllSubModulesLoaded();

    public String getIdentifier() {
        return this.getClass().getName();
    }
}
