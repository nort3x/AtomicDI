package me.nort3x.atomic.wrappers;

import me.nort3x.atomic.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AtomicAnnotation {

    final Class<? extends Annotation> correspondingAnnotation;
    final Set<Class<? extends Annotation>> annotationSet;

    final boolean isAtomic;
    final boolean isInterAction;
    final boolean isExcluded;
    final boolean isAtom;
    final boolean isPredefined;


    private AtomicAnnotation(Class<? extends Annotation> correspondingAnnotation) {

        this.correspondingAnnotation = correspondingAnnotation;

        discover(correspondingAnnotation, correspondingAnnotation);

        annotationSet = relations.get(correspondingAnnotation);

//         if any of them is explicitly adding something
        isAtomic = annotationSet.contains(Atomic.class) || correspondingAnnotation.equals(Atomic.class);
        isAtom = annotationSet.contains(Atom.class) || correspondingAnnotation.equals(Atom.class);
        isInterAction = annotationSet.contains(Interaction.class) || correspondingAnnotation.equals(Interaction.class);
        isExcluded = annotationSet.contains(Exclude.class) || correspondingAnnotation.equals(Exclude.class);
        isPredefined = annotationSet.contains(Predefined.class) || correspondingAnnotation.equals(Predefined.class);


    }

    public static final ConcurrentHashMap<Class<? extends Annotation>, Set<Class<? extends Annotation>>> relations = new ConcurrentHashMap<>();

    public static void discover(Class<? extends Annotation> annotation, Class<? extends Annotation> caller) {

        for (Annotation an : annotation.getAnnotations()) {
            if (!relations.computeIfAbsent(caller, x -> ConcurrentHashMap.newKeySet()).contains(an.annotationType())) {
                relations.get(caller).add(an.annotationType());
                discover(an.annotationType(), caller);
            }
        }

    }

    public static final ConcurrentHashMap<Class<? extends Annotation>, AtomicAnnotation> alreadyLoadedAnnotations = new ConcurrentHashMap<>();

    public static AtomicAnnotation getOrCreate(Class<? extends Annotation> annotation) {
        return alreadyLoadedAnnotations.computeIfAbsent(annotation, AtomicAnnotation::new);
    }


    public Set<Class<? extends Annotation>> getAnnotationSet() {
        return annotationSet;
    }


    public boolean isAtomic() {
        return isAtomic;
    }

    public boolean isInterAction() {
        return isInterAction;
    }

    public boolean isExcluded() {
        return isExcluded;
    }

    public boolean isAtom() {
        return isAtom;
    }

    public boolean isPredefined() {
        return isPredefined;
    }

    public Class<? extends Annotation> getCorrespondingAnnotation() {
        return correspondingAnnotation;
    }


    // a wrapper should have transparent equal checks
    @Override
    public boolean equals(Object o) {
        if (o instanceof AtomicAnnotation) {
            return ((AtomicAnnotation) o).correspondingAnnotation.equals(correspondingAnnotation);
        }
        return correspondingAnnotation.equals(o);
    }

    @Override
    public int hashCode() {
        return correspondingAnnotation.hashCode();
    }


    public static boolean isAtomic(Class<?> clazz) {
        return Arrays.stream(clazz.getAnnotations()).parallel()
                .map(Annotation::annotationType)
                .map(AtomicAnnotation::new)
                .map(AtomicAnnotation::isAtomic)
                .reduce(false, Boolean::logicalOr);
    }

    public static boolean isInterAction(Method method) {
        return Arrays.stream(method.getAnnotations()).parallel()
                .map(Annotation::annotationType)
                .map(AtomicAnnotation::new)
                .map(AtomicAnnotation::isInterAction)
                .reduce(false, Boolean::logicalOr);
    }

    public static boolean isExcluded(Class<?> clazz) {
        return Arrays.stream(clazz.getAnnotations()).parallel()
                .map(Annotation::annotationType)
                .map(AtomicAnnotation::new)
                .map(AtomicAnnotation::isExcluded)
                .reduce(false, Boolean::logicalOr);
    }

    public static boolean isExcluded(Method m) {
        return Arrays.stream(m.getAnnotations()).parallel()
                .map(Annotation::annotationType)
                .map(AtomicAnnotation::new)
                .map(AtomicAnnotation::isExcluded)
                .reduce(false, Boolean::logicalOr);
    }

    public static boolean isExcluded(Field f) {
        return Arrays.stream(f.getAnnotations()).parallel()
                .map(Annotation::annotationType)
                .map(AtomicAnnotation::new)
                .map(AtomicAnnotation::isExcluded)
                .reduce(false, Boolean::logicalOr);
    }

    public static boolean isAtom(Field f) {
        return Arrays.stream(f.getAnnotations()).parallel()
                .map(Annotation::annotationType)
                .map(AtomicAnnotation::new)
                .map(AtomicAnnotation::isAtom)
                .reduce(false, Boolean::logicalOr);
    }

    public static boolean isPredefined(Field f) {
        return Arrays.stream(f.getAnnotations()).parallel()
                .map(Annotation::annotationType)
                .map(AtomicAnnotation::new)
                .map(AtomicAnnotation::isPredefined)
                .reduce(false, Boolean::logicalOr);
    }

    public static boolean isPredefined(Parameter p) {
        return Arrays.stream(p.getAnnotations()).parallel()
                .map(Annotation::annotationType)
                .map(AtomicAnnotation::new)
                .map(AtomicAnnotation::isPredefined)
                .reduce(false, Boolean::logicalOr);
    }


}
