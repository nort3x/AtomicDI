package doublePolicies.policy2;

import me.nort3x.atomic.annotation.Atomic;
import me.nort3x.atomic.bean.AtomicDIModule;


@Atomic
public class Module2 extends AtomicDIModule {
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

    @Override
    protected void afterLoadInvoke(String... args) {

    }


}
