import Package1.ClassOne;
import Package1.Coffee.Java;
import me.nort3x.atomic.AtomicDI;
import me.nort3x.atomic.core.container.Container;
import me.nort3x.atomic.logger.AtomicLogger;
import me.nort3x.atomic.logger.Priority;
import me.nort3x.atomic.wrappers.AtomicType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CoffeeMachineCyclicTest {
    @Test
    void shouldResolveCycleInCoffeeMachine() {
        AtomicLogger.setVerbosityLevel(Priority.DEBUG);
        AtomicDI.run(ClassOne.class);
        Java j = (Java) Container.makeContainerAround(AtomicType.of(Java.class)).getCentralUnique();
        Assertions.assertTrue(j.hasBeansInCoffeeMachine());
    }
}
