package EveryThingTest;

import me.nort3x.atomic.annotation.Atomic;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Atomic
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomAnnotation {
}
