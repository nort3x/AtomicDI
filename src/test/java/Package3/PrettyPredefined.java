package Package3;

import me.nort3x.atomic.annotation.Atomic;
import me.nort3x.atomic.annotation.PostConstruction;
import me.nort3x.atomic.annotation.Predefined;

@Atomic
public class PrettyPredefined {
    @Predefined("littleInt")
    Integer i;
    @Predefined("goodString")
    String s;
    @Predefined("anEnum")
    EnumLike e;

    @PostConstruction
    void p() {
        System.out.println(i);
        System.out.println(s);
        System.out.println(e);
    }
}
