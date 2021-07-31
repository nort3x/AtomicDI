package me.neiizun.lightdrop.atomic.anonation;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Atomic {
    // tag a class for being scanned
}
