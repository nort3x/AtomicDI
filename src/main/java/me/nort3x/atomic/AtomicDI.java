package me.nort3x.atomic;

import me.nort3x.atomic.basic.AtomicModule;
import me.nort3x.atomic.core.container.Container;
import me.nort3x.atomic.core.integrator.PredefinedLoader;
import me.nort3x.atomic.core.internal.AtomicEnvironment;
import me.nort3x.atomic.core.internal.Resolver;
import me.nort3x.atomic.logger.AtomicLogger;
import me.nort3x.atomic.logger.Priority;
import me.nort3x.atomic.wrappers.AtomicType;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static me.nort3x.atomic.core.integrator.PredefinedLoader.addDefinitionFile;

public class AtomicDI {

    static Set<Class<?>> scannablePaths = ConcurrentHashMap.newKeySet();

    public static void addAsScannablePath(Class<?> topClass) {
        scannablePaths.add(topClass);
    }


    static {
        addAsScannablePath(AtomicDI.class);
    }

    final Resolver rs;
    final AtomicEnvironment atomicEnvironment;
    private static final AtomicDI instance = new AtomicDI();

    public AtomicDI() {
        // assemble
        rs = new Resolver();
        atomicEnvironment = new AtomicEnvironment();
        try {
            URL url = PredefinedLoader.class.getClassLoader().getResource("AtomicDI.ini");
            if (url != null) {
                File resourceFile = new File(url.toURI());
                addDefinitionFile(resourceFile);
            }
            File pathFile = new File("AtomicDI.ini");
            addDefinitionFile(pathFile);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }


    ConcurrentHashMap<AtomicType, AtomicModule> modules = new ConcurrentHashMap<>();
    ExecutorService tp = Executors.newCachedThreadPool();

    private void resolve(Class<?> points, String[] args) {
        scannablePaths.add(points);
        scannablePaths.parallelStream().forEach(rs::resolve);
        atomicEnvironment.getAllAtomicTypesDerivedFromAtomicType(AtomicType.of(AtomicModule.class)).parallelStream()
                .filter(x -> !x.getCorrespondingType().equals(AtomicModule.class))
                .forEach(module -> {
                    AtomicModule am = (AtomicModule) Container.makeContainerAround(module).getCentralShared();
                    modules.put(module, am);
                    AtomicLogger.getInstance().info("LoadedModule: " + am.getName() + " : " + am.getVersion(), Priority.IMPORTANT, AtomicDI.class);
                });
        modules.values().parallelStream().forEach(x -> {
            x.onModuleLoaded(atomicEnvironment, args);
            AtomicLogger.getInstance().info("Invoked scanFinished of: " + x.getName() + " : " + x.getVersion(), Priority.IMPORTANT, AtomicDI.class);
        });

        modules.values().parallelStream().forEach(x -> {
            tp.submit(() -> {
                AtomicLogger.getInstance().info("startingModule : " + x.getName() + " : " + x.getVersion(), Priority.IMPORTANT, AtomicDI.class);
                x.onModuleStart(atomicEnvironment);
                AtomicLogger.getInstance().info("ModuleReturned : " + x.getName() + " : " + x.getVersion(), Priority.IMPORTANT, AtomicDI.class);
                x.onModuleStop(atomicEnvironment);
            });
        });
    }

    public static void run(Class<?> entryPoint, String... args) {
        instance.resolve(entryPoint, args);
    }
}
