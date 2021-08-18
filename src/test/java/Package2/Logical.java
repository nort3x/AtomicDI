package Package2;

import me.nort3x.atomic.annotation.Atom;
import me.nort3x.atomic.annotation.Atomic;
import me.nort3x.atomic.annotation.PostConstruction;

@Atomic
public class Logical {
    @Atom(scope = Atom.Scope.GLOBAL)
    ShareMe shareMe = null;
    @Atom
    Logical2 lg;

    @PostConstruction
    void constructor() {
        assert shareMe != null;
    }
}
