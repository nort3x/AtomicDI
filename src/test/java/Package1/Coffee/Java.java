package Package1.Coffee;

import me.nort3x.atomic.annotation.Atom;
import me.nort3x.atomic.annotation.Atomic;

@Atomic
public class Java {
    @Atom
    CoffeeMachine coffeeMachine;

    public boolean isJava() {
        return true;
    }

    public boolean hasBeansInCoffeeMachine() {
        return coffeeMachine.hasBeans();
    }
}
