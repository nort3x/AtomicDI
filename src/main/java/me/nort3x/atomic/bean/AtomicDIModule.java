package me.nort3x.atomic.bean;

public abstract class AtomicDIModule {
    protected String provideModulePackagePath() {
        return this.getClass().getPackage().getName();
    }

    ;

    protected abstract String provideModuleName();

    protected abstract int provideModuleVersion();

    protected abstract void onPreLoad();

    protected abstract void onPostLoad();
}
