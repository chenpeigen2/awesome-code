package org.peter.asm.poet.demo;

import org.peter.asm.poet.BeanGenerator;
import org.peter.asm.poet.Property;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * JavaPoet 演示：生成源码 → 交给 javac 编译 → 加载 → 调用。
 * <p>
 * 运行：{@code ./gradlew :asm:poet:demo}
 *
 * <p>和 {@code :asm:basics:demo} 对照着看：
 * <ul>
 *   <li>basics 直接用 ASM 写字节码，一步到位，但可读性为零；</li>
 *   <li>poet 写源码文本，多一次 javac，但生成的代码人可读、可 diff、可提交。</li>
 * </ul>
 */
public final class PoetDemo {

    public static void main(String[] args) {
        System.out.println(new PoetDemo().run());
    }

    /** 跑一遍完整流程并返回可读报告。 */
    public String run() {
        try {
            return execute();
        } catch (ReflectiveOperationException | IOException e) {
            throw new IllegalStateException("poet demo failed", e);
        }
    }

    private String execute() throws ReflectiveOperationException, IOException {
        String source = BeanGenerator.generate("com.example.generated", "User",
                Property.of("name", String.class),
                Property.of("age", int.class));

        StringBuilder out = new StringBuilder();
        out.append("---- generated source ----\n").append(source).append('\n');
        out.append("---- compiled and executed ----\n");

        Path outputDir = Files.createTempDirectory("poet-demo-out");
        Path sourceFile = outputDir.resolve("User.java");
        Files.writeString(sourceFile, source, StandardCharsets.UTF_8);

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        int exit = compiler.run(null, null, null,
                "-d", outputDir.toString(), "--release", "25", sourceFile.toString());
        if (exit != 0) {
            throw new IllegalStateException("javac failed with exit code " + exit);
        }

        try (URLClassLoader loader = new URLClassLoader(
                new URL[] {outputDir.toUri().toURL()}, PoetDemo.class.getClassLoader())) {
            Class<?> type = loader.loadClass("com.example.generated.User");
            Object alice = type.getConstructor(String.class, int.class).newInstance("Alice", 30);
            Object twin = type.getConstructor(String.class, int.class).newInstance("Alice", 30);
            Object bob = type.getConstructor(String.class, int.class).newInstance("Bob", 30);

            out.append("toString(): ").append(alice).append('\n');
            out.append("getName(): ").append(type.getMethod("getName").invoke(alice)).append('\n');
            out.append("getAge(): ").append(type.getMethod("getAge").invoke(alice)).append('\n');
            out.append("alice.equals(twin): ").append(alice.equals(twin)).append('\n');
            out.append("alice.equals(bob): ").append(alice.equals(bob)).append('\n');
            out.append("hashCode stable: ")
                    .append(alice.hashCode() == twin.hashCode());
        } finally {
            deleteRecursively(outputDir);
        }
        return out.toString();
    }

    private static void deleteRecursively(Path root) throws IOException {
        try (var paths = Files.walk(root)) {
            for (Path path : paths.sorted(java.util.Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(path);
            }
        }
    }
}
