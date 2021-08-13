package doublePolicies;

import doublePolicies.policy1.Module1;
import doublePolicies.policy2.Module2;
import me.nort3x.atomic.bean.DependencyGrapher;

public class EntryPoint {
    public static void main(String[] args) {
        DependencyGrapher.getInstance().addModules(new Module1(), new Module2());
        DependencyGrapher.getInstance().run();
    }
}
