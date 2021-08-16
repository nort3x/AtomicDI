package me.nort3x.atomic.processor;

import me.nort3x.atomic.annotation.Atomic;
import me.nort3x.atomic.logger.AtomicLogger;
import me.nort3x.atomic.logger.Priority;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.lang.reflect.Constructor;
import java.util.Set;

public class AtomicAnnotationProcessor extends AbstractProcessor {
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        System.out.println("i've called");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> annotatedElements
                = roundEnv.getElementsAnnotatedWith(Atomic.class);

        annotatedElements.stream().forEach(item -> {

            try {
                if (!item.getKind().getDeclaringClass().isAnnotationPresent(Atomic.class))
                    return;
                Constructor c = item.getKind().getDeclaringClass().getConstructor();
            } catch (NoSuchMethodException e) {
                AtomicLogger.getInstance().fatal("AtomicType does not provide NoArgsConstructor :" + item.getKind().getDeclaringClass().getName(), Priority.VERY_IMPORTANT, AtomicAnnotationProcessor.class);
            }

        });
        return false;
    }
}
