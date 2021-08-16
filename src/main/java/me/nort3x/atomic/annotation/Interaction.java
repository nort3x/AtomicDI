package me.nort3x.atomic.annotation;

import java.lang.annotation.*;


/**
 * its better to Annotate every Scannable method with this Annotation or inheritors of this annotation
 * this is arbitrary but helps with immunity of code in further releases
 *
 * @see PostConstruction
 */
@Inherited
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Atomic
public @interface Interaction {
}
