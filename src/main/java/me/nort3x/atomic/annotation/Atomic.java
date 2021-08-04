package me.nort3x.atomic.annotation;

import java.lang.annotation.*;


/**
 * the only primitive condition for scanner to pickup class for further analysis is being Atomic
 */

@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Atomic {
}
