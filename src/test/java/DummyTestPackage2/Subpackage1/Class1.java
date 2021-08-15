package DummyTestPackage2.Subpackage1;

import DummyTestPackage2.Subpackage1.Subpackage11.Class11;
import me.nort3x.atomic.annotation.Atom;
import me.nort3x.atomic.annotation.Atomic;
import me.nort3x.atomic.annotation.PostConstruction;

@Atomic
public class Class1 {
    @Atom
    Class11 class11;

    @PostConstruction
    void ps() {

    }
}
