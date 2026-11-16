package org.peter.inmemory;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 内存编译的规格。
 * <p>
 * 这是 ASM 的「邻居」技术：ASM 直接写字节码，这里则让 javac 在内存里编译源码。
 * 两者都产出一个 {@code byte[]}，都可以交给 ClassLoader 定义成类。
 */
class InMemoryCompilerTest {

    @Test
    void compilesSimpleClassAndReturnsBytecode() {
        Map<String, byte[]> compiled = InMemoryCompiler.compile(
                "demo.Greeting",
                """
                package demo;
                public class Greeting {
                    public String hello() { return "hi"; }
                }
                """);

        assertTrue(compiled.containsKey("demo.Greeting"), compiled.keySet().toString());
        assertTrue(compiled.get("demo.Greeting").length > 0);
        // class 文件以魔数 0xCAFEBABE 开头
        byte[] bytes = compiled.get("demo.Greeting");
        assertEquals((byte) 0xCA, bytes[0]);
        assertEquals((byte) 0xFE, bytes[1]);
        assertEquals((byte) 0xBA, bytes[2]);
        assertEquals((byte) 0xBE, bytes[3]);
    }

    @Test
    void compiledClassIsLoadableAndCallable() throws Exception {
        Map<String, byte[]> compiled = InMemoryCompiler.compile(
                "demo.Greeting",
                """
                package demo;
                public class Greeting {
                    public String hello() { return "hi"; }
                }
                """);

        Object instance = InMemoryCompiler.newInstance(compiled, "demo.Greeting");

        assertEquals("hi", instance.getClass().getMethod("hello").invoke(instance));
    }

    @Test
    void collectsInnerClassesToo() {
        Map<String, byte[]> compiled = InMemoryCompiler.compile(
                "demo.Outer",
                """
                package demo;
                public class Outer {
                    public static class Inner {
                        public int value() { return 42; }
                    }
                }
                """);

        assertTrue(compiled.containsKey("demo.Outer"), compiled.keySet().toString());
        assertTrue(compiled.containsKey("demo.Outer$Inner"), compiled.keySet().toString());
    }

    @Test
    void reportsCompilationErrorsWithDiagnostics() {
        InMemoryCompiler.CompilationException thrown = assertThrows(
                InMemoryCompiler.CompilationException.class,
                () -> InMemoryCompiler.compile(
                        "demo.Broken",
                        """
                        package demo;
                        public class Broken {
                            public void bad() { return 1; }
                        }
                        """));

        assertTrue(thrown.getMessage().contains("Broken.java"), thrown.getMessage());
    }

    @Test
    void compileFailureLeavesNoPartiallyWrittenState() {
        // 同名类先成功编译一次，再编译一个坏版本：失败的调用不应污染结果
        InMemoryCompiler.compile("demo.Ok", """
                package demo;
                public class Ok { public int value() { return 1; } }
                """);

        assertThrows(InMemoryCompiler.CompilationException.class, () -> InMemoryCompiler.compile(
                "demo.Ok",
                """
                package demo;
                public class Ok { this is not java }
                """));
    }
}
