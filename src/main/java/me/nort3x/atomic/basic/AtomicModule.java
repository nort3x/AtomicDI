package me.nort3x.atomic.basic;


import me.nort3x.atomic.annotation.Atomic;
import me.nort3x.atomic.core.AtomicEnvironment;

import java.util.function.Consumer;

@Atomic
public abstract class AtomicModule {

    abstract public String getName();
    abstract public int getVersion();


    abstract public void whenScannedFinished(AtomicEnvironment atomicEnvironment);
    abstract public void whenModuleStarted(AtomicEnvironment atomicEnvironment);
    abstract public void whenModuleStopped(AtomicEnvironment atomicEnvironment);

}
