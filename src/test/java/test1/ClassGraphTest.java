package test1;

import me.nort3x.atomic.bean.AtomicDIModule;
import me.nort3x.atomic.bean.DependencyGrapher;
import org.junit.jupiter.api.Test;

public class ClassGraphTest extends AtomicDIModule {


    @Test
    void shouldGraphDummyPackage() {
        DependencyGrapher d = DependencyGrapher.getInstance();
        d.addModules(this);
        d.run();
//        Factory<?> f = DependencyGrapher.getInstance().getProvider().getFactoryOf(DependencyLike.class);
//        Assertions.assertTrue(f.generate().isPresent());
//        DependencyLike dp = ((DependencyLike) f.generate().get());
//        Assertions.assertEquals(dp.getInt(), 2);
//        Assertions.assertEquals(dp.a, 1);
    }


    @Override
    protected String provideModuleName() {
        return null;
    }

    @Override
    protected int provideModuleVersion() {
        return 0;
    }

    @Override
    protected void onPreLoad(String... args) {

    }

    @Override
    protected void onPostLoad(String... args) {

    }

}
