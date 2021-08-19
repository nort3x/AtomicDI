package me.nort3x.atomic.core.integrator;

import me.nort3x.atomic.annotation.Predefined;
import me.nort3x.atomic.logger.AtomicLogger;
import me.nort3x.atomic.logger.Priority;
import me.nort3x.atomic.wrappers.AtomicField;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class PredefinedLoader {


    private static final ConcurrentHashMap<String, Value> values = new ConcurrentHashMap<>();


    public static void addDefinitionFile(File f) throws IOException {

        if (!f.exists()) {
            AtomicLogger.getInstance().warning("Couldn't find ini File: " + f.toURI() + " for addition to PredefinedLoader ", Priority.IMPORTANT, PredefinedLoader.class);
            return;
        }
        AtomicLogger.getInstance().info("Loading Predefined ini File: " + f.toURI(), Priority.VERBOSE, PredefinedLoader.class);
        String content = new String(Files.readAllBytes(f.toPath()));
        Arrays.stream(content.split("\n")).parallel()
                .forEach(entry -> {
                    if (entry.trim().startsWith(";"))
                        return;
                    else if (entry.trim().startsWith("["))
                        return;
                    String[] key_value = entry.split("=");
                    if (key_value.length != 2) {
                        AtomicLogger.getInstance().warning("Entry: " + entry + " in DefinitionFile: " + f + " is not valid it should be like: 'Key -> Value ;' this value will be ignored and may invoke undefined behavior", Priority.VERY_IMPORTANT, PredefinedLoader.class);
                        return;
                    }
                    if (values.containsKey(key_value[0])) {
                        AtomicLogger.getInstance().warning("Entry: " + entry + " in DefinitionFile: " + f + " is going tp replace already-existing Entry: " + values.get(key_value[0]).toString(), Priority.VERBOSE, PredefinedLoader.class);
                    }
                    AtomicLogger.getInstance().info("Key: " + key_value[0] + " associated with: " + key_value[1] + " from Predefined ini File: " + f, Priority.VERBOSE, PredefinedLoader.class);
                    values.put(key_value[0].trim(), new Value(key_value[0].trim(), key_value[1].trim()));
                });
    }

    public static void setPredefinedValue(AtomicField f, Object obj) {
        if (!f.isPredefined()) {
            AtomicLogger.getInstance().warning("Predefined field Injector is called for AtomicField: " + f.getCorrespondingField().getDeclaringClass().getName() + "." + f.getCorrespondingField().getName() + "but its not annotated with Predefined", Priority.IMPORTANT, PredefinedLoader.class);
        }
        String key = f.getCorrespondingField().getAnnotation(Predefined.class).value();
        Value v = values.getOrDefault(key, null);
        if (v == null) {
            AtomicLogger.getInstance().warning("Predefined key-value: " + f.getCorrespondingField().getDeclaringClass().getName() + "." + f.getCorrespondingField().getName() + " is [not Found] among scanned entry paths", Priority.IMPORTANT, PredefinedLoader.class);
            return;
        }
        f.setField(obj, v.readFor(f.getType()));
    }


}
