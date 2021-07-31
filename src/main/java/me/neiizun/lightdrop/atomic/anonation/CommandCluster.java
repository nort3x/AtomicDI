package me.neiizun.lightdrop.atomic.anonation;


import java.lang.annotation.*;


/**
 * use this annotation on classes containing {@link Command} for being scanned
 */
@Documented
@Inherited
@Atomic
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandCluster {
    String forBot();
}