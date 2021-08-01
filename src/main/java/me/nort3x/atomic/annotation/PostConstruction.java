package me.nort3x.atomic.annotation;


import java.lang.annotation.*;


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@AtomicMethod
@Documented
public @interface PostConstruction {
    // acts as a lazy constructor
    // when injector injects all of Atomic fields first method annotated with this tag will be called
    // annotated method should be void to void
}
