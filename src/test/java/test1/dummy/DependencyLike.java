package test1.dummy;

import me.nort3x.atomic.annotation.Atom;
import me.nort3x.atomic.annotation.Atomic;
import me.nort3x.atomic.annotation.PostConstruction;

@Atomic
public class DependencyLike {

    @Atom(type = Atom.Type.Shared) Dependency2 dependency2;

    public int getInt(){
        return dependency2.getHisInt();
    }

    public int a=0;
    @PostConstruction
    void construct(){
        a = 1;
    }
}
