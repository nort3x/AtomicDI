package test1.dummy;

import me.nort3x.atomic.annotation.Atomic;
import me.nort3x.atomic.bean.AtomicDISubModule;
import me.nort3x.atomic.bean.Provider;

public class SimpleRule extends AtomicDISubModule {
    @Override
    public void accept(Provider provider) {
        provider.getAllAtomicAnnotatedWith(Atomic.class).forEach(x -> System.out.println(x.getName()));
    }

    @Override
    public void whenAllSubModulesLoaded() {

    }
}
