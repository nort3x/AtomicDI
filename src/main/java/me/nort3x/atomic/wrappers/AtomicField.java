package me.nort3x.atomic.wrappers;

import me.nort3x.atomic.logger.AtomicLogger;
import me.nort3x.atomic.logger.enums.Priority;
import me.nort3x.atomic.utility.CustomCollector;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;

public final class AtomicField {

    final Field correspondingField;
    final Set<AtomicAnnotation> annotationSet;
    final Class<?> type;

    final boolean isAtomicType;
    private boolean isAtom = false;
    private boolean isPredefined = false;

    public AtomicField(Field correspondingField) {
        this.correspondingField = correspondingField;
        annotationSet = Arrays.stream(correspondingField.getAnnotations()).parallel()
                .map(annotation -> AtomicAnnotation.of(annotation.annotationType()))
                .peek(atomicAnnotation -> isAtom |= atomicAnnotation.isAtom())
                .peek(atomicAnnotation -> isPredefined |= atomicAnnotation.isPredefined())
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

    public void setField(Object instance, Object value) {
        try {
            correspondingField.set(instance, value);
        } catch (IllegalAccessException e) {
            AtomicLogger.getInstance().getLogger(AtomicField.class,Priority.VERY_IMPORTANT).error("Setting Field:" + correspondingField.getDeclaringClass().getName() + "." + correspondingField.getName() + " thrown Exception: " +
                    AtomicLogger.exceptionToString(e));
        }
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

    public boolean isAtom() {
        return isAtom;
    }

    public boolean isPredefined() {
        return isPredefined;
    }
}
