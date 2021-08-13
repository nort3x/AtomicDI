package doublePolicies;

import doublePolicies.policy1.Module1;
import doublePolicies.policy2.Module2;
import me.nort3x.atomic.bean.DependencyGrapher;

public class EntryPoint {
    public static void main(String[] args) {
        DependencyGrapher.getInstance().addModules(Module1.class, Module2.class);
        DependencyGrapher.getInstance().run();
    }
}
