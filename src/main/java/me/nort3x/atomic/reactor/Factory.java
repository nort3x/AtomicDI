package me.nort3x.atomic.reactor;

import me.nort3x.atomic.logger.AtomicLogger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * //todo
 *
 * @param <T>
 */
public class Factory<T> implements ReactorLike<T>{
    private final Constructor<T> constructor;
    private final LinearReactor<T> recPre = new LinearReactor<>();
    private final ParallelReactor<T> recInter = new ParallelReactor<>();
    private final LinearReactor<T> recPost = new LinearReactor<>();

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
            AtomicLogger.getInstance().complain_NorArgConstructor(constructor.getDeclaringClass());
            return Optional.empty();
        }
    }
}
