package org.peter.asm.poet;

import org.junit.jupiter.api.Test;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 生成的源码必须「真的能被 javac 编译、编译出来的类真的能用」。
 * <p>
 * 这是 JavaPoet 最硬的验收标准：文本再好看，编译不过也没用。
 */
class GeneratedSourceCompilesTest {

    @Test
    void generatedSourceCompilesAndBehavesLikeAValueObject() throws Exception {
        String source = BeanGenerator.generate("com.example.generated", "User",
                Property.of("name", String.class),
                Property.of("age", int.class));

        Class<?> type = compile("com.example.generated.User", source);
        Object alice = newInstance(type, "Alice", 30);
        Object aliceTwin = newInstance(type, "Alice", 30);
        Object bob = newInstance(type, "Bob", 30);

        assertEquals(alice, aliceTwin, "相同字段值应相等");
        assertEquals(alice.hashCode(), aliceTwin.hashCode(), "相等对象哈希值必须相同");
        assertNotEquals(alice, bob);
        assertEquals("User{name=Alice, age=30}", alice.toString());
        assertEquals("Alice", type.getMethod("getName").invoke(alice));
        assertEquals(30, type.getMethod("getAge").invoke(alice));
    }

    @Test
    void generatedSourceWithNoPropertiesCompiles() throws Exception {
        String source = BeanGenerator.generate("com.example.generated", "Marker");

        Class<?> type = compile("com.example.generated.Marker", source);
        Object instance = type.getDeclaredConstructor().newInstance();

        assertEquals("Marker{}", instance.toString());
        assertEquals(instance, type.getDeclaredConstructor().newInstance());
    }

    @Test
    void generatedSourceCoversEveryPrimitiveType() throws Exception {
        String source = BeanGenerator.generate("com.example.generated", "AllTypes",
                Property.of("flag", boolean.class),
                Property.of("letter", char.class),
                Property.of("small", byte.class),
                Property.of("medium", short.class),
                Property.of("count", int.class),
                Property.of("big", long.class),
                Property.of("ratio", float.class),
                Property.of("precise", double.class));

        Class<?> type = compile("com.example.generated.AllTypes", source);

        assertEquals(8, type.getDeclaredFields().length);
        assertEquals(8, java.util.Arrays.stream(type.getDeclaredMethods())
                .filter(m -> m.getName().startsWith("get")).count());
    }

    // ------------------------------------------------------------------
    // 编译 + 加载工具
    // ------------------------------------------------------------------

    private static Class<?> compile(String binaryName, String source) throws Exception {
        Path sourceDir = Files.createTempDirectory("javapoet-src");
        Path outputDir = Files.createTempDirectory("javapoet-out");

        Path sourceFile = sourceDir.resolve(binaryName.replace('.', File.separatorChar) + ".java");
        Files.createDirectories(sourceFile.getParent());
        Files.writeString(sourceFile, source, StandardCharsets.UTF_8);

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        int exitCode = compiler.run(
                null, null, null,
                "-d", outputDir.toString(),
                "--release", "25",
                sourceFile.toString());
        assertTrue(exitCode == 0, "javac failed for:\n" + source);

        return new java.net.URLClassLoader(
                new java.net.URL[] {outputDir.toUri().toURL()},
                GeneratedSourceCompilesTest.class.getClassLoader())
                .loadClass(binaryName);
    }

    private static Object newInstance(Class<?> type, Object... arguments) throws Exception {
        Class<?>[] parameterTypes = java.util.Arrays.stream(arguments)
                .map(GeneratedSourceCompilesTest::primitiveOf)
                .toArray(Class<?>[]::new);

        return type.getDeclaredConstructor(parameterTypes).newInstance(arguments);
    }

    /** 包装类型 → 对应的基本类型，用于精确匹配构造器签名。 */
    private static Class<?> primitiveOf(Object boxed) {
        if (boxed instanceof Integer) {
            return int.class;
        }
        if (boxed instanceof Long) {
            return long.class;
        }
        if (boxed instanceof Boolean) {
            return boolean.class;
        }
        if (boxed instanceof Double) {
            return double.class;
        }
        if (boxed instanceof Float) {
            return float.class;
        }
        if (boxed instanceof Short) {
            return short.class;
        }
        if (boxed instanceof Byte) {
            return byte.class;
        }
        if (boxed instanceof Character) {
            return char.class;
        }
        return boxed.getClass();
    }
}
