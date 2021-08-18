package me.nort3x.atomic.core;

import me.nort3x.atomic.basic.AtomicModule;
import me.nort3x.atomic.core.container.Container;
import me.nort3x.atomic.core.integrator.PredefinedLoader;
import me.nort3x.atomic.core.internal.GreedyBag;
import me.nort3x.atomic.core.internal.Resolver;
import me.nort3x.atomic.logger.AtomicLogger;
import me.nort3x.atomic.logger.Priority;
import me.nort3x.atomic.wrappers.AtomicType;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static me.nort3x.atomic.core.integrator.PredefinedLoader.addDefinitionFile;

public class AtomicDI {

    final Resolver rs;
    final GreedyBag gb;
    final AtomicEnvironment atomicEnvironment;
    public AtomicDI() {
        // assemble
        gb = new GreedyBag();
        rs = new Resolver(gb);
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

    private final static AtomicDI instance = new AtomicDI();

    public static AtomicDI getInstance() {
        return instance;
    }

    ConcurrentHashMap<AtomicType,AtomicModule> modules = new ConcurrentHashMap<>();
    ExecutorService tp = Executors.newCachedThreadPool();
    public void resolve(Class<?>... points) {
        rs.resolve(AtomicDI.class);
        Stream.of(points).parallel().forEach(rs::resolve);
        atomicEnvironment.getAllAtomicTypesDerivedFromAtomicType(AtomicType.of(AtomicModule.class)).parallelStream()
                .filter(x->!x.getCorrespondingType().equals(AtomicModule.class))
                .forEach(module->{
                    AtomicModule am = (AtomicModule) Container.makeContainerAround(module).getCentral();
                    modules.put(module,am);
                    AtomicLogger.getInstance().info("LoadedModule: "+am.getName()+" : "+am.getVersion(),Priority.IMPORTANT,AtomicDI.class);
                });
        modules.values().parallelStream().forEach(x->{
            x.whenScannedFinished(atomicEnvironment);
            AtomicLogger.getInstance().info("Invoked scanFinished of: "+x.getName()+" : "+x.getVersion(),Priority.IMPORTANT,AtomicDI.class);
        });

        modules.values().parallelStream().forEach(x->{
            tp.submit(()->{
                AtomicLogger.getInstance().info("startingModule : "+x.getName()+" : "+x.getVersion(),Priority.IMPORTANT,AtomicDI.class);
                x.whenModuleStarted(atomicEnvironment);
                AtomicLogger.getInstance().info("ModuleReturned : "+x.getName()+" : "+x.getVersion(),Priority.IMPORTANT,AtomicDI.class);
                x.whenModuleStopped(atomicEnvironment);
            });
        });


    }

    public GreedyBag getGreedyBag() {
        return gb;
    }
}
