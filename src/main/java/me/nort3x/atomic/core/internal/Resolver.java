package me.nort3x.atomic.core.internal;

import me.nort3x.atomic.annotation.Atomic;
import me.nort3x.atomic.annotation.Exclude;
import me.nort3x.atomic.logger.AtomicLogger;
import me.nort3x.atomic.logger.Priority;
import org.reflections8.Reflections;

import java.util.Collection;
import java.util.stream.Collectors;

// responsible for chewing everything from given path, just give and forget
public class Resolver {

    final AtomicLogger logger = AtomicLogger.getInstance();
    final GreedyBag gb;

    public Resolver(GreedyBag gb) {
        this.gb = gb;
    }

    public void resolve(Class<?> point) {

        // a filter should not be like this but for sake of log it is
        Collection<Class<?>> grabbedTypes = new Reflections(point) // scan for all Types and SubTypes
                .getTypesAnnotatedWith(Atomic.class, true).stream() // which are atomic
                .peek(x -> logger.info("Discovered Atomic Type: " + x.getName(), Priority.DEBUG, Resolver.class))
                .filter(aClass -> {
                    if (aClass.isAnnotationPresent(Exclude.class)) {
                        logger.info("Excluded Atomic Type: " + aClass.getName(), Priority.DEBUG, Resolver.class);
                        return false;
                    } else
                        return true;
                }) // and not excluded
                .collect(Collectors.toList()); // make a list out of them


        // resolve annotation of grabbed types
        grabbedTypes.parallelStream().forEach(gb::load);

    }
}
