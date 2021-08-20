package me.nort3x.atomic.core.container;

import me.nort3x.atomic.annotation.Atom;
import me.nort3x.atomic.basic.AtomicModule;
import me.nort3x.atomic.logger.AtomicLogger;
import me.nort3x.atomic.logger.Priority;
import me.nort3x.atomic.wrappers.AtomicField;
import me.nort3x.atomic.wrappers.AtomicType;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/*
Container is a Set of Elements around type X s.t. { type y | there is at least one path between X and y by recursive dependency  } Union.with {X itself}
in other sense Container is the smallest Set of instances such that X can function as it should and can be considered independent from everything else
 */
public class Container {

    // statically caching internal creator Set for later calls
    private static final ConcurrentHashMap<AtomicType, Set<AtomFieldSchematic>> alreadyScanned = new ConcurrentHashMap<>();

    // holding instances each corresponding to on Element of Container
    private final ConcurrentHashMap<AtomicType, Object> instances = new ConcurrentHashMap<>();

    // preserver instance of central element this Container is describing
    final AtomicType formedAround;

    private static final ConcurrentHashMap<AtomicType, Object> sharedCrossEveryOne = new ConcurrentHashMap<>();
    private final Set<AtomFieldSchematic> internalSet;

    // a Container is formed when all of its elements are working
    private Container(Set<AtomFieldSchematic> closedSetOfTypes, AtomicType formedAround) {

        this.formedAround = formedAround;
        this.internalSet = closedSetOfTypes;

        // create all
        makeAndAddInstance(closedSetOfTypes);
        closedSetOfTypes.parallelStream().forEach(type -> Configurator.configAndGet(type.type, instances, this));
    }


//    private static Set<Object> alreadyConfiguredObject =Collections.newSetFromMap(Collections.synchronizedMap(new WeakHashMap<>()));
//
//    private static void configureInstance(Object o){
//        if(!alreadyConfiguredObject.contains(o)){
//
//        }
//    }


    private void makeAndAddInstance(Set<AtomFieldSchematic> set) {
        set.stream() // for types
                .filter(atomicType -> {
                    if (atomicType.type.getNoArgsConstructor() != null) // if no argsConstructor exist
                        return true;
                    AtomicLogger.getInstance().fatal("AtomicType: " + atomicType.type.getCorrespondingType().getName() + "has NoArgsConstructor", Priority.VERY_IMPORTANT, Container.class);
                    return false;
                })
                .forEach(atomicType -> {
                    if (atomicType.atomScope == Atom.Scope.PER_CONTAINER)
                        instances.put(atomicType.type, createInstance(atomicType.type));
                    else if (atomicType.atomScope == Atom.Scope.GLOBAL) {
                                instances.put(atomicType.type, sharedCrossEveryOne.computeIfAbsent(atomicType.type, this::createInstance));
                            }
                        }
                );
    }


    /**
     * @param atomicType referring type
     * @return a unique fresh instance of asked AtomicType or null if doesnt exist in this Container
     * @see Container#getCentralUnique()
     */
    public Object getUnique(AtomicType atomicType) {
        if (!instances.containsKey(atomicType)) {
            AtomicLogger.getInstance().fatal("Requesting AtomicType: " + atomicType.getCorrespondingType().getName() + " which is not present around: " + formedAround.getCorrespondingType().getName(), Priority.VERY_IMPORTANT, Container.class);
        }
        return instances.getOrDefault(atomicType, null);
    }


    /**
     * @param atomicType referring type
     * @return if any or create instance of asked AtomicType or null if doesnt exist in this Container
     * @see Container#getCentralShared()
     */
    public Object getShared(AtomicType atomicType) {
        if (!instances.containsKey(atomicType)) {
            AtomicLogger.getInstance().fatal("Requesting AtomicType: " + atomicType.getCorrespondingType().getName() + " which is not present around: " + formedAround.getCorrespondingType().getName(), Priority.VERY_IMPORTANT, Container.class);
        }
        return sharedCrossEveryOne.computeIfAbsent(atomicType, x -> getUnique(atomicType));
    }

    /**
     * @return a unique fresh instance of central element which is the most dependent element of this Container
     */
    public Object getCentralUnique() {
        return getUnique(formedAround);
    }

    /**
     * @return if any or create an instance of central element which is the most dependent element of this Container
     */
    public Object getCentralShared() {
        return getShared(formedAround);
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
    private static Set<AtomFieldSchematic> makeContainerRelationSet(AtomicType type) {
        Set<AtomFieldSchematic> containerRelationSet = ConcurrentHashMap.newKeySet(); // make relation set
        containerRelationSet.add(new AtomFieldSchematic(Atom.Scope.PER_CONTAINER, type));                                      // add 'root' type to it
        addMutualDependenciesRecursive(type, containerRelationSet);          // add everything else respect to root recursively (it modifies set)
        return containerRelationSet;                                         // return respectively
    }

    private static class AtomFieldSchematic {

        Atom.Scope atomScope;
        AtomicType type;

        public AtomFieldSchematic(Atom.Scope scope, AtomicType type) {
            this.atomScope = scope;
            this.type = type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof AtomFieldSchematic)) return false;
            AtomFieldSchematic that = (AtomFieldSchematic) o;
            return atomScope == that.atomScope && Objects.equals(type, that.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(atomScope, type);
        }

    }

    private static void addMutualDependenciesRecursive(AtomicType atomicType, Set<AtomFieldSchematic> fillThisSet) {
        atomicType.getFieldSet().parallelStream()
                .filter(AtomicField::isAtom)
                .filter(atomicField -> !atomicField.isPredefined())
                .map(field -> {
                    Atom anon = field.getCorrespondingField().getAnnotation(Atom.class);
                    AtomicType type = anon.concreteType().equals(Object.class) ?
                            AtomicType.of(field.getType()) :
                            AtomicType.of(anon.concreteType());

                    if (!anon.concreteType().equals(Object.class)) {
                        addMutualDependenciesRecursive(type, fillThisSet);
                    }

                    Atom.Scope scope = anon.scope();
                    return new AtomFieldSchematic(anon.scope(), type);
                })
                .peek(field -> {
                    if (field.type.isSubOf(AtomicType.of(AtomicModule.class))) { // overriding scope in case of module
                        if (!field.atomScope.equals(Atom.Scope.GLOBAL))
                            AtomicLogger.getInstance().warning("Atom Field of Type AtomicModule can Only be Scoped Global, overriding scope of Field: " + field.type.getCorrespondingType().getName(), Priority.VERY_IMPORTANT, Container.class);
                        field.atomScope = Atom.Scope.GLOBAL;
                    }
                })
                .forEach(subAtom -> {
                    if (!fillThisSet.contains(subAtom)) {
                        fillThisSet.add(subAtom);
                        addMutualDependenciesRecursive(subAtom.type, fillThisSet);
                    }
                });
    }


}
