package me.nort3x.atomic.bean;

import me.nort3x.atomic.logger.AtomicLogger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * will act as middleware between grapher and {@link Constructor} so that exception handling become more reasonable
 */
public class SafeConstructor {
    private final ConcurrentHashMap<Class<?>, Constructor<?>> allConstructors = new ConcurrentHashMap<>(); // caching No-Arg-Constructor of each type avoiding frequent lookups

    protected void putNewClasses(Collection<Class<?>> clazzes) {
        clazzes.parallelStream().forEach(x -> {
            Optional<Constructor<?>> con = ReflectionUtils.getNoArgsConstructor(x);
            if (con.isPresent())
                allConstructors.putIfAbsent(x, con.get());
            else
                AtomicLogger.getInstance().complain_NorArgConstructor(x);
        });
    }

    protected Optional<Constructor<?>> getConstructor(Class<?> clazz) {
        Constructor<?> constructor = allConstructors.get(clazz);
        return constructor == null ? Optional.empty() : Optional.of(constructor);
    }

    protected Optional<Object> getNewInstance(Class<?> clazz) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Optional<Constructor<?>> op = getConstructor(clazz);
        if (!op.isPresent())
            return Optional.empty();
        return Optional.of(op.get().newInstance());
    }

}
