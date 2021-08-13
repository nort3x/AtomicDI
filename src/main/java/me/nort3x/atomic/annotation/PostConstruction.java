package me.nort3x.atomic.annotation;


import java.lang.annotation.*;

/**
 * first method in {@link Atomic} types that is annotated with this will be called exactly after creation
 *
 * <pre>
 *     <code>
 * {@literal @}Atomic
 *        class MyType{
 *
 *             {@literal @}Atom
 *              MyDependency dependency;
 *
 *             {@literal @}PostConstruction
 *              void postConstructor(){
 *                  dependency.configure();
 *              }
 *        }
 *     </code>
 * </pre>
 * <p>
 * <p>
 * Cyclic dependency is not resolved in this version, using this carelessly cam result in NPE
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@AtomicMethod
@Documented
public @interface PostConstruction {

}
