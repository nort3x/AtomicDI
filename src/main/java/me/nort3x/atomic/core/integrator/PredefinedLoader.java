package me.nort3x.atomic.core.integrator;

import me.nort3x.atomic.logger.AtomicLogger;
import me.nort3x.atomic.logger.Priority;
import me.nort3x.atomic.wrappers.AtomicField;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

// todo
public class PredefinedLoader {


    private static final ConcurrentHashMap<String, Value> values = new ConcurrentHashMap<>();

    public static void addDefinitionFile(File f) throws IOException {
        String content = new String(Files.readAllBytes(f.toPath()));
        Arrays.stream(content.split(";")).parallel()
                .forEach(entry -> {
                    String[] key_value = entry.split("->");
                    if (key_value.length != 2) {
                        AtomicLogger.getInstance().warning("Entry: " + entry + " in DefinitionFile: " + f + " is not valid it should be like: 'Key -> Value ;' this value will be ignored and may invoke undefined behavior", Priority.VERY_IMPORTANT, PredefinedLoader.class);
                        return;
                    }
                    if (values.containsKey(key_value[0])) {
                        AtomicLogger.getInstance().warning("Entry: " + entry + " in DefinitionFile: " + f + " is going tp replace already-existing Entry: " + values.get(key_value[0]).toString(), Priority.VERBOSE, PredefinedLoader.class);
                    }
                    values.put(key_value[0], new Value(key_value[0], key_value[1]));
                });
    }

    public static void setPredefinedValue(AtomicField f, Object obj) {
//        f.getCorrespondingField().set(obj,);
    }


}
