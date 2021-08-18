package EveryThingTest;

import me.nort3x.atomic.annotation.Atom;
import me.nort3x.atomic.annotation.Atomic;
import me.nort3x.atomic.basic.AtomicModule;
import me.nort3x.atomic.core.AtomicEnvironment;


@Atomic
public class AnotherModule extends AtomicModule {
    @Atom MainModule mainModule;
    @Override
    public String getName() {
        return "OtherModule";
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public void whenScannedFinished(AtomicEnvironment atomicEnvironment) {
            assert mainModule!=null;
    }

    @Override
    public void whenModuleStarted(AtomicEnvironment atomicEnvironment) {
            System.out.println(mainModule.mainModuleIsOneAfterScanned());
    }

    @Override
    public void whenModuleStopped(AtomicEnvironment atomicEnvironment) {
    }
}
