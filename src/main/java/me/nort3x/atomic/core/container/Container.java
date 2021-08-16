package me.nort3x.atomic.core.container;

import me.nort3x.atomic.logger.AtomicLogger;
import me.nort3x.atomic.logger.Priority;
import me.nort3x.atomic.wrappers.AtomicField;
import me.nort3x.atomic.wrappers.AtomicType;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/*
Container is a Set of Elements around type X s.t. { type y | there is at least one path between X and y by recursive dependency  } Union.with {X itself}
in other sense Container is the smallest Set of instances such that X can function as it should and can be considered independent from everything else
 */
public class Container {

    // statically caching internal creator Set for later calls
    private static final ConcurrentHashMap<AtomicType, Set<AtomicType>> alreadyScanned = new ConcurrentHashMap<>();

    // holding instances each corresponding to on Element of Container
    private final ConcurrentHashMap<AtomicType, Object> instances = new ConcurrentHashMap<>();

    // preserver instance of central element this Container is describing
    final AtomicType formedAround;


    // a Container is formed when all of its elements are working
    private Container(Set<AtomicType> closedSetOfTypes, AtomicType formedAround) {

        this.formedAround = formedAround;

        // create all
        closedSetOfTypes.stream() // for types
                .filter(atomicType -> {
                    if (atomicType.getNoArgsConstructor() != null) // if no argsConstructor exist
                        return true;
                    AtomicLogger.getInstance().fatal("AtomicType: " + atomicType.getCorrespondingType().getName() + "has NoArgsConstructor", Priority.VERY_IMPORTANT, Container.class);
                    return false;
                })
                .forEach(atomicType -> instances.put(atomicType, createInstance(atomicType)));

        // wire all
        closedSetOfTypes.parallelStream()
                .forEach(atomicType -> atomicType.getFieldSet().parallelStream()
                        .forEach(atomicField -> atomicField.setField(instances.get(atomicType), instances.get(AtomicType.of(atomicField.getType())))));

    }


    /**
     * @param atomicType referring type
     * @return instance of asked AtomicType or null if doesnt exist in this Container
     */
    public Object get(AtomicType atomicType) {
        if (!instances.containsKey(atomicType)) {
            AtomicLogger.getInstance().fatal("Requesting AtomicType: " + atomicType.getCorrespondingType().getName() + " which is not present around: " + formedAround.getCorrespondingType().getName(), Priority.VERY_IMPORTANT, Container.class);
        }
        return instances.getOrDefault(atomicType, null);
    }

    /**
     * @return instance of central element which is the most dependent element of this Container
     */
    public Object getCentral() {
        return get(formedAround);
    }


    private Object createInstance(AtomicType type) {
        return type.getNoArgsConstructor().getInstance()
                .orElseGet(() -> {
                    AtomicLogger.getInstance().fatal("AtomicCreation Failed for AtomicType: " + type.getCorrespondingType().getName() + " in Container around: " + formedAround.getCorrespondingType().getName(), Priority.VERY_IMPORTANT, Container.class);
                    return null;
                });

    }


    public static Container makeContainerAround(AtomicType atomicType) {
        return new Container(alreadyScanned.computeIfAbsent(atomicType, Container::makeContainerRelationSet), atomicType);
    }

    /**
     * @param type given atomicType as root-element
     * @return a set of every atomic element root will need for functioning
     * Authored, by Kemikals
     */
    private static Set<AtomicType> makeContainerRelationSet(AtomicType type) {
        Set<AtomicType> containerRelationSet = ConcurrentHashMap.newKeySet(); // make relation set
        containerRelationSet.add(type);                                      // add 'root' type to it
        addMutualDependenciesRecursive(type, containerRelationSet);          // add everything else respect to root recursively (it modifies set)
        return containerRelationSet;                                         // return respectively
    }

    private static void addMutualDependenciesRecursive(AtomicType atomicType, Set<AtomicType> fillThisSet) {
        atomicType.getFieldSet().parallelStream()
                .map(AtomicField::getCorrespondingField)
                .map(Field::getType)
                .map(AtomicType::of)
                .forEach(subAtom -> {
                    if (!fillThisSet.contains(subAtom)) {
                        fillThisSet.add(subAtom);
                        addMutualDependenciesRecursive(subAtom, fillThisSet);
                    }
                });
    }


}
