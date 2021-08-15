import DummyTestPackage2.Pointer2;
import me.nort3x.atomic.core.AtomicDI;
import me.nort3x.atomic.logger.AtomicLogger;
import me.nort3x.atomic.logger.Priority;
import org.junit.jupiter.api.Test;

public class ScanningTest {
    @Test
    void willScanPackageAndSubPackages() throws InterruptedException {
        AtomicLogger.setVerbosityLevel(Priority.DEBUG);
        AtomicDI a = new AtomicDI();
        a.getResolver().resolve(Pointer2.class);
        //Assertions.assertEquals(2, a.getGreedyBag().getTypesAnnotations().size());

        Thread.sleep(1000);
    }
}
