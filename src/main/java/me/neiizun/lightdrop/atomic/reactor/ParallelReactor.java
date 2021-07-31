package me.neiizun.lightdrop.atomic.reactor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * <h2>ParallelReactor</h2>
 * this class can hold operations and parallel invoke those operations on given object
 * <br/>
 * <br/>
 *
 * <h3>LifeCycle</h3>
 * <blockquote>
 *     <li>
 *         init()
 *     </li>
 *     <li>
 *          addReaction(...)
 *       </li>
 *  <li>
 *           sealReactor()
 *       </li>
 *  <li>
 *           actOn(....)
 *       </li>
 * </blockquote>
 * @version 0.0.0
 * @author H.ardaki
 */
public class ParallelReactor<T> implements ReactorLike<T>{
    List<Consumer<T>> reactions = new ArrayList<>();
    AtomicInteger atomicInteger = new AtomicInteger(0);

    @Override
    public void addReaction(Consumer<T> reaction){
        atomicInteger.updateAndGet(x->{
            if(x==0)
                reactions.add(reaction);
            return x;
        });
    }

    @Override
    public void sealReactor(){
        atomicInteger.incrementAndGet();
    }


    public T actOn(T t){
        reactions.parallelStream().forEach(x->x.accept(t));
        return t;
    }

}
