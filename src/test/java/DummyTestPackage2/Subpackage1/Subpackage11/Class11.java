package DummyTestPackage2.Subpackage1.Subpackage11;

import me.nort3x.atomic.annotation.Atom;
import me.nort3x.atomic.annotation.Atomic;
import me.nort3x.atomic.annotation.Exclude;
import me.nort3x.atomic.annotation.Interaction;

@Atomic
public class Class11 {

    @Atom
    int Field1;

    @Exclude
    @Interaction
    void Something() {
    }
}
