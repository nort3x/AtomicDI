package me.nort3x.atomic.wrappers;

import me.nort3x.atomic.logger.AtomicLogger;
import me.nort3x.atomic.logger.Priority;
import me.nort3x.atomic.utility.CustomCollector;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AtomicType {

    static final AtomicLogger logger = AtomicLogger.getInstance();

    boolean isAtomic = false;
    boolean isPostConstructable;

    final Class<?> correspondingType;
    final Set<AtomicField> fieldSet;
    Set<AtomicAnnotation> annotationSet;
    final Set<AtomicMethod> methodSet;
    Set<AtomicConstructor> constructorSet;
    AtomicMethod postConstructor;

    public AtomicType(Class<?> type) {
        correspondingType = type;

        annotationSet = Arrays.stream(correspondingType.getAnnotations()).parallel()
                .map(annotation -> AtomicAnnotation.getOrCreate(annotation.annotationType()))
                .collect(CustomCollector.concurrentSet());

        if (annotationSet.parallelStream().map(AtomicAnnotation::isAtomic).reduce(false, Boolean::logicalOr)) { // its Atomic;
            isAtomic = true;

            fieldSet = Arrays.stream(type.getDeclaredFields()).parallel()
                    .filter(AtomicAnnotation::isAtom)
                    .peek(x -> logger.info("Discovered AtomicField: " + x.getName() + " In AtomicType: " + type.getName(), Priority.DEBUG, AtomicType.class))
                    .filter(x -> {
                        if (!AtomicAnnotation.isExcluded(x))
                            return true;
                        logger.info("Excluded AtomicField: " + x.getName() + " from AtomicType: " + type.getName(), Priority.DEBUG, AtomicType.class);
                        return false;
                    })
                    .peek(field -> {
                        int modifier = field.getModifiers();
                        if (Modifier.isStatic(modifier)) {
                            logger.warning("AtomicField is <Static> and its highly discouraging consider using Shared type in Atom annotation: " + field.getName() + " from AtomicType: " + type.getName(), Priority.VERBOSE, AtomicType.class);
                        } else if (Modifier.isFinal(modifier)) {
                            logger.warning("AtomicField is <Final> and its highly discouraging, it will be reinitialized if NoArgsConstructor exist: " + field.getName() + " from AtomicType: " + type.getName(), Priority.VERBOSE, AtomicType.class);
                        }
                    })
                    .peek(field -> field.setAccessible(true))
                    .map(AtomicField::new)
                    .collect(CustomCollector.concurrentSet());


            methodSet = Arrays.stream(type.getDeclaredMethods()).parallel()
                    .filter(AtomicAnnotation::isInterAction)
                    .peek(x -> logger.info("Discovered Interaction: " + x.getName() + " In AtomicType: " + type.getName(), Priority.DEBUG, AtomicType.class))
                    .filter(x -> {
                        if (!AtomicAnnotation.isExcluded(x))
                            return true;
                        logger.info("Excluded InterAction: " + x.getName() + " from AtomicType: " + type.getName(), Priority.DEBUG, AtomicType.class);
                        return false;
                    }).peek(field -> field.setAccessible(true))
                    .map(AtomicMethod::new)
                    .peek(method -> {
                        if (method.isPostConstructor())
                            logger.info("Discovered PostConstructor InterAction: " + method.getCorrespondingMethod().getName() + " In AtomicType: " + type.getName(), Priority.DEBUG, AtomicType.class);
                    })
                    .collect(CustomCollector.concurrentSet());

            methodSet.parallelStream()
                    .filter(AtomicMethod::isPostConstructor)
                    .findFirst().ifPresent(x -> {
                postConstructor = x;
                isPostConstructable = true;
                logger.info("Selected PostConstructor: " + x.getCorrespondingMethod().getName() + " for AtomicType: " + type.getName(), Priority.DEBUG, AtomicType.class);
            });

            if (methodSet.parallelStream().filter(AtomicMethod::isPostConstructor).count() > 1)
                logger.fatal("Multiple PostConstructor Detected and its highly discouraging because of parallel behaviors, consider Excluding all except one,  for AtomicType: " + type.getName(), Priority.VERY_IMPORTANT, AtomicType.class);


            constructorSet = Arrays.stream(type.getConstructors()).parallel()
                    .map(AtomicConstructor::new)
                    .collect(CustomCollector.concurrentSet());

            if (constructorSet.parallelStream().noneMatch(AtomicConstructor::isNoArgConstructor))
                logger.fatal("Declared AtomicType doesnt have NoArgsConstructor it can potentially invoke undefined behaviors", Priority.IMPORTANT, AtomicType.class);


        } else {
            fieldSet = ConcurrentHashMap.newKeySet();
            annotationSet = ConcurrentHashMap.newKeySet();
            methodSet = ConcurrentHashMap.newKeySet();
        }
    }


    public Optional<AtomicMethod> getPostConstructor() {
        if (isPostConstructable)
            return Optional.of(postConstructor);
        return Optional.empty();
    }

    public static AtomicLogger getLogger() {
        return logger;
    }

    public boolean isAtomic() {
        return isAtomic;
    }

    public Class<?> getCorrespondingType() {
        return correspondingType;
    }

    public Set<AtomicField> getFieldSet() {
        return fieldSet;
    }

    public Set<AtomicAnnotation> getAnnotationSet() {
        return annotationSet;
    }

    public Set<AtomicMethod> getMethodSet() {
        return methodSet;
    }

    public Set<AtomicConstructor> getConstructorSet() {
        return constructorSet;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AtomicType)
            return ((AtomicType) o).correspondingType.equals(correspondingType);
        return correspondingType.equals(o);
    }

    @Override
    public int hashCode() {
        return correspondingType.hashCode();
    }
}
