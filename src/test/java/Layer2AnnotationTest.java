import Package2.Greedy;
import Package2.Logical;
import Package2.ShareMe;
import me.nort3x.atomic.AtomicDI;
import me.nort3x.atomic.core.container.Container;
import me.nort3x.atomic.logger.AtomicLogger;
import me.nort3x.atomic.logger.Priority;
import me.nort3x.atomic.wrappers.AtomicType;
import org.junit.jupiter.api.Test;


public class Layer2AnnotationTest {
    @Test
    void shouldShareAndCreateShare() {
        AtomicLogger.setVerbosityLevel(Priority.DEBUG);
        AtomicDI.run(ShareMe.class);
        Logical lg = (Logical) Container.makeContainerAroundShared(AtomicType.of(Logical.class)).getCentralUnique();
        Greedy greedy = (Greedy) Container.makeContainerAroundShared(AtomicType.of(Greedy.class)).getCentralUnique();
    }
}
