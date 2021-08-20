import Package3.EnumLike;
import Package3.PrettyPredefined;
import me.nort3x.atomic.AtomicDI;
import me.nort3x.atomic.core.container.Container;
import me.nort3x.atomic.logger.AtomicLogger;
import me.nort3x.atomic.logger.Priority;
import me.nort3x.atomic.wrappers.AtomicType;
import org.junit.jupiter.api.Test;

public class PredefinedTest {
    @Test
    void shouldPredefine() {
        AtomicLogger.setVerbosityLevel(Priority.DEBUG);
        AtomicDI.run(EnumLike.class);

        PrettyPredefined pt = (PrettyPredefined) Container.makeContainerAroundShared(AtomicType.of(PrettyPredefined.class)).getCentralUnique();
    }
}
