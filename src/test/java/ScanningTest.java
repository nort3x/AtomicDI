import Package1.ClassOne;
import me.nort3x.atomic.AtomicDI;
import me.nort3x.atomic.core.container.Container;
import me.nort3x.atomic.logger.AtomicLogger;
import me.nort3x.atomic.logger.Priority;
import me.nort3x.atomic.wrappers.AtomicType;
import org.junit.jupiter.api.Test;

public class ScanningTest {
    @Test
    void willScanPackageAndSubPackages() throws InterruptedException {
        AtomicLogger.setVerbosityLevel(Priority.DEBUG);
        AtomicDI.run(ClassOne.class);
        //Assertions.assertEquals(2, a.getGreedyBag().getTypesAnnotations().size());
        Container c = Container.makeContainerAround(AtomicType.of(ClassOne.class));

        c.get(AtomicType.of(ClassOne.class));

        Thread.sleep(1000);
    }
}
