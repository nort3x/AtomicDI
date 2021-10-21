package me.nort3x.atomic;

import me.nort3x.atomic.basic.AtomicModule;
import me.nort3x.atomic.core.container.Container;
import me.nort3x.atomic.core.internal.AtomicEnvironment;
import me.nort3x.atomic.core.internal.Resolver;
import me.nort3x.atomic.logger.AtomicLogger;
import me.nort3x.atomic.logger.enums.Priority;
import me.nort3x.atomic.wrappers.AtomicType;
import org.slf4j.Logger;

import java.io.*;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static me.nort3x.atomic.core.integrator.PredefinedLoader.addDefinitionFile;


public class AtomicDI {


    Logger logger;
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
        logger = AtomicLogger.getInstance().getLogger(getClass(),Priority.IMPORTANT);
        // assemble
        rs = new Resolver();
        atomicEnvironment = new AtomicEnvironment();
        try {
            InputStream innerInps = getClass().getClassLoader().getResourceAsStream("atomic.properties");
            if (innerInps != null) {
                addDefinitionFile(innerInps);
            }
            File pathFile = new File("atomic.properties");
            if (pathFile.exists() && pathFile.canRead())
                addDefinitionFile(new FileInputStream(pathFile));
        } catch (IOException e) {
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
                    AtomicModule am = (AtomicModule) Container.makeContainerAroundShared(module).getCentralShared();
                    modules.put(module, am);
                    logger.info("LoadedModule: " + am.getName() + " : " + am.getVersion());
                });
        modules.values().parallelStream().forEach(x -> {
            x.onLoad(atomicEnvironment, args);
            logger.info("Invoked scanFinished of: " + x.getName() + " : " + x.getVersion());
        });

        modules.values().parallelStream().forEach(x -> {
            tp.submit(() -> {
                logger.info("startingModule : " + x.getName() + " : " + x.getVersion());
                x.onStart(atomicEnvironment);
                logger.info("ModuleReturned : " + x.getName() + " : " + x.getVersion());
            });
        });
    }


    public void stopAllModules(){
        modules.values().forEach(this::stopModule);
    }
    public void stopModule(AtomicModule module){
        logger.info("Stopping Module : " + module.getName() + " : " + module.getVersion());
        module.onStop(atomicEnvironment);
        logger.info("Module Stopped : " + module.getName() + " : " + module.getVersion());
    }
    public Map<AtomicType, AtomicModule> getModules(){
        return modules;
    }


    public static void run(Class<?> entryPoint, String... args) {
        instance.resolve(entryPoint, args);
    }

}
