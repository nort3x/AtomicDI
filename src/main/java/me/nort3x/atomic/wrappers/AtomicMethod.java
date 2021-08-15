package me.nort3x.atomic.wrappers;

import me.nort3x.atomic.annotation.PostConstruction;
import me.nort3x.atomic.utility.CustomCollector;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

public class AtomicMethod {
    final Method correspondingMethod;
    final Set<AtomicAnnotation> annotationSet;
    final Set<AtomicParameter> parameterSet;
    final Class<?> outputType;
    final boolean isPostConstruct;
    final boolean hasAtomicOutput;

    public AtomicMethod(Method correspondingMethod) {
        this.correspondingMethod = correspondingMethod;
        this.parameterSet = Arrays.stream(correspondingMethod.getParameters()).parallel()
                .map(AtomicParameter::new)
                .collect(CustomCollector.concurrentSet());
        this.annotationSet = Arrays.stream(correspondingMethod.getAnnotations()).parallel()
                .map(annotation -> AtomicAnnotation.getOrCreate(annotation.annotationType()))
                .collect(CustomCollector.concurrentSet());
        this.outputType = correspondingMethod.getReturnType();
        isPostConstruct = this.annotationSet.contains(AtomicAnnotation.getOrCreate(PostConstruction.class));
        hasAtomicOutput = AtomicAnnotation.isAtomic(this.outputType);
    }

    public boolean IsOutPutAtomicType() {
        return hasAtomicOutput;
    }

    public Method getCorrespondingMethod() {
        return correspondingMethod;
    }

    public Set<AtomicAnnotation> getAnnotationSet() {
        return annotationSet;
    }

    public Set<AtomicParameter> getParameterSet() {
        return parameterSet;
    }

    public Class<?> getOutputType() {
        return outputType;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AtomicMethod)
            return ((AtomicMethod) o).correspondingMethod.equals(correspondingMethod);
        return correspondingMethod.equals(o);
    }

    @Override
    public int hashCode() {
        return correspondingMethod.hashCode();
    }

    public boolean isPostConstructor() {
        return isPostConstruct;
    }

}
