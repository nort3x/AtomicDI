package me.nort3x.atomic.core.container;

import me.nort3x.atomic.logger.AtomicLogger;
import me.nort3x.atomic.logger.Priority;
import me.nort3x.atomic.reactor.LinearReactor;
import me.nort3x.atomic.reactor.ParallelReactor;
import me.nort3x.atomic.wrappers.AtomicType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;


/**
 * responsible for configuration of given object within caller Container
 */
public class Configurator {

    // Parallel reactor will set all the fields
    private final ParallelReactor<TupleOfInstanceAndContainerMap> fieldSetters = new ParallelReactor<>();
    // postConfigurator sequentially will add the final touch
    private final LinearReactor<TupleOfInstanceAndContainerMap> postConfigurations = new LinearReactor<>();


    @SuppressWarnings("rawtypes")
    private Configurator(AtomicType atomicType, Container container) {
        atomicType.getFieldSet().parallelStream()
                .forEach(field -> {
                    fieldSetters.addReaction(new Consumer<TupleOfInstanceAndContainerMap>() {
                        final AtomicType at = AtomicType.of(field.getType()); // cache for faster lookup

                        @Override
                        public void accept(TupleOfInstanceAndContainerMap tp) {
                            field.setField(tp.obj, tp.map.get(at));
                        }
                    });
                });

        atomicType.getPostConstructor().ifPresent(postConfig -> postConfigurations.addReaction(tupleOfInstanceAndContainerMap -> {
            postConfig.invoke(tupleOfInstanceAndContainerMap.obj, exp -> {
                AtomicLogger.getInstance().warning("PostConfiguration: " + atomicType.getCorrespondingType().getName() + "." + postConfig.getCorrespondingMethod().getName()
                        + " Thrown Exception: " + AtomicLogger.exceptionToString(exp), Priority.IMPORTANT, Configurator.class);
            }, container.provideAsParameterForMethod(postConfig));
        }));

    }

    private static final Map<AtomicType, Configurator> configurators = new ConcurrentHashMap<>();


    static Object configAndGet(AtomicType t, Map<AtomicType, Object> instances, Container container) {
        Configurator cfg = configurators.computeIfAbsent(t, type -> new Configurator(t, container));
        return cfg.postConfigurations.reactOn(
                cfg.fieldSetters.reactOn(new TupleOfInstanceAndContainerMap() {{
                    this.map = instances;
                    this.obj = instances.get(t);
                }})
        );
    }

    static class TupleOfInstanceAndContainerMap {
        Map<AtomicType, Object> map;
        Object obj;
    }

}
