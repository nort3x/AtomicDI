package me.nort3x.atomic.core.internal;

import me.nort3x.atomic.annotation.Atomic;
import me.nort3x.atomic.annotation.Exclude;
import me.nort3x.atomic.logger.AtomicLogger;
import me.nort3x.atomic.logger.enums.Priority;
import me.nort3x.atomic.wrappers.AtomicType;
import org.reflections8.Reflections;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.stream.Collectors;

// responsible for chewing everything from given path, just give and forget
public class Resolver {

    final AtomicLogger logger = AtomicLogger.getInstance();
    private Logger lImp = AtomicLogger.getInstance().getLogger(Resolver.class, Priority.IMPORTANT), lDeb = AtomicLogger.getInstance().getLogger(Resolver.class, Priority.DEBUG);

    public Resolver() {

    }

    public void resolve(Class<?> point) {

        // a filter should not be like this but for sake of log it is
        Collection<Class<?>> grabbedTypes = new Reflections(point) // scan for all Types and SubTypes
                .getTypesAnnotatedWith(Atomic.class, true).stream() // which are atomic
                .peek(x -> lDeb.info("Discovered Atomic Type: " + x.getName()))
                .filter(aClass -> {
                    if (aClass.isAnnotationPresent(Exclude.class)) {
                        lDeb.info("Excluded Atomic Type: " + aClass.getName());
                        return false;
                    } else
                        return true;
                }) // and not excluded
                .collect(Collectors.toList()); // make a list out of them


        // resolve annotation of grabbed types
        grabbedTypes.parallelStream().forEach(type -> {
            AtomicType at = AtomicType.of(type);
            if (!at.isAtomic())
                lImp.warn("NonAtomicTypeCalled to be loaded, you are facing a bug, please report this");
        });

    }
}
