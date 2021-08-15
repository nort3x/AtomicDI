package me.nort3x.atomic.wrappers;

import me.nort3x.atomic.utility.CustomCollector;

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Set;

public final class AtomicParameter {

    private final Parameter correspondingParameter;
    private final Set<AtomicAnnotation> annotationSet;
    private final Class<?> type;
    private final boolean isAtomicType;

    public AtomicParameter(Parameter correspondingParameter) {
        this.correspondingParameter = correspondingParameter;
        annotationSet = Arrays.stream(correspondingParameter.getAnnotations()).parallel()
                .map(annotation -> AtomicAnnotation.getOrCreate(annotation.annotationType()))
                .collect(CustomCollector.concurrentSet());
        type = correspondingParameter.getType();
        isAtomicType = AtomicAnnotation.isAtomic(type);
    }

    public Class<?> getType() {
        return type;
    }

    public boolean isAtomicType() {
        return isAtomicType;
    }

    public Parameter getCorrespondingParameter() {
        return correspondingParameter;
    }

    public Set<AtomicAnnotation> getAnnotationSet() {
        return annotationSet;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AtomicParameter)
            return ((AtomicParameter) o).correspondingParameter.equals(correspondingParameter);
        return correspondingParameter.equals(o);
    }

    @Override
    public int hashCode() {
        return correspondingParameter.hashCode();
    }
}
