package EveryThingTest;

import me.nort3x.atomic.annotation.Atomic;
import me.nort3x.atomic.basic.AtomicModule;
import me.nort3x.atomic.core.AtomicEnvironment;
import me.nort3x.atomic.wrappers.AtomicType;

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
    public void whenScannedFinished(AtomicEnvironment atomicEnvironment) {
        i = 1;
        System.out.println(AtomicType.of(AnotherModule.class).getAtomicAnnotationIfExist(CustomAnnotation.class).get());
    }

    @Override
    public void whenModuleStarted(AtomicEnvironment atomicEnvironment) {
            k=1;
    }

    @Override
    public void whenModuleStopped(AtomicEnvironment atomicEnvironment) {

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
