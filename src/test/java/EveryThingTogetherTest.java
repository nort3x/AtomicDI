import EveryThingTest.MainModule;
import me.nort3x.atomic.core.AtomicDI;
import me.nort3x.atomic.logger.AtomicLogger;
import me.nort3x.atomic.logger.Priority;
import org.junit.jupiter.api.Test;

public class EveryThingTogetherTest {
    @Test
    void everyTest() throws InterruptedException {
        AtomicLogger.setVerbosityLevel(Priority.DEBUG);
        AtomicDI.getInstance().resolve(MainModule.class);
        Thread.sleep(10000);
    }
}
