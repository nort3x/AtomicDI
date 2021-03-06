package me.nort3x.atomic.core.integrator;

import me.nort3x.atomic.logger.AtomicLogger;
import me.nort3x.atomic.logger.enums.Priority;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Value {

    private static final Map<Class<?>, Function<String, Object>> handlers = new HashMap<>();

    // 8 primitives
    static {
        handlers.put(Long.class, Long::valueOf);
        handlers.put(Byte.class, Byte::valueOf);
        handlers.put(Boolean.class, Boolean::valueOf);
        handlers.put(Integer.class, Integer::valueOf);
        handlers.put(Short.class, Short::valueOf);
        handlers.put(Double.class, Double::valueOf);
        handlers.put(Character.class, x -> x.charAt(0));
        handlers.put(Float.class, Float::valueOf);
    }


    private final String key;
    private final String valueAsString;

    public Value(String key, String valueAsString) {
        this.key = key;
        this.valueAsString = valueAsString;
    }

    @SuppressWarnings("unchecked")
    public Object readFor(Class<?> type) {
        try {
            if (type.equals(String.class)) {
                return valueAsString;
            } else if (type.isEnum()) {
                return Enum.valueOf((Class<Enum>) type, valueAsString);
            } else if (handlers.containsKey(type)) {
                return handlers.get(type).apply(valueAsString);
            }
        } catch (Exception e) {
            AtomicLogger.getInstance().getLogger(Value.class,Priority.VERY_IMPORTANT).error("Resolving Predefined '" + key + "' : '" + valueAsString + "' to " + type.getName() + " resulted an Exception: " + AtomicLogger.exceptionToString(e));
        }
        AtomicLogger.getInstance().getLogger(Value.class,Priority.VERY_IMPORTANT).warn("requested Predefined '" + key + "' : '" + valueAsString + "' could not be resolved to given type: " + type.getName() + " because its not a supported type, caller will receive null");
        return null;
    }

    @Override
    public String toString() {
        return key + " : " + valueAsString;
    }
}
