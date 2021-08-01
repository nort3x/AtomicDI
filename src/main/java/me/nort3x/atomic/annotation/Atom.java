package me.nort3x.atomic.annotation;

import java.lang.annotation.*;


/**
 * <h2>Atom</h2>
 * Atom force scanner to provide annotated field at runtime
 * <blackout>
 *     <pre>
 *         <code>
 *
 *          {@literal @}Atomic // or any scannable tags
 *           class Something{
 *
 *                // will be provided by scanner at runtime
 *               {@literal @}Atom SomethingElse somethingElse;
 *
 *           }
 *         </code>
 *     </pre>
 * </blackout>
 *
 * @see Atomic
 */
@Documented
@Inherited
@Atomic
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Atom {

    enum Type {
        Shared,
        Unique
    }

    Type type() default Type.Shared;
}
