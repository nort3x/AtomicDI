package me.nort3x.atomic.core.integrator;

import me.nort3x.atomic.annotation.Predefined;
import me.nort3x.atomic.logger.AtomicLogger;
import me.nort3x.atomic.logger.Priority;
import me.nort3x.atomic.wrappers.AtomicField;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class PredefinedLoader {


    private static final ConcurrentHashMap<String,Value> values = new ConcurrentHashMap<>();


    public static void addDefinitionFile(InputStream inputStream) throws IOException {
        Properties p = new Properties();
        p.load(inputStream);
        p.entrySet().forEach(entry->{
            values.put(entry.getKey().toString(),new Value(entry.getKey().toString(),entry.getValue().toString()));
        });
    }

    public static void setPredefinedValue(AtomicField f, Object obj) {
        if (!f.isPredefined()) {
            AtomicLogger.getInstance().warning("Predefined field Injector is called for AtomicField: " + f.getCorrespondingField().getDeclaringClass().getName() + "." + f.getCorrespondingField().getName() + "but its not annotated with Predefined", Priority.IMPORTANT, PredefinedLoader.class);
        }
        String key = f.getCorrespondingField().getAnnotation(Predefined.class).value();
        Value value = values.getOrDefault(key,null);
        if (value == null) {
            AtomicLogger.getInstance().warning("Predefined key-value: " + f.getCorrespondingField().getDeclaringClass().getName() + "." + f.getCorrespondingField().getName() + " is [not Found] among scanned entry paths", Priority.IMPORTANT, PredefinedLoader.class);
            return;
        }
        f.setField(obj,value.readFor(f.getType()));
    }


}
