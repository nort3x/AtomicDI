package test1;

import me.neiizun.lightdrop.atomic.bean.DependencyGrapher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import test1.dummy.DependancyLike;

public class ClassGraphTest {


    @Test
    void shouldGraphDummyPackage(){
        DependencyGrapher.graphUsingThisEntryPoint(this.getClass());
        var f  = DependencyGrapher.getFactoryOf(DependancyLike.class);
        Assertions.assertFalse(f.generate().isEmpty());
        DependancyLike dp = ((DependancyLike)f.generate().get());
        Assertions.assertEquals(dp.getInt(),2);
        Assertions.assertEquals(dp.a,1);
    }

}
