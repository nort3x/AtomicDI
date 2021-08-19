package me.nort3x.atomic.annotation;

import java.lang.annotation.*;


/**
 * <h2>Atom</h2>
 * Atom forces scanner to provide annotated field at runtime
 * any custom annotation that are annotated with Atom will also have the same effect
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
