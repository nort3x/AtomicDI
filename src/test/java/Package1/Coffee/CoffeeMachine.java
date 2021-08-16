package Package1.Coffee;

import me.nort3x.atomic.annotation.Atom;
import me.nort3x.atomic.annotation.Atomic;

@Atomic
public class CoffeeMachine {
    @Atom
    Bean bean;


    boolean hasBeans() {
        return bean.isGrown();
    }
}
