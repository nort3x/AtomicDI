import Package3.EnumLike;
import Package3.PrettyPredefined;
import me.nort3x.atomic.core.AtomicDI;
import me.nort3x.atomic.core.container.Container;
import me.nort3x.atomic.logger.AtomicLogger;
import me.nort3x.atomic.logger.Priority;
import me.nort3x.atomic.wrappers.AtomicType;
import org.junit.jupiter.api.Test;

public class PredefinedTest {
    @Test
    void shouldPredefine() {
        AtomicLogger.setVerbosityLevel(Priority.DEBUG);
        AtomicDI.getInstance().resolve(EnumLike.class);

        PrettyPredefined pt = (PrettyPredefined) Container.makeContainerAround(AtomicType.of(PrettyPredefined.class)).getCentral();
    }
}