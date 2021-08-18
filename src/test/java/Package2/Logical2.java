package Package2;

import me.nort3x.atomic.annotation.Atom;
import me.nort3x.atomic.annotation.Atomic;

@Atomic
public class Logical2 {
    @Atom(scope = Atom.Scope.GLOBAL)
    ShareMe shareMe;
}
