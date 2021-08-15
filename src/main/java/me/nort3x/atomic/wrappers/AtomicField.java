package me.nort3x.atomic.wrappers;

import me.nort3x.atomic.utility.CustomCollector;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;

public class AtomicField {

    final Field correspondingField;
    final Set<AtomicAnnotation> annotationSet;
    final Class<?> type;

    final boolean isAtomicType;

    public AtomicField(Field correspondingField) {
        this.correspondingField = correspondingField;
        annotationSet = Arrays.stream(correspondingField.getAnnotations()).parallel()
                .map(annotation -> AtomicAnnotation.getOrCreate(annotation.annotationType()))
                .collect(CustomCollector.concurrentSet());
        this.type = correspondingField.getType();

        isAtomicType = AtomicAnnotation.isAtomic(type);
    }

    public Class<?> getType() {
        return type;
    }

    public boolean isAtomicType() {
        return isAtomicType;
    }

    public Field getCorrespondingField() {
        return correspondingField;
    }

    public Set<AtomicAnnotation> getAnnotationSet() {
        return annotationSet;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AtomicField)
            return ((AtomicField) o).correspondingField.equals(correspondingField);
        return correspondingField.equals(o);
    }

    @Override
    public int hashCode() {
        return correspondingField.hashCode();
    }
}
