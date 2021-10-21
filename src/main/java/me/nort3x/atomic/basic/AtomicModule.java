package me.nort3x.atomic.basic;


import me.nort3x.atomic.annotation.Atomic;
import me.nort3x.atomic.core.internal.AtomicEnvironment;

/**
most fundamental block for introducing Module to AtomicDI
inheritor should implement given methods and annotate it as {@link Atomic}
 */
@Atomic
public abstract class AtomicModule {

    /**
     * @return module name
     */
    abstract public String getName();

    /**
     * @return module version
     */
    abstract public int getVersion();

    /**
     * @param atomicEnvironment
     * @param args
     */
    abstract public void onLoad(AtomicEnvironment atomicEnvironment, String[] args);

    /**
     * @param atomicEnvironment
     */
    abstract public void onStart(AtomicEnvironment atomicEnvironment);

    abstract public void onStop(AtomicEnvironment atomicEnvironment);

    abstract public void onRestart(AtomicEnvironment atomicEnvironment);

}
