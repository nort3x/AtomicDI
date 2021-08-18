package me.nort3x.atomic.annotation;

import java.lang.annotation.*;


/**
 * <h2>Atom</h2>
 * Atom force scanner to provide annotated field at runtime
 * <pre>
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
 *
 * @see Atomic
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Atomic
public @interface Atom {

    enum Scope {
        GLOBAL,
        PER_CONTAINER
    }

    Scope scope() default Scope.GLOBAL;

    /**
     * when subclasses of annotated Atomic exist specify type explicitly
     *
     * @return exact concrete type
     */
    Class<?> concreteType() default Object.class;
}
