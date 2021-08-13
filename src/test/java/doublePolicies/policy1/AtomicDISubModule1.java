package doublePolicies.policy1;

import me.nort3x.atomic.annotation.Atomic;
import me.nort3x.atomic.bean.AtomicDISubModule;
import me.nort3x.atomic.bean.Provider;

@Atomic
public class AtomicDISubModule1 extends AtomicDISubModule {
    @Override
    public void accept(Provider provider) {
        System.out.println("policy1 called");
    }

    @Override
    public void whenAllSubModulesLoaded() {

    }
}
