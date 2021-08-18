package Package2;

import me.nort3x.atomic.annotation.Atom;
import me.nort3x.atomic.annotation.Atomic;

@Atomic
public class Greedy {
    @Atom(scope = Atom.Scope.PER_CONTAINER)
    ShareMe de2;
}
