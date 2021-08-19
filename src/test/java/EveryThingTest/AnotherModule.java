package EveryThingTest;

import me.nort3x.atomic.annotation.Atom;
import me.nort3x.atomic.basic.AtomicModule;
import me.nort3x.atomic.core.internal.AtomicEnvironment;


@CustomAnnotation
public class AnotherModule extends AtomicModule {
    @Atom(concreteType = MainModule.class)
    AtomicModule mainModule;

    @Override
    public String getName() {
        return "OtherModule";
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public void onModuleLoaded(AtomicEnvironment atomicEnvironment, String[] args) {
        assert mainModule != null;
    }

    @Override
    public void onModuleStart(AtomicEnvironment atomicEnvironment) {
        System.out.println(((MainModule) mainModule).mainModuleIsOneAfterScanned());
    }

    @Override
    public void onModuleStop(AtomicEnvironment atomicEnvironment) {
    }
}
