import Package2.Greedy;
import Package2.Logical;
import Package2.ShareMe;
import me.nort3x.atomic.core.AtomicDI;
import me.nort3x.atomic.core.container.Container;
import me.nort3x.atomic.logger.AtomicLogger;
import me.nort3x.atomic.logger.Priority;
import me.nort3x.atomic.wrappers.AtomicType;
import org.junit.jupiter.api.Test;


public class Layer2AnnotationTest {
    @Test
    void shouldShareAndCreateShare() {
        AtomicLogger.setVerbosityLevel(Priority.DEBUG);
        AtomicDI.getInstance().resolve(ShareMe.class);
        Logical lg = (Logical) Container.makeContainerAround(AtomicType.of(Logical.class)).getCentral();
        Greedy greedy = (Greedy) Container.makeContainerAround(AtomicType.of(Greedy.class)).getCentral();
    }
}
