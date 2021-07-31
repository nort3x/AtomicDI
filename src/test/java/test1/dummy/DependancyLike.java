package test1.dummy;

import me.neiizun.lightdrop.atomic.anonation.Atom;
import me.neiizun.lightdrop.atomic.anonation.Atomic;
import me.neiizun.lightdrop.atomic.anonation.PostConstruction;

@Atomic
public class DependancyLike {

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
