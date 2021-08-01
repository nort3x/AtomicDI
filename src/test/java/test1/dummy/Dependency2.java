package test1.dummy;

import me.nort3x.atomic.annotation.Atomic;

@Atomic
public class Dependency2 {

    static int SOMETHING =  2;
    @SuppressWarnings("")
    public int getHisInt() {
        return SOMETHING;
    }
}
