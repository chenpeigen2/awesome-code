package org.peter.apt;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * 为被 {@link Builder} 标记的 setter 所在类生成一个流式 Builder。
 * <p>
 * 对 {@code Person} 生成：
 * <pre>{@code
 * public class PersonBuilder {
 *     private final Person object = new Person();
 *     public PersonBuilder setName(String value) { object.setName(value); return this; }
 *     public Person build() { return object; }
 * }
 * }</pre>
 *
 * <h2>为什么用 {@code getSupportedAnnotationTypes()} 而不是注解</h2>
 * 用 {@code @SupportedAnnotationTypes} 也行，但显式覆写方法能让
 * 「支持哪个注解、支持到哪个 Java 版本」在类型层面可读，
 * 也方便子类改写。这里用覆写方式。
 *
 * <h2>处理器必须遵守的几条规矩</h2>
 * <ul>
 *   <li><b>不要吞掉异常</b>：处理器里抛异常会让 javac 打出一堆无意义的堆栈。
 *       遇到非法输入应该用 {@link Messager#printMessage} 报 error，让编译干净地失败。</li>
 *   <li><b>不要返回 {@code true} 却什么都不做</b>：返回 true 表示「这个注解我处理了，
 *       别给别人」。若同一注解有多个处理器，会互相抢。</li>
 *   <li><b>按元素类型校验</b>：@code @Target} 只约束「能写在哪儿」，
 *       但同一个注解可能通过别的方式出现在不合法位置，处理器自己要再校验一遍。</li>
 * </ul>
 */
public final class BuilderProcessor extends AbstractProcessor {

    private Messager messager;
    private Filer filer;
    private Elements elements;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
        this.filer = processingEnv.getFiler();
        this.elements = processingEnv.getElementUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(Builder.class.getName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        // 跟随 javac 当前版本，避免在更新的 JDK 上打印 "source version 8 is obsolete" 警告
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        TypeElement builderAnnotation = elements.getTypeElement(Builder.class.getName());
        if (builderAnnotation == null) {
            return false; // 本轮没有这个注解，交给别人
        }

        // 一个类可能有多个 setter，先按「所属类」分组
        Map<TypeElement, Map<String, TypeMirror>> settersByClass = new LinkedHashMap<>();

        for (Element element : roundEnv.getElementsAnnotatedWith(builderAnnotation)) {
            if (element.getKind() != ElementKind.METHOD) {
                error(element, "@Builder only applies to methods, but found it on a "
                        + element.getKind().toString().toLowerCase(java.util.Locale.ROOT));
                continue;
            }

            ExecutableElement method = (ExecutableElement) element;
            if (!isValidSetter(method)) {
                error(element, "@Builder must be applied to a setXxx method with a single argument");
                continue;
            }

            TypeElement owner = (TypeElement) method.getEnclosingElement();
            TypeMirror parameterType = ((ExecutableType) method.asType()).getParameterTypes().get(0);
            settersByClass.computeIfAbsent(owner, k -> new LinkedHashMap<>())
                    .put(method.getSimpleName().toString(), parameterType);
        }

        settersByClass.forEach(this::writeBuilder);

        // 返回 false：不独占该注解，允许其它处理器也看到它
        return false;
    }

    /** setter 的判定：名字以 {@code set} 开头、恰好一个参数、非 static。 */
    private static boolean isValidSetter(ExecutableElement method) {
        return method.getSimpleName().toString().startsWith("set")
                && method.getParameters().size() == 1
                && !method.getModifiers().contains(Modifier.STATIC);
    }

    private void writeBuilder(TypeElement owner, Map<String, TypeMirror> setters) {
        String packageName = elements.getPackageOf(owner).getQualifiedName().toString();
        String ownerSimpleName = owner.getSimpleName().toString();
        String builderSimpleName = ownerSimpleName + "Builder";
        String builderQualifiedName = packageName.isEmpty()
                ? builderSimpleName
                : packageName + "." + builderSimpleName;

        // 用 TreeMap 保证生成顺序稳定：同样的输入永远产出同样的文件，
        // 否则每次编译的 diff 都不一样，也没法写稳定的测试
        Map<String, TypeMirror> sortedSetters = new TreeMap<>(setters);

        try {
            JavaFileObject file = filer.createSourceFile(builderQualifiedName, owner);
            try (PrintWriter out = new PrintWriter(file.openWriter())) {
                writeSource(out, packageName, ownerSimpleName, builderSimpleName, sortedSetters);
            }
        } catch (IOException e) {
            error(owner, "failed to write " + builderQualifiedName + ": " + e.getMessage());
        }
    }

    private static void writeSource(
            PrintWriter out,
            String packageName,
            String ownerSimpleName,
            String builderSimpleName,
            Map<String, TypeMirror> setters) {
        if (!packageName.isEmpty()) {
            out.println("package " + packageName + ";");
            out.println();
        }

        out.println("public class " + builderSimpleName + " {");
        out.println();
        out.println("    private final " + ownerSimpleName + " object = new " + ownerSimpleName + "();");
        out.println();
        out.println("    public " + ownerSimpleName + " build() {");
        out.println("        return object;");
        out.println("    }");

        setters.forEach((methodName, parameterType) -> {
            out.println();
            out.println("    public " + builderSimpleName + " " + methodName + "(" + parameterType + " value) {");
            out.println("        object." + methodName + "(value);");
            out.println("        return this;");
            out.println("    }");
        });

        out.println("}");
    }

    private void error(Element element, String message) {
        messager.printMessage(Diagnostic.Kind.ERROR, message, element);
    }
}
