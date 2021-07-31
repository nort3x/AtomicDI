package me.neiizun.lightdrop.atomic.bean;

import me.neiizun.lightdrop.atomic.anonation.Atom;
import me.neiizun.lightdrop.atomic.anonation.Atomic;
import me.neiizun.lightdrop.atomic.anonation.PostConstruction;

import me.neiizun.lightdrop.atomic.reactor.Factory;
import org.reflections8.Reflections;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class DependencyGrapher {

    private static final ConcurrentHashMap<Class<?>, Constructor<?>> allConstructors = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Field, Object> instancesOfSharedFields = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Class<?>, Factory<?>> factories = new ConcurrentHashMap<>();
    private static final ConcurrentLinkedQueue<Class<?>> allLoadedAtomic = new ConcurrentLinkedQueue<>();
    private static final ConcurrentHashMap<Field, Atom.Type> atomFieldsType = new ConcurrentHashMap<>();

    public static void graphUsingThisEntryPoint(Class<?> point) {

        // get All Atomics
        Collection<Class<?>> atomics = new Reflections(point).getTypesAnnotatedWith(Atomic.class);
        allLoadedAtomic.addAll(atomics);
        // get All Constructors of Atomics
        makeConstructors(atomics);
        makeFactories(atomics);
    }

    public static Factory<?> getFactoryOf(Class<?> clazz){
        return factories.get(clazz);
    }


    private static void makeConstructors(Collection<Class<?>> clazzes) {
        clazzes.parallelStream().forEach(x -> {
            var con = ReflectionUtils.getNoArgsConstructor(x);
            con.ifPresentOrElse(
                    xx -> allConstructors.putIfAbsent(x, xx),
                    () -> LoggerFactory.getLogger(DependencyGrapher.class).warn("Atomic types should contain No-Args-Construct but " + x.getName() + " does not!, this can lead to Undefined Behavior"));
        });
    }

    private static void makeFactories(Collection<Class<?>> atomics) {
        atomics.parallelStream().forEach(x -> {
            // generate factory
            Factory<?> f = new Factory<>(allConstructors.get(x));

            // wire fields
            getListOfRequestedFields(x).forEach(field -> {
                f.addReaction(o -> {
                    provideRequestedFieldFor(o, field);
                });
            });
            ReflectionUtils.getMethodsAnnotatedWith(x,PostConstruction.class).stream().findFirst().ifPresent(postConstructor->{
                f.addPostReaction(obj->{
                    try {
                        postConstructor.invoke(obj);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        LoggerFactory.getLogger(DependencyGrapher.class).warn("PostConstruction method should be No-Args-method but " + postConstructor.getName() + " is not!, this can lead to Undefined Behavior");
                    }
                });
            });
            factories.putIfAbsent(x,f);
        });
    }


    private static void provideRequestedFieldFor(Object forWhom, Field fromWhere) {
        try {
            fromWhere.set(forWhom,switch(atomFieldsType.computeIfAbsent(fromWhere,x->x.getAnnotation(Atom.class).type())){
                case Shared -> instancesOfSharedFields.computeIfAbsent(fromWhere, x->{
                    try {
                        Class<?> s = x.getType();
                        return  allConstructors.get(s).newInstance();
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        scream(x.getClass());
                        return   null;
                    }
                });
                case Unique -> {
                    try {
                        yield allConstructors.get(fromWhere.getType()).newInstance();
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                        yield null;
                    }
                }
            });
        } catch (IllegalAccessException e) {
            LoggerFactory.getLogger(DependencyGrapher.class).error("you are facing a bug please report this at: https://github.com/nort3x/LighDropAtomic ", e);
        }
    }

    private static List<Field> getListOfRequestedFields(Class<?> clazz) {
        return ReflectionUtils.getAllFieldsFor(clazz).stream().filter(x -> x.isAnnotationPresent(Atom.class)).collect(Collectors.toList());
    }

    protected static void scream(Class<?> x){
        LoggerFactory.getLogger(DependencyGrapher.class).warn("Atomic types should contain No-Args-Construct but " + x.getName() + " does not!, this can lead to Undefined Behavior");
    }
}
