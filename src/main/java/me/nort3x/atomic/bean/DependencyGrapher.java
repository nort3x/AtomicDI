package me.nort3x.atomic.bean;

import me.nort3x.atomic.annotation.Atom;
import me.nort3x.atomic.annotation.Atomic;
import me.nort3x.atomic.annotation.Exclude;
import me.nort3x.atomic.annotation.PostConstruction;
import me.nort3x.atomic.logger.AtomicLogger;
import me.nort3x.atomic.reactor.Factory;
import me.nort3x.atomic.reactor.ParallelReactor;
import org.reflections8.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * <H2>DependencyGrapher</H2>
 * this class will organize module scanning system
 * following life cycle happens after calling {@link DependencyGrapher#run(String...)} ()} ()}
 * <ul>
 *
 *         <li>Grab all {@link Atomic}s</li>
 *         <li>Check/Generate all {@link Atomic}s no-args-constructors</li>
 *         <li>Wire dependencies({@link Atom}s) and generate factories</li>
 *         <li>Apply provided Custom-Polices (see {@link AtomicDISubModule})</li>
 *
 * </ul>
 * <p>
 * subclasses should be only accessible via singleton as well
 */
public final class DependencyGrapher {


    // restricted API to DependencyGrapher
    private final Provider provider;
    private final SafeConstructor safeConstructor;

    // singleton pattern
    protected DependencyGrapher() {
        provider = new Provider(this);
        safeConstructor = new SafeConstructor();
    }

    private static DependencyGrapher instance;

    public static DependencyGrapher getInstance() {
        if (instance == null)
            instance = new DependencyGrapher();
        return instance;
    }


    // add each module for scanning
    @SafeVarargs
    public final void addModules(AtomicDIModule... atomicDIModules) {
        Arrays.stream(atomicDIModules).forEach(x -> {
            module_instances.putIfAbsent(x.getClass(), x);
        });
    }

    // main method user should call
    public void run(String... args) {
        provider.setArgs(args);
        module_instances.forEach((clazz, module) -> {
            AtomicLogger.getInstance().info("[Grapher] Scanning : " + module.provideModuleName() + ":" + module.provideModuleVersion());
            module.onPreLoad(args);
            Collection<Class<?>> atomics = new Reflections(module.provideModulePackagePath()).getTypesAnnotatedWith(Atomic.class).stream().filter(x -> !x.isAnnotationPresent(Exclude.class)).collect(Collectors.toList());
            ReflectionUtils.loadAllLoadedAtomic(atomics, clazz);
            // get All Constructors of Atomics
            makeConstructors(atomics);
            makeFactories(atomics);
            makeRules(clazz);
            module.onPostLoad(args);
        });
        AtomicLogger.getInstance().info("[Grapher] Invoking AtomicDISubModules WarmUp");
        allSubModulesWarmUps.actOn(provider);

        AtomicLogger.getInstance().info("[Grapher] Invoking AtomicDISubModules afterLoads");
        allSubModulesAfterLoad.actOn(null);

        AtomicLogger.getInstance().info("[Grapher] Invoking AtomicDIModules afterLoads");
        module_instances.values().forEach(x -> {

            AtomicLogger.getInstance().info("[Grapher] Invoking Module " + x.provideModuleName() + ":" + x.provideModuleVersion() + " afterLoad");
            x.afterLoadInvoke(args);
        });

    }

    // main method user should call
    public void run(Class<?> point, String... args) {
        addModules(new AtomicDIModule() {
            @Override
            protected String provideModuleName() {
                return "Default EntryPoint: " + point.getSimpleName();
            }

            @Override
            protected int provideModuleVersion() {
                return -1;
            }

            @Override
            protected void onPreLoad(String... args) {
            }

            @Override
            protected void onPostLoad(String... args) {
            }

            @Override
            protected void afterLoadInvoke(String... args) {

            }

            @Override
            protected String provideModulePackagePath() {
                return point.getPackage().getName();
            }
        });
        run(args);
    }

    // generate Constructors for factories
    private void makeConstructors(Collection<Class<?>> clazzes) {
        safeConstructor.putNewClasses(clazzes);
    }

    // generate Factories and WireUp
    private void makeFactories(Collection<Class<?>> atomics) {
        atomics.parallelStream().forEach(x -> {
            Optional<Constructor<?>> op = safeConstructor.getConstructor(x);
            if (op.isPresent()) {
                // generate factory
                Factory<?> f = new Factory<>(op.get());

                // wire fields
                getListOfRequestedFields(x).forEach(field -> f.addReaction(o -> provideRequestedFieldFor(o, field)));
                ReflectionUtils.getMethodsAnnotatedWith(x, PostConstruction.class).stream().findFirst().ifPresent(postConstructor -> f.addPostReaction(obj -> {
                    try {
                        postConstructor.invoke(obj);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        AtomicLogger.getInstance().complain_PostConfigurationMethodHasParameter(x, postConstructor);
                    }
                }));
                factories.putIfAbsent(x, f);
            } else
                AtomicLogger.getInstance().complain_AtomicNotFound(x);
        });
    }

    // parse rules and ready them up for execution
    private void makeRules(Class<? extends AtomicDIModule> moduleClazz) {
        List<AtomicDISubModule> subModules = ReflectionUtils.getAllAtomicInModuleDerivedFrom(AtomicDISubModule.class, moduleClazz).stream().map(x -> {
            try {
                return (AtomicDISubModule) x.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                AtomicLogger.getInstance().complain_NorArgConstructor(x);
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
        allSubModulesWarmUps.addReactions(subModules.stream().map(x -> (Consumer<Provider>) provider -> {
            AtomicLogger.getInstance().info("[Grapher] WarmingUp SubModule " + x.getIdentifier());
            x.accept(provider);
        }).collect(Collectors.toList()));

        allSubModulesAfterLoad.addReactions(subModules.stream().map(x -> (Consumer<Void>) provider -> {
            AtomicLogger.getInstance().info("[Grapher] Invoking Submodule " + x.getIdentifier() + " afterLoad");
            x.whenAllSubModulesLoaded();
        }).collect(Collectors.toList()));

    }

    // return list of Atom fields of given Atomic only used in makeFactories
    private List<Field> getListOfRequestedFields(Class<?> clazz) {
        return ReflectionUtils.getAllFieldsFor(clazz).stream().filter(x -> x.isAnnotationPresent(Atom.class)).collect(Collectors.toList());
    }


    private void provideRequestedFieldFor(Object forWhom, Field fromWhere) {

        Class<?> typeOfField = atomFieldConcreteType.computeIfAbsent(fromWhere, field -> {  // compute and cache type of field
            if (field.getAnnotation(Atom.class).concreteType() != Object.class) {
                Class<?> cc = field.getAnnotation(Atom.class).concreteType();
                if (!field.getType().isAssignableFrom(cc))
                    AtomicLogger.getInstance().error_unCastableAtom(field, cc);
                return cc;
            } else
                return field.getType();
        });

        Optional<Object> requiredInstance; // below switch will fill this instance according to type

        switch (atomFieldsType.computeIfAbsent(fromWhere, x -> x.getAnnotation(Atom.class).type())) { // compute and cache Type of Field

            case Shared: // shared act like singleton so we cache if we made one
                Object resolvedInstance = instancesOfSharedFields.computeIfAbsent(fromWhere, x -> {
                    try {
                        Optional<Object> instance = safeConstructor.getNewInstance(typeOfField);
                        if (!instance.isPresent()) { // case Atomic not found meaning user request a non atomic type
                            AtomicLogger.getInstance().complain_AtomicNotFound(typeOfField);
                            return null;
                        } else
                            return instance.get(); // when everything goes fine
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) { // when type is actually atomic but no-args-const doesnt exist
                        AtomicLogger.getInstance().complain_NorArgConstructor(typeOfField);
                        return null;
                    }
                });
                requiredInstance = resolvedInstance == null ? Optional.empty() : Optional.of(resolvedInstance); // passing out the required instance
                break;
            case Unique:
                try {
                    requiredInstance = safeConstructor.getNewInstance(typeOfField);
                    if (!requiredInstance.isPresent()) { // case Atomic not found meaning user request a non atomic type
                        AtomicLogger.getInstance().complain_AtomicNotFound(typeOfField);
                    }
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    requiredInstance = Optional.empty();
                    AtomicLogger.getInstance().complain_NorArgConstructor(typeOfField);
                }
                break;
            default:
                requiredInstance = Optional.empty();
                break;
        }

        try {
            if (requiredInstance.isPresent()) {
                fromWhere.set(forWhom, requiredInstance.get());
            }
        } catch (IllegalAccessException e) {
            AtomicLogger.getInstance().facingABug(fromWhere.getDeclaringClass(), e);
        }

    }


    // return generator of given Atomic
    protected Factory<?> getFactoryOf(Class<?> clazz) {
        return factories.get(clazz);
    }


    // for test only! you should not access Provider Directly!
    public Provider getProvider() {
        return provider;
    }


    // bunch of maps for caching and cutting out jvm lookups , these are references
    private final ConcurrentHashMap<Class<? extends AtomicDIModule>, AtomicDIModule> module_instances = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Field, Atom.Type> atomFieldsType = new ConcurrentHashMap<>(); // caching defined type of atom {Shared,Unique,...} avoiding getAnnotation lookup
    private final ConcurrentHashMap<Field, Class<?>> atomFieldConcreteType = new ConcurrentHashMap<>(); // caching getType lookup
    private final ConcurrentHashMap<Class<?>, Factory<?>> factories = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Field, Object> instancesOfSharedFields = new ConcurrentHashMap<>();

    // rules will applied parallelized after initialization and scan
    private final ParallelReactor<Provider> allSubModulesWarmUps = new ParallelReactor<>();
    private final ParallelReactor<Void> allSubModulesAfterLoad = new ParallelReactor<>();

}
