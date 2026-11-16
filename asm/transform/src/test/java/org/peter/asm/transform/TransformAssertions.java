package org.peter.asm.transform;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.CheckClassAdapter;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** 用 ASM 自己的数据流分析验证字节码，出错会写进 PrintWriter 而不是抛异常。 */
final class TransformAssertions {

    private TransformAssertions() {
    }

    static void assertVerifies(byte[] classBytes) {
        StringWriter errors = new StringWriter();
        CheckClassAdapter.verify(
                new ClassReader(classBytes),
                TransformAssertions.class.getClassLoader(),
                false,
                new PrintWriter(errors));
        assertEquals("", errors.toString().trim(), "ASM verifier reported errors");
    }
}
