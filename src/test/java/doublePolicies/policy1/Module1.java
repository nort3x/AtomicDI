package doublePolicies.policy1;

import me.nort3x.atomic.bean.AtomicDIModule;

public class Module1 extends AtomicDIModule {
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