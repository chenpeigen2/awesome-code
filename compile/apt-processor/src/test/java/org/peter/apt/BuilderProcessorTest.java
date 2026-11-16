package org.peter.apt;

import org.junit.jupiter.api.Test;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link BuilderProcessor} 的规格。
 * <p>
 * 注解处理器的「公共接缝」就是 javac 本身：喂源码进去，看它吐出来什么源码 / 什么报错。
 * 所以这里不用 mock，直接跑一个真实的编译任务，把处理器挂上去。
 */
class BuilderProcessorTest {

    @Test
    void generatesBuilderForEveryAnnotatedSetter() throws IOException {
        CompilationResult result = compile(Map.of(
                "demo/Person.java", """
                        package demo;

                        public class Person {
                            private String name;
                            private int age;

                            @org.peter.apt.Builder
                            public void setName(String name) { this.name = name; }

                            @org.peter.apt.Builder
                            public void setAge(int age) { this.age = age; }
                        }
                        """));

        assertTrue(result.success(), result.diagnostics());

        String builder = result.generatedSource("demo.PersonBuilder");
        assertTrue(builder.contains("public class PersonBuilder {"), builder);
        // TypeMirror.toString() 给的是全限定名：引用类型是 java.lang.String，基本类型是 int
        assertTrue(builder.contains("public PersonBuilder setName(java.lang.String value) {"), builder);
        assertTrue(builder.contains("public PersonBuilder setAge(int value) {"), builder);
        assertTrue(builder.contains("object.setName(value);"), builder);
        assertTrue(builder.contains("return this;"), builder);
        assertTrue(builder.contains("public Person build() {"), builder);
        assertTrue(builder.contains("return object;"), builder);
    }

    @Test
    void generatedBuilderIsUsableFromTheSameCompilation() throws IOException {
        // 关键验收：处理器生成的类，必须能在「同一次编译」里被别的源文件引用
        CompilationResult result = compile(Map.of(
                "demo/Person.java", """
                        package demo;

                        public class Person {
                            private String name;

                            @org.peter.apt.Builder
                            public void setName(String name) { this.name = name; }

                            public String getName() { return name; }
                        }
                        """,
                "demo/UsesBuilder.java", """
                        package demo;

                        public class UsesBuilder {
                            public static Person build() {
                                return new PersonBuilder().setName("doge").build();
                            }
                        }
                        """));

        assertTrue(result.success(), result.diagnostics());
        assertTrue(result.generatedSource("demo.PersonBuilder")
                .contains("public PersonBuilder setName(java.lang.String value)"));
    }

    @Test
    void reportsErrorWhenAnnotatedMethodIsNotASetter() throws IOException {
        CompilationResult result = compile(Map.of(
                "demo/Bad.java", """
                        package demo;

                        public class Bad {
                            @org.peter.apt.Builder
                            public void notASetter(String a, String b) { }
                        }
                        """));

        assertFalse(result.success(), "编译应该失败");
        assertTrue(result.diagnostics().contains("must be applied to a setXxx method"),
                result.diagnostics());
    }

    @Test
    void reportsErrorWhenSetterHasMoreThanOneParameter() throws IOException {
        CompilationResult result = compile(Map.of(
                "demo/Bad.java", """
                        package demo;

                        public class Bad {
                            @org.peter.apt.Builder
                            public void setName(String a, String b) { }
                        }
                        """));

        assertFalse(result.success(), "编译应该失败");
        assertTrue(result.diagnostics().contains("single argument"), result.diagnostics());
    }

    @Test
    void producesNoBuilderWhenNothingIsAnnotated() throws IOException {
        CompilationResult result = compile(Map.of(
                "demo/Plain.java", """
                        package demo;

                        public class Plain {
                            public void setName(String name) { }
                        }
                        """));

        assertTrue(result.success(), result.diagnostics());
        assertTrue(result.generatedSources().isEmpty(),
                "不应生成任何文件，实际: " + result.generatedSources().keySet());
    }

    @Test
    void handlesClassInDefaultPackage() throws IOException {
        CompilationResult result = compile(Map.of(
                "Root.java", """
                        public class Root {
                            private String id;

                            @org.peter.apt.Builder
                            public void setId(String id) { this.id = id; }
                        }
                        """));

        assertTrue(result.success(), result.diagnostics());
        String builder = result.generatedSource("RootBuilder");
        assertFalse(builder.contains("package "), builder);
        assertTrue(builder.contains("public class RootBuilder {"), builder);
    }

    @Test
    void rejectsAnnotationOnAField() throws IOException {
        CompilationResult result = compile(Map.of(
                "demo/WrongTarget.java", """
                        package demo;

                        public class WrongTarget {
                            @org.peter.apt.Builder
                            private String name;
                        }
                        """));

        // @Target(METHOD) 由 javac 在进入处理器之前就拦下了，
        // 报错是 JDK 自带的 "annotation interface not applicable to this kind of declaration"，
        // 而不是处理器的自定义消息——所以这里断言编译失败 + 指向正确行号
        assertFalse(result.success(), "编译应该失败");
        assertTrue(result.diagnostics().contains("not applicable to this kind of declaration"),
                result.diagnostics());
        assertTrue(result.diagnostics().contains("WrongTarget.java:4"), result.diagnostics());
    }

    @Test
    void ignoresStaticSetters() throws IOException {
        CompilationResult result = compile(Map.of(
                "demo/Statics.java", """
                        package demo;

                        public class Statics {
                            @org.peter.apt.Builder
                            public static void setName(String name) { }
                        }
                        """));

        assertFalse(result.success(), "static setter 应被判为非法");
        assertTrue(result.diagnostics().contains("must be applied to a setXxx method"),
                result.diagnostics());
    }

    // ------------------------------------------------------------------
    // 用真实的 javac 跑一遍，收集生成的文件与诊断
    // ------------------------------------------------------------------

    private record CompilationResult(boolean success, String diagnostics, Map<String, String> generatedSources) {

        String generatedSource(String binaryName) {
            String source = generatedSources.get(binaryName);
            assertTrue(source != null, "没有生成 " + binaryName + "，实际生成: " + generatedSources.keySet());
            return source;
        }
    }

    private static CompilationResult compile(Map<String, String> sources) throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager standard = compiler.getStandardFileManager(null, null, null);
        CollectingFileManager fileManager = new CollectingFileManager(standard);

        List<JavaFileObject> units = new ArrayList<>();
        sources.forEach((path, source) -> units.add(new StringSource(path, source)));

        // 用 DiagnosticCollector 而不是靠 stdout：编译器报错有时不走 writer，
        // 只抓 writer 会在失败时得到空字符串，完全没法调试
        javax.tools.DiagnosticCollector<JavaFileObject> diagnostics =
                new javax.tools.DiagnosticCollector<>();
        JavaCompiler.CompilationTask task = compiler.getTask(
                null,
                fileManager,
                diagnostics,
                List.of("-proc:full", "-classpath", System.getProperty("java.class.path")),
                null,
                units);
        task.setProcessors(List.of(new BuilderProcessor()));

        boolean success = task.call();
        fileManager.close();
        return new CompilationResult(success, describe(diagnostics), fileManager.generatedSources());
    }

    private static String describe(javax.tools.DiagnosticCollector<JavaFileObject> diagnostics) {
        StringBuilder out = new StringBuilder();
        for (javax.tools.Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
            out.append(diagnostic.getKind())
                    .append(" at ")
                    .append(diagnostic.getSource() == null ? "<unknown>" : diagnostic.getSource().getName())
                    .append(':').append(diagnostic.getLineNumber())
                    .append(": ").append(diagnostic.getMessage(java.util.Locale.ROOT))
                    .append('\n');
        }
        return out.toString();
    }

    /** 内存中的 .java 源文件。 */
    private static final class StringSource extends javax.tools.SimpleJavaFileObject {

        private final String code;

        private StringSource(String path, String code) {
            super(URI.create("string:///" + path), Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }

    /** 把处理器生成的源码收进内存，而不是写到磁盘。 */
    private static final class CollectingFileManager
            extends javax.tools.ForwardingJavaFileManager<StandardJavaFileManager> {

        private final Map<String, StringBuilder> sources = new java.util.LinkedHashMap<>();

        private CollectingFileManager(StandardJavaFileManager delegate) {
            super(delegate);
        }

        @Override
        public JavaFileObject getJavaFileForOutput(
                Location location, String className, JavaFileObject.Kind kind, javax.tools.FileObject sibling)
                throws IOException {
            // 所有输出都收进内存：如果 .class 交给父类，javac 会写到进程的工作目录，
            // 测试就会在源码树里留下垃圾 .class 文件
            StringBuilder buffer = new StringBuilder();
            if (kind == JavaFileObject.Kind.SOURCE) {
                sources.put(className, buffer);
            }
            return new javax.tools.SimpleJavaFileObject(
                    URI.create("mem:///" + className.replace('.', '/') + kind.extension), kind) {
                @Override
                public OutputStream openOutputStream() {
                    return new OutputStream() {
                        @Override
                        public void write(int b) {
                            buffer.append((char) b);
                        }
                    };
                }

                // 关键：生成的源码会在下一轮被 javac 读回来做类型检查，
                // 不实现这个方法就会抛 UnsupportedOperationException
                @Override
                public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                    return buffer.toString();
                }
            };
        }

        private Map<String, String> generatedSources() {
            Map<String, String> result = new java.util.LinkedHashMap<>();
            sources.forEach((name, buffer) -> result.put(name, buffer.toString()));
            return result;
        }
    }
}
