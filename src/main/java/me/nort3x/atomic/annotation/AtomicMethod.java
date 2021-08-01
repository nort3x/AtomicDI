package me.nort3x.atomic.annotation;

import java.lang.annotation.*;




@Inherited
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AtomicMethod {
    // scanner will pick these methods up inside @Atomic classes
}
