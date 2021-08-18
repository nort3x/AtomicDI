package me.nort3x.atomic.core;

import me.nort3x.atomic.utility.CustomCollector;
import me.nort3x.atomic.wrappers.AtomicAnnotation;
import me.nort3x.atomic.wrappers.AtomicType;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AtomicEnvironment {
    ConcurrentHashMap<AtomicAnnotation,Set<AtomicType>> typesAnnotatedWith = new ConcurrentHashMap<>();
    ConcurrentHashMap<AtomicType,Set<AtomicType>> subTypedFrom = new ConcurrentHashMap<>();
    Set<AtomicType> getAllAtomicTypesAnnotatedWith(AtomicAnnotation atomicAnnotation){
         return typesAnnotatedWith.computeIfAbsent(atomicAnnotation,
                 annotation->
                 AtomicType.getAlreadyScannedTypesAtomicTypes().values().stream()
                         .parallel()
                         .filter(atomicType -> atomicType.isAnnotationPresent(annotation))
                         .collect(CustomCollector.concurrentSet())
         );

    }
    Set<AtomicType> getAllAtomicTypesDerivedFromAtomicType(AtomicType atomicType){
        return subTypedFrom.computeIfAbsent(atomicType,
                atomicType1->
                        AtomicType.getAlreadyScannedTypesAtomicTypes().values().stream()
                                .parallel()
                                .filter(type -> type.isSubOf(atomicType1))
                                .collect(CustomCollector.concurrentSet())
        );
    }
}
