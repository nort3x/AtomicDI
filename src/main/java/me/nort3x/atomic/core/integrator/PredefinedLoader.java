package me.nort3x.atomic.core.integrator;

import me.nort3x.atomic.annotation.Predefined;
import me.nort3x.atomic.logger.AtomicLogger;
import me.nort3x.atomic.logger.enums.Priority;
import me.nort3x.atomic.wrappers.AtomicField;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class PredefinedLoader {


    private static final ConcurrentHashMap<String, Value> values = new ConcurrentHashMap<>();


    public static void addDefinitionFile(InputStream inputStream) throws IOException {
        Properties p = new Properties();
        p.load(inputStream);
        p.entrySet().forEach(entry -> {
            values.put(entry.getKey().toString(), new Value(entry.getKey().toString(), entry.getValue().toString()));
        });
    }

    private static Logger l = AtomicLogger.getInstance().getLogger(PredefinedLoader.class, Priority.IMPORTANT);

    public static void setPredefinedValue(AtomicField f, Object obj) {
        if (!f.isPredefined()) {
            l.warn("Predefined field Injector is called for AtomicField: " + f.getCorrespondingField().getDeclaringClass().getName() + "." + f.getCorrespondingField().getName() + "but its not annotated with Predefined");
        }
        String key = f.getCorrespondingField().getAnnotation(Predefined.class).value();
        String value_env = System.getenv(key.toUpperCase(Locale.ROOT).replace(".", "_"));
        Value value = value_env == null ? null : new Value(key, value_env);
        if (value == null)
            value = values.getOrDefault(key, null);
        if (value == null) {
            l.warn("Predefined key-value: " + f.getCorrespondingField().getDeclaringClass().getName() + "." + f.getCorrespondingField().getName() + " is [not Found] among scanned entry paths");
            return;
        }
        f.setField(obj, value.readFor(f.getType()));
    }


}
