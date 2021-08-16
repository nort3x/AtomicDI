package Package1.Coffee;

import me.nort3x.atomic.annotation.Atom;
import me.nort3x.atomic.annotation.Atomic;

@Atomic
public class Bean {
    @Atom
    Java j;

    public boolean isGrown() {
        return j.isJava();
    }
}
