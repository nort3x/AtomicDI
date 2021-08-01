package me.nort3x.atomic.bean;

import me.nort3x.atomic.annotation.Atom;
import me.nort3x.atomic.annotation.Atomic;
import me.nort3x.atomic.annotation.PostConstruction;

import me.nort3x.atomic.reactor.Factory;
import me.nort3x.atomic.reactor.ParallelReactor;
import org.reflections8.Reflections;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DependencyGrapher {

    private  final ConcurrentHashMap<Class<?>, Constructor<?>> allConstructors = new ConcurrentHashMap<>();
    private  final ConcurrentHashMap<Field, Object> instancesOfSharedFields = new ConcurrentHashMap<>();
    private  final ConcurrentHashMap<Class<?>, Factory<?>> factories = new ConcurrentHashMap<>();
    private  final ConcurrentHashMap<Field, Atom.Type> atomFieldsType = new ConcurrentHashMap<>();
    private  final ParallelReactor<DependencyGrapher> rules = new ParallelReactor<>();


    private static DependencyGrapher instance;
    public static DependencyGrapher getInstance(){
        if(instance ==null)
            instance = new DependencyGrapher();
        return instance;
    }

    private DependencyGrapher(){}



    public  void graphUsingThisEntryPoint(Class<?> point) {

        // get All Atomics
        Collection<Class<?>> atomics = new Reflections(point).getTypesAnnotatedWith(Atomic.class);

        ReflectionUtils.loadAllLoadedAtomic(atomics);
        // get All Constructors of Atomics
        makeConstructors(atomics);
        makeFactories(atomics);
        makeRules();
        rules.actOn(this);
    }

    public  Factory<?> getFactoryOf(Class<?> clazz){
        return factories.get(clazz);
    }


    public  void makeRules(){
        rules.addReactions(ReflectionUtils.getAllAtomicDerivedFrom(Policy.class).stream().map(x-> {
            try {
                return (Policy) x.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                scream(x);
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList()));
    }

    private  void makeConstructors(Collection<Class<?>> clazzes) {
        clazzes.parallelStream().forEach(x -> {
            Optional<Constructor<?>> con = ReflectionUtils.getNoArgsConstructor(x);
            con.ifPresentOrElse(
                    xx -> allConstructors.putIfAbsent(x, xx),
                    () -> LoggerFactory.getLogger(DependencyGrapher.class).warn("Atomic types should contain No-Args-Construct but " + x.getName() + " does not!, this can lead to Undefined Behavior"));
        });
    }

    private  void makeFactories(Collection<Class<?>> atomics) {
        atomics.parallelStream().forEach(x -> {
            // generate factory
            Factory<?> f = new Factory<>(allConstructors.get(x));

            // wire fields
            getListOfRequestedFields(x).forEach(field -> f.addReaction(o -> provideRequestedFieldFor(o, field)));
            ReflectionUtils.getMethodsAnnotatedWith(x, PostConstruction.class).stream().findFirst().ifPresent(postConstructor-> f.addPostReaction(obj->{
                try {
                    postConstructor.invoke(obj);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    LoggerFactory.getLogger(DependencyGrapher.class).warn("PostConstruction method should be No-Args-method but " + postConstructor.getName() + " is not!, this can lead to Undefined Behavior");
                }
            }));
            factories.putIfAbsent(x,f);
        });
    }


    private  void provideRequestedFieldFor(Object forWhom, Field fromWhere) {
        try {
            Optional<Object> o;
            switch(atomFieldsType.computeIfAbsent(fromWhere,x->x.getAnnotation(Atom.class).type())){
                case Shared:
                   o = Optional.ofNullable(instancesOfSharedFields.computeIfAbsent(fromWhere, x -> {
                       try {
                           Class<?> s = x.getType();
                           return allConstructors.get(s).newInstance();
                       } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                           scream(x.getClass());
                           return null;
                       }
                   }));
                case Unique :
                    try {
                        o = Optional.of(allConstructors.get(fromWhere.getType()).newInstance());
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        o = Optional.empty();
                        e.printStackTrace();
                        scream(allConstructors.get(fromWhere.getType()).getDeclaringClass());
                    }
                    break;
                default:
                    o = Optional.empty();
                }

            if(o.isPresent())
                fromWhere.set(forWhom,o.get());

        } catch (IllegalAccessException e) {
            LoggerFactory.getLogger(DependencyGrapher.class).error("you are facing a bug please report this at: https://github.com/nort3x/AtomicDI ", e);
        }
}

    private  List<Field> getListOfRequestedFields(Class<?> clazz) {
        return ReflectionUtils.getAllFieldsFor(clazz).stream().filter(x -> x.isAnnotationPresent(Atom.class)).collect(Collectors.toList());
    }

    private  void scream(Class<?> x){
        LoggerFactory.getLogger(DependencyGrapher.class).warn("Atomic types should contain No-Args-Construct but " + x.getName() + " does not!, this can lead to Undefined Behavior");
    }



    public  List<Class<?>> getAllAtomicAnnotatedWith(Class<? extends Annotation> annotation) {
        return ReflectionUtils.getAllAtomicAnnotatedWith(annotation);
    }


    public  List<Class<?>> getAllAtomicDerivedFrom(Class<?> clazz) {
        return ReflectionUtils.getAllAtomicDerivedFrom(clazz);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getAllAtomicInstancesDerivedFrom(Class<T> clazz) {
        return getAllAtomicDerivedFrom(clazz).stream()
                .map(x->getFactoryOf(x).generate())
                .flatMap(Optional::stream)
                .map(x->(T) x)
                .collect(Collectors.toList());
    }


}
