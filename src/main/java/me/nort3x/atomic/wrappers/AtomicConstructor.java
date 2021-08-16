package me.nort3x.atomic.wrappers;

import me.nort3x.atomic.logger.AtomicLogger;
import me.nort3x.atomic.logger.Priority;
import me.nort3x.atomic.utility.CustomCollector;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

public final class AtomicConstructor {
    final Constructor<?> correspondingConstructor;
    final Set<AtomicParameter> parameterSet;
    final Set<AtomicAnnotation> annotationSet;
    final boolean isNoArgConstructor;

    public AtomicConstructor(Constructor<?> correspondingConstructor) {
        this.correspondingConstructor = correspondingConstructor;
        this.parameterSet = Arrays.stream(correspondingConstructor.getParameters()).parallel()
                .map(AtomicParameter::new)
                .collect(CustomCollector.concurrentSet());
        isNoArgConstructor = parameterSet.size() == 0;
        this.annotationSet = Arrays.stream(correspondingConstructor.getAnnotations()).parallel()
                .map(annotation -> AtomicAnnotation.of(annotation.annotationType()))
                .collect(CustomCollector.concurrentSet());
    }

    public boolean isNoArgConstructor() {
        return isNoArgConstructor;
    }

    public Constructor<?> getCorrespondingConstructor() {
        return correspondingConstructor;
    }

    public Set<AtomicParameter> getParameterSet() {
        return parameterSet;
    }

    public Set<AtomicAnnotation> getAnnotationSet() {
        return annotationSet;
    }

    public Optional<Object> getInstance(Object... args) {
        try {
            return Optional.of(correspondingConstructor.newInstance(args));
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            AtomicLogger.getInstance().fatal("Invocation of Constructor of type: " + correspondingConstructor.getDeclaringClass().getName() + " thrown exception: " + AtomicLogger.exceptionToString(e), Priority.VERY_IMPORTANT, AtomicConstructor.class);
            return Optional.empty();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AtomicConstructor)
            return ((AtomicConstructor) o).correspondingConstructor.equals(correspondingConstructor);
        return correspondingConstructor.equals(o);
    }

    @Override
    public int hashCode() {
        return correspondingConstructor.hashCode();
    }
}
