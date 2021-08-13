package doublePolicies.policy2;

import me.nort3x.atomic.annotation.Atomic;
import me.nort3x.atomic.basic.Policy;
import me.nort3x.atomic.bean.Provider;

@Atomic
public class Policy2 extends Policy {
    @Override
    public void accept(Provider provider) {
        System.out.println("policy2 called");
    }
}

