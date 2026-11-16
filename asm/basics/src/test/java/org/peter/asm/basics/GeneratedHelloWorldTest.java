package org.peter.asm.basics;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.CheckClassAdapter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ClassWriter 生成的字节码必须「真的能跑」。
 * <p>
 * 断言不是比对字节，而是把类加载起来、调用方法、看真实输出——
 * 这才是字节码是否正确的外部可观测证据。
 */
class GeneratedHelloWorldTest {

    @Test
    void generatedClassPrintsThroughItsStaticMethod() throws Exception {
        byte[] bytes = GeneratedHelloWorld.dump();

        assertEquals("Hello from ASM", GeneratedHelloWorld.callStatic(bytes, "message"));
    }

    @Test
    void generatedClassExposesTheDeclaredMembers() {
        ClassFile file = ClassFile.read(GeneratedHelloWorld.dump());

        assertEquals("sample/HelloWorld", file.name());
        assertEquals("java/lang/Object", file.superName());
        assertEquals("java.lang.String", file.field("greeting").orElseThrow().typeName());
        assertTrue(file.field("greeting").orElseThrow().isStatic());
        assertTrue(file.method("main", "([Ljava/lang/String;)V").isPresent());
        assertTrue(file.method("<init>", "()V").isPresent());
    }

    @Test
    void generatedClassPassesAsmOwnVerifier() {
        // CheckClassAdapter.verify 用 ASM 自己的数据流分析校验生成的字节码，
        // 校验失败会把错误写进 PrintWriter，而不是抛异常
        StringWriter errors = new StringWriter();
        CheckClassAdapter.verify(
                new ClassReader(GeneratedHelloWorld.dump()), false, new PrintWriter(errors));
        assertEquals("", errors.toString().trim());
    }

    @Test
    void generatedInstanceMethodWorksOnAnInstance() throws Exception {
        byte[] bytes = GeneratedHelloWorld.dump();

        Object instance = GeneratedHelloWorld.newInstance(bytes);
        Method greet = instance.getClass().getMethod("greet", String.class);

        assertEquals("Hello, ASM!", greet.invoke(instance, "ASM"));
    }
}
