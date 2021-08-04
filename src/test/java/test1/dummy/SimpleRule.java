package test1.dummy;

import me.nort3x.atomic.annotation.Atomic;
import me.nort3x.atomic.basic.Policy;
import me.nort3x.atomic.bean.Provider;

public class SimpleRule extends Policy {
    @Override
    public void accept(Provider provider) {
        provider.getAllAtomicAnnotatedWith(Atomic.class).forEach(x -> System.out.println(x.getName()));
    }
}
