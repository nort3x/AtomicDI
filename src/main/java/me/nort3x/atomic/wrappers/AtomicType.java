package me.nort3x.atomic.wrappers;

import me.nort3x.atomic.logger.AtomicLogger;
import me.nort3x.atomic.logger.enums.Priority;
import me.nort3x.atomic.utility.CustomCollector;
import org.slf4j.Logger;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class AtomicType {

    static final Logger loggerVerbose = AtomicLogger.getInstance().getLogger(AtomicType.class,Priority.VERBOSE);
    static final Logger loggerImportant = AtomicLogger.getInstance().getLogger(AtomicType.class,Priority.VERY_IMPORTANT);
    static final Logger loggerDebug = AtomicLogger.getInstance().getLogger(AtomicType.class,Priority.DEBUG);


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
                    .peek(x -> loggerVerbose.debug("Discovered AtomicField: " + x.getName() + " In AtomicType: " + type.getName()))
                    .filter(x -> {
                        if (!AtomicAnnotation.isExcluded(x))
                            return true;
                        loggerVerbose.debug("Excluded AtomicField: " + x.getName() + " from AtomicType: " + type.getName());
                        return false;
                    })
                    .peek(field -> {
                        int modifier = field.getModifiers();
                        if (Modifier.isStatic(modifier)) {
                            loggerVerbose.warn("AtomicField is <Static> and its highly discouraging consider using Shared type in Atom annotation: " + field.getName() + " from AtomicType: " + type.getName());
                        } else if (Modifier.isFinal(modifier)) {
                            loggerVerbose.warn("AtomicField is <Final> and its highly discouraging, it will be reinitialized if NoArgsConstructor exist: " + field.getName() + " from AtomicType: " + type.getName());
                        }
                    })
                    .peek(field -> field.setAccessible(true))
                    .map(AtomicField::new)
                    .collect(CustomCollector.concurrentSet());


            methodSet = Arrays.stream(type.getDeclaredMethods()).parallel()
                    .filter(AtomicAnnotation::isInterAction)
                    .peek(x -> loggerDebug.debug("Discovered Interaction: " + x.getName() + " In AtomicType: " + type.getName()))
                    .filter(x -> {
                        if (!AtomicAnnotation.isExcluded(x))
                            return true;
                        loggerDebug.debug("Excluded InterAction: " + x.getName() + " from AtomicType: " + type.getName());
                        return false;
                    }).peek(field -> field.setAccessible(true))
                    .map(AtomicMethod::new)
                    .peek(method -> {
                        if (method.isPostConstructor())
                            loggerDebug.debug("Discovered PostConstructor InterAction: " + method.getCorrespondingMethod().getName() + " In AtomicType: " + type.getName());
                    })
                    .collect(CustomCollector.concurrentSet());

            methodSet.parallelStream()
                    .filter(AtomicMethod::isPostConstructor)
                    .findFirst().ifPresent(x -> {
                postConstructor = x;
                isPostConstructable = true;
                loggerDebug.debug("Selected PostConstructor: " + x.getCorrespondingMethod().getName() + " for AtomicType: " + type.getName());
            });

            if (methodSet.parallelStream().filter(AtomicMethod::isPostConstructor).count() > 1)
                loggerImportant.error("Multiple PostConstructor Detected and its highly discouraging because of parallel behaviors, consider Excluding all except one,  for AtomicType: " + type.getName());


            constructorSet = Arrays.stream(type.getConstructors()).parallel()
                    .peek(x -> x.setAccessible(true))
                    .map(AtomicConstructor::new)
                    .collect(CustomCollector.concurrentSet());

            if (!(type.isInterface() || type.isAnnotation() || Modifier.isAbstract(type.getModifiers()))) {
                Optional<AtomicConstructor> ngc_bean = constructorSet.stream().filter(AtomicConstructor::isNoArgConstructor).findFirst();
                if (!ngc_bean.isPresent()) {
                    loggerImportant.error("NoArgsConstructor not found for AtomicType: " + type.getName() + " could not generate instance from given Type, any dependent Type will be affected ");
                    System.exit(-1);
                }
                ngc = ngc_bean.get();
            }


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
    public static ConcurrentHashMap<Class<?>, AtomicType> getAlreadyScannedTypesAtomicTypes(){
        return alreadyScannedTypesAtomicTypes;
    }
    public static AtomicType of(Class<?> clazz) {
        return alreadyScannedTypesAtomicTypes.computeIfAbsent(clazz, AtomicType::new);
    }

    public boolean isAnnotationPresent(AtomicAnnotation atomicAnnotation) {
        return annotationSet.stream().anyMatch(atomicAnnotation1 -> atomicAnnotation1.isAssignableFrom(atomicAnnotation));
    }

    public boolean isSubOf(AtomicType atomicType1) {
        return atomicType1.correspondingType.isAssignableFrom(correspondingType);
    }

    public boolean isSuperFor(AtomicType atomicType1) {
        return correspondingType.isAssignableFrom(atomicType1.correspondingType);
    }
}
