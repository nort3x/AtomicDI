package me.nort3x.atomic.Test1;

import me.nort3x.atomic.AtomicDI;
import me.nort3x.atomic.core.container.Container;
import me.nort3x.atomic.wrappers.AtomicType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Main {
    @Test
    void shouldScan(){
        AtomicDI.run(getClass());
        Class1 c1 = (Class1) Container.makeContainerAroundUnique(AtomicType.of(Class1.class)).getCentralUnique();
        Assertions.assertNotEquals(c1.num,0);
    }
}
