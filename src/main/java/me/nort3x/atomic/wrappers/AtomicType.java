package me.nort3x.atomic.wrappers;

import me.nort3x.atomic.logger.AtomicLogger;
import me.nort3x.atomic.logger.Priority;
import me.nort3x.atomic.utility.CustomCollector;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class AtomicType {

    static final AtomicLogger logger = AtomicLogger.getInstance();

    boolean isAtomic = false;
    boolean isPostConstructable;

    final Class<?> correspondingType;
    final Set<AtomicField> fieldSet;
    Set<AtomicAnnotation> annotationSet;
    final Set<AtomicMethod> methodSet;
    Set<AtomicConstructor> constructorSet;
    AtomicMethod postConstructor;
    AtomicConstructor ngc;

    private AtomicType(Class<?> type) {
        correspondingType = type;

        annotationSet = Arrays.stream(correspondingType.getAnnotations()).parallel()
                .map(annotation -> AtomicAnnotation.of(annotation.annotationType()))
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
                    .peek(x -> x.setAccessible(true))
                    .map(AtomicConstructor::new)
                    .collect(CustomCollector.concurrentSet());

            Optional<AtomicConstructor> ngc_bean = constructorSet.stream().filter(AtomicConstructor::isNoArgConstructor).findFirst();
            if (!ngc_bean.isPresent()) {
                logger.fatal("NoArgsConstructor not found for AtomicType: " + type.getName() + " could not generate instance from given Type, any dependent Type will be affected ", Priority.VERY_IMPORTANT, AtomicType.class);
                System.exit(-1);
            }
            ngc = ngc_bean.get();


            if (constructorSet.parallelStream().noneMatch(AtomicConstructor::isNoArgConstructor))
                logger.fatal("Declared AtomicType: " + type.getName() + " doesnt have NoArgsConstructor it can potentially invoke undefined behaviors", Priority.IMPORTANT, AtomicType.class);


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

    public AtomicConstructor getNoArgsConstructor() {
        return ngc;
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


    private static final ConcurrentHashMap<Class<?>, AtomicType> alreadyScannedTypesAtomicTypes = new ConcurrentHashMap<>();

    public static AtomicType of(Class<?> clazz) {
        return alreadyScannedTypesAtomicTypes.computeIfAbsent(clazz, AtomicType::new);
    }
}
