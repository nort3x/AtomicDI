package me.nort3x.atomic.basic;


import me.nort3x.atomic.annotation.Atomic;
import me.nort3x.atomic.core.internal.AtomicEnvironment;

/*
most fundamental block for introducing Module to AtomicDI
inheritor should implement given methods and annotate
 */
@Atomic
public abstract class AtomicModule {

    abstract public String getName();

    abstract public int getVersion();

    abstract public void onModuleLoaded(AtomicEnvironment atomicEnvironment, String[] args);

    abstract public void onModuleStart(AtomicEnvironment atomicEnvironment);

    abstract public void onModuleStop(AtomicEnvironment atomicEnvironment);

}
