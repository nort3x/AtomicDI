package me.nort3x.atomic.consumer;

import me.nort3x.atomic.core.AtomicDI;
import me.nort3x.atomic.core.container.Container;
import me.nort3x.atomic.wrappers.AtomicAnnotation;
import me.nort3x.atomic.wrappers.AtomicType;

import java.lang.annotation.Annotation;

public class Provider {

    public AtomicAnnotation getAtomicAnnotation(Class<? extends Annotation> annotation) {
        return AtomicDI.getInstance().getGreedyBag().getAtomicAnnotation(annotation);
    }

    public AtomicType getAtomicType(Class<?> type) {
        return AtomicDI.getInstance().getGreedyBag().getAtomicType(type);
    }

    public Container spawnNewContainer(AtomicType atomicType) {
        return Container.makeContainerAround(atomicType);
    }

    public Container spawnNewContainer(Class<?> atomicType) {
        return Container.makeContainerAround(getAtomicType(atomicType));
    }


}
