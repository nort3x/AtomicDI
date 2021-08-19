package EveryThingTest;

import me.nort3x.atomic.annotation.Atomic;
import me.nort3x.atomic.basic.AtomicModule;
import me.nort3x.atomic.core.internal.AtomicEnvironment;

@Atomic
public class MainModule extends AtomicModule {
    @Override
    public String getName() {
        return "MainModuleForTest";
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public void onModuleLoaded(AtomicEnvironment atomicEnvironment, String[] args) {
        i = 1;
    }

    @Override
    public void onModuleStart(AtomicEnvironment atomicEnvironment) {
        k = 1;
    }

    @Override
    public void onModuleStop(AtomicEnvironment atomicEnvironment) {

    }


    int k = 0;
    public int mainModuleIsOneAfterMainCallInvoke(){
        return k;
    }

    int i = 0;
    public int mainModuleIsOneAfterScanned(){
        return i;
    }
}
