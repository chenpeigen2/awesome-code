package org.peter.inmemory;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 用 {@link JavaCompiler} 在内存里编译 Java 源码，不落盘。
 * <p>
 * 三个角色：
 * <ul>
 *   <li>{@link StringSource} —— 把一段源码字符串伪装成一个 {@code .java} 文件；</li>
 *   <li>{@link ByteArrayOutput} —— 把 javac 写出的 {@code .class} 收进内存；</li>
 *   <li>{@link InMemoryFileManager} —— 把上面两者接进 javac 的文件系统。</li>
 * </ul>
 *
 * 与 ASM 的对照：ASM 从指令层直接生成 {@code byte[]}，不经过 javac；
 * 这里让 javac 帮我们做语法检查、类型检查，代价是必须先生成源码文本。
 */
public final class InMemoryCompiler {

    private InMemoryCompiler() {
    }

    /** 编译失败时抛出，message 里带 javac 的完整诊断。 */
    public static final class CompilationException extends RuntimeException {
        public CompilationException(String message) {
            super(message);
        }
    }

    /**
     * 编译一个编译单元，返回 {@code 类名 → 字节码}。
     * <p>
     * 内部类会被一起返回（key 形如 {@code demo.Outer$Inner}），
     * 因为它们在字节码层面是独立的类。
     *
     * @param binaryName 顶层类的全限定名，例如 {@code demo.Greeting}
     * @param source     完整源码（含 package 声明）
     */
    public static Map<String, byte[]> compile(String binaryName, String source) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("no system Java compiler; a JDK (not a JRE) is required");
        }

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        StandardJavaFileManager standard = compiler.getStandardFileManager(diagnostics, null, null);

        try (InMemoryFileManager fileManager = new InMemoryFileManager(standard)) {
            JavaFileObject unit = new StringSource(binaryName, source);
            JavaCompiler.CompilationTask task = compiler.getTask(
                    null, fileManager, diagnostics, List.of("--release", "25"), null, List.of(unit));

            if (!task.call()) {
                throw new CompilationException(describe(diagnostics.getDiagnostics()));
            }
            return fileManager.classBytes();
        } catch (IOException e) {
            throw new CompilationException("failed to close file manager: " + e.getMessage());
        }
    }

    /** 编译 → 定义成类 → 反射调用无参构造器。 */
    public static Object newInstance(Map<String, byte[]> classBytes, String binaryName) throws Exception {
        ClassLoader loader = new MemoryClassLoader(classBytes);
        return loader.loadClass(binaryName).getDeclaredConstructor().newInstance();
    }

    /** 把编译好的字节码定义成类。 */
    public static ClassLoader classLoader(Map<String, byte[]> classBytes) {
        return new MemoryClassLoader(classBytes);
    }

    private static String describe(List<Diagnostic<? extends JavaFileObject>> diagnostics) {
        StringBuilder out = new StringBuilder("compilation failed:");
        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics) {
            JavaFileObject source = diagnostic.getSource();
            out.append('\n')
                    .append(diagnostic.getKind())
                    .append(" in ")
                    .append(source == null ? "<unknown>" : source.getName())
                    .append(" at line ")
                    .append(diagnostic.getLineNumber())
                    .append(": ")
                    .append(diagnostic.getMessage(Locale.ROOT));
        }
        return out.toString();
    }

    // ------------------------------------------------------------------
    // javac 的三件套
    // ------------------------------------------------------------------

    /** 一段源码字符串，冒充一个 .java 文件。 */
    private static final class StringSource extends SimpleJavaFileObject {

        private final String code;

        private StringSource(String binaryName, String code) {
            super(uriOf(binaryName), Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }

        private static URI uriOf(String binaryName) {
            try {
                return new URI("string:///" + binaryName.replace('.', '/') + Kind.SOURCE.extension);
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException("invalid binary name: " + binaryName, e);
            }
        }
    }

    /** 接收 javac 写出的 .class 字节。 */
    private static final class ByteArrayOutput extends SimpleJavaFileObject {

        private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        private ByteArrayOutput(String binaryName, Kind kind) {
            super(URI.create("bytes:///" + binaryName.replace('.', '/') + kind.extension), kind);
        }

        @Override
        public OutputStream openOutputStream() {
            return buffer;
        }

        private byte[] bytes() {
            return buffer.toByteArray();
        }
    }

    /** 把内存里的输入输出接进 javac。 */
    private static final class InMemoryFileManager
            extends ForwardingJavaFileManager<StandardJavaFileManager> {

        private final Map<String, ByteArrayOutput> outputs = new HashMap<>();

        private InMemoryFileManager(StandardJavaFileManager delegate) {
            super(delegate);
        }

        @Override
        public JavaFileObject getJavaFileForOutput(
                JavaFileManager.Location location,
                String className,
                JavaFileObject.Kind kind,
                FileObject sibling) {
            ByteArrayOutput output = new ByteArrayOutput(className, kind);
            outputs.put(className, output);
            return output;
        }

        private Map<String, byte[]> classBytes() {
            Map<String, byte[]> result = new HashMap<>();
            outputs.forEach((name, output) -> result.put(name, output.bytes()));
            return Map.copyOf(result);
        }
    }

    /** 用内存里的字节码定义类；找不到的交给父加载器。 */
    private static final class MemoryClassLoader extends ClassLoader {

        private final Map<String, byte[]> classBytes;

        private MemoryClassLoader(Map<String, byte[]> classBytes) {
            super(InMemoryCompiler.class.getClassLoader());
            this.classBytes = Map.copyOf(classBytes);
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            byte[] bytes = classBytes.get(name);
            if (bytes == null) {
                return super.findClass(name);
            }
            return defineClass(name, bytes, 0, bytes.length);
        }
    }
}
