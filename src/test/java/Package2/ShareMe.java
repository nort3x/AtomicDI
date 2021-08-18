package Package2;

import me.nort3x.atomic.annotation.Atomic;

@Atomic
public class ShareMe {

    public ShareMe() {
        System.out.println("someOneCalledMe!");
    }
}
