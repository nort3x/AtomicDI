package doublePolicies;

import doublePolicies.policy1.Module1;
import doublePolicies.policy2.Module2;
import me.nort3x.atomic.annotation.Atomic;
import me.nort3x.atomic.annotation.Exclude;
import me.nort3x.atomic.bean.DependencyGrapher;
import org.reflections8.Reflections;

import java.util.Collection;
import java.util.stream.Collectors;

public class EntryPoint {
    public static void main(String[] args) {
        DependencyGrapher.getInstance().addModules(new Module1(), new Module2());
        DependencyGrapher.getInstance().run();
        Collection<Class<?>> atomics = new Reflections("doublePolicies.policy1").getTypesAnnotatedWith(Atomic.class).stream().filter(x -> !x.isAnnotationPresent(Exclude.class)).collect(Collectors.toList());
    }
}
