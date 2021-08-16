package me.nort3x.atomic.core.container;

import me.nort3x.atomic.logger.AtomicLogger;
import me.nort3x.atomic.logger.Priority;
import me.nort3x.atomic.wrappers.AtomicField;
import me.nort3x.atomic.wrappers.AtomicType;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Container is a closed set of recursive dependencies between bunch of object,
 * Container doesnt now how its created, it doesnt have a root, the only thing it knows is that its closed
 * so it will create instances of asked objects if needed any will entangle them inside each other if needed
 */
public class Container {

    private final ConcurrentHashMap<AtomicType, Object> instances = new ConcurrentHashMap<>();
    final AtomicType formedAround;

    private Container(Set<AtomicType> closedSetOfTypes, AtomicType formedAround) {

        this.formedAround = formedAround;

        // create all
        closedSetOfTypes.stream() // for types
                .filter(atomicType -> {
                    if (atomicType.getNoArgsConstructor() != null) // if no argsConstructor exist
                        return true;
                    AtomicLogger.getInstance().fatal("AtomicType: " + atomicType.getCorrespondingType().getName() + "has NoArgsConstructor", Priority.VERY_IMPORTANT, Container.class);
                    return false;
                }).forEach(atomicType -> instances.put(atomicType, instanceCreator(atomicType)));


        // config all
        closedSetOfTypes.parallelStream()
                .forEach(atomicType -> atomicType.getFieldSet().parallelStream()
                        .forEach(atomicField -> atomicField.setField(instances.get(atomicType), instances.get(AtomicType.getOrCreate(atomicField.getType())))));

    }


    public Object get(AtomicType atomicType) {
        if (!instances.containsKey(atomicType)) {
            AtomicLogger.getInstance().fatal("Requesting AtomicType: " + atomicType.getCorrespondingType().getName() + " which is not present around: " + formedAround.getCorrespondingType().getName(), Priority.VERY_IMPORTANT, Container.class);
        }
        return instances.getOrDefault(atomicType, null);
    }


    private Object instanceCreator(AtomicType type) {
        Optional<Object> probablyInstance = type.getNoArgsConstructor().getInstance();
        if (probablyInstance.isPresent())
            return probablyInstance.get();
        AtomicLogger.getInstance().fatal("AtomicCreation Failed for AtomicType: " + type.getCorrespondingType().getName() + " in Container around: " + formedAround.getCorrespondingType().getName(), Priority.VERY_IMPORTANT, Container.class);
        return null;

    }

    public static Container makeContainerAround(AtomicType atomicType) {
        Optional<Set<AtomicType>> alreadyExist = scannedContainers.stream().filter(container -> container.contains(atomicType)).findFirst();
        if (alreadyExist.isPresent())
            return new Container(alreadyExist.get(), atomicType);
        else {
            Set<AtomicType> containerInternalSet = ConcurrentHashMap.newKeySet();
            containerInternalSet.add(atomicType);
            addMutualDependenciesRecursive(atomicType, containerInternalSet);
            return new Container(containerInternalSet, atomicType);
        }
    }

    private static void addMutualDependenciesRecursive(AtomicType atomicType, Set<AtomicType> fillThisSet) {
        atomicType.getFieldSet().parallelStream()
                .map(AtomicField::getCorrespondingField)
                .map(Field::getType)
                .map(AtomicType::getOrCreate)
                .forEach(subAtom -> {
                    if (!fillThisSet.contains(subAtom)) {
                        fillThisSet.add(subAtom);
                        addMutualDependenciesRecursive(subAtom, fillThisSet);
                    }
                });
    }

    private static final Set<Set<AtomicType>> scannedContainers = ConcurrentHashMap.newKeySet();


}
