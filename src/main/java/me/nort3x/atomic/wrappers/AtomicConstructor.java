package me.nort3x.atomic.wrappers;

import me.nort3x.atomic.utility.CustomCollector;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Set;

public class AtomicConstructor {
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
                .map(annotation -> AtomicAnnotation.getOrCreate(annotation.annotationType()))
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
