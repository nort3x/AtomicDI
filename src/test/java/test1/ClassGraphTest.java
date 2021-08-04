package test1;

import me.nort3x.atomic.bean.DependencyGrapher;
import me.nort3x.atomic.reactor.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import test1.dummy.DependencyLike;

public class ClassGraphTest {


    @Test
    void shouldGraphDummyPackage(){
        DependencyGrapher d = DependencyGrapher.getInstance();
        d.graphUsingThisEntryPoint(this.getClass());
        Factory<?> f = DependencyGrapher.getInstance().getProvider().getFactoryOf(DependencyLike.class);
        Assertions.assertTrue(f.generate().isPresent());
        DependencyLike dp = ((DependencyLike) f.generate().get());
        Assertions.assertEquals(dp.getInt(), 2);
        Assertions.assertEquals(dp.a, 1);
    }





}
