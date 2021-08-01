package test1;

import me.nort3x.atomic.bean.DependencyGrapher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import test1.dummy.DependencyLike;

public class ClassGraphTest {


    @Test
    void shouldGraphDummyPackage(){
        DependencyGrapher.getInstance().graphUsingThisEntryPoint(this.getClass());
        var f  = DependencyGrapher.getInstance().getFactoryOf(DependencyLike.class);
        Assertions.assertFalse(f.generate().isEmpty());
        DependencyLike dp = ((DependencyLike)f.generate().get());
        Assertions.assertEquals(dp.getInt(),2);
        Assertions.assertEquals(dp.a,1);
    }

}
