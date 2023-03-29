package org.peter.basic;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

//http://blog.kail.xyz/post/2018-12-01/java/JSR269-Pluggable-Annotation-Processing-API.html
@SupportedAnnotationTypes({"main.basic.PrintHello"})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class HelloAnnotationProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        System.out.println("Hello World");

        annotations.forEach(typeElement -> {
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(typeElement);
            elements.forEach(element -> {
                System.out.print(element.getEnclosingElement());
                System.out.print(".");
                System.out.println(element.getSimpleName());
            });
        });

        return true;
    }
}
