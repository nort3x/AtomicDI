package me.nort3x.atomic.annotation;

import java.lang.annotation.*;

/**
 * will exclude annotated type/method/field  from each and every scan or query
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.CONSTRUCTOR})
@Documented
@Inherited
public @interface Exclude {
}
