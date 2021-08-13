package doublePolicies.policy1;

import doublePolicies.policy2.AtomicDISubModule2;
import me.nort3x.atomic.annotation.Atom;
import me.nort3x.atomic.annotation.Atomic;
import me.nort3x.atomic.bean.AtomicDISubModule;
import me.nort3x.atomic.bean.Provider;

@Atomic
public class AtomicDISubModule1 extends AtomicDISubModule {


    @Atom(concreteType = AtomicDISubModule2.class)
    AtomicDISubModule2 m;

    @Override
    public void accept(Provider provider) {
        System.out.println("policy1 called");
    }

    @Override
    public void whenAllSubModulesLoaded() {
        System.out.println(m.getIdentifier());
    }
}
