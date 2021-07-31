package me.neiizun.lightdrop.atomic.reactor;

import me.neiizun.lightdrop.atomic.bean.DependencyGrapher;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.function.Consumer;

public class Factory<T> implements ReactorLike<T>{
    private final Constructor<T> constructor;
    LinearReactor<T> recPre = new LinearReactor<>();
    ParallelReactor<T> recInter = new ParallelReactor<>();
    LinearReactor<T> recPost = new LinearReactor<>();

    public Factory(Constructor<T> constructor){
        this.constructor = constructor;
    }

    @Override
    public void addReaction(Consumer<T> o) {
        recInter.addReaction(o);
    }
    public void addPreReaction(Consumer<T> o) {
        recPre.addReaction(o);
    }
    public void addPostReaction(Consumer<T> o) {
        recPost.addReaction(o);
    }





    @Override
    public void sealReactor() {

        recPost.sealReactor();
        recInter.sealReactor();
        recPre.sealReactor();

    }

    public Optional<T> generate(){
        try {
            T instance = constructor.newInstance();
            return  Optional.of(
                    recPost.actOn(recInter.actOn(recPre.actOn(instance)))
            );

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            LoggerFactory.getLogger(DependencyGrapher.class).error("making instance of Atomic type failed: ", e);
            return Optional.empty();
        }
    }
}
