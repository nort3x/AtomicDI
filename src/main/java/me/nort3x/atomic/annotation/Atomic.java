package me.nort3x.atomic.annotation;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Atomic {
    // tag a class for being scanned
}
