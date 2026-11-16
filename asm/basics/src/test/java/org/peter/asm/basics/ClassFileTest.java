package org.peter.asm.basics;

import org.junit.jupiter.api.Test;
import org.peter.asm.basics.fixture.ClassBytes;
import org.peter.asm.basics.fixture.Greeter;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 字节码结构模型的规格。
 * <p>
 * 预期值全部来自 fixture/Greeter.java 这份源码，而不是来自被测实现。
 */
class ClassFileTest {

    private final ClassFile greeter = ClassFile.read(ClassBytes.of(Greeter.class));

    @Test
    void readsClassHeader() {
        assertEquals("org/peter/asm/basics/fixture/Greeter", greeter.name());
        assertEquals("org.peter.asm.basics.fixture.Greeter", greeter.javaName());
        assertEquals("java/lang/Object", greeter.superName());
        assertEquals(List.of("java/lang/Runnable"), greeter.interfaces());
        assertEquals("25", greeter.versionName());
        assertTrue(greeter.isPublic());
        assertFalse(greeter.isInterface());
        assertFalse(greeter.isAbstract());
    }

    @Test
    void readsFieldsWithDescriptorAndFlags() {
        assertEquals(2, greeter.fields().size());

        ClassFile.Field name = greeter.field("name").orElseThrow();
        assertEquals("Ljava/lang/String;", name.descriptor());
        assertEquals("java.lang.String", name.typeName());
        assertEquals("private final", name.modifiers());
        assertFalse(name.isStatic());

        ClassFile.Field counter = greeter.field("counter").orElseThrow();
        assertEquals("I", counter.descriptor());
        assertEquals("private static", counter.modifiers());
        assertTrue(counter.isStatic());
    }

    @Test
    void readsMethodsWithDescriptorAndFlags() {
        assertEquals(4, greeter.methods().size());

        ClassFile.Method greet = greeter.method("greet", "()Ljava/lang/String;").orElseThrow();
        assertEquals("public", greet.modifiers());
        assertEquals("java.lang.String", greet.returnTypeName());
        assertEquals(List.of(), greet.parameterTypeNames());

        ClassFile.Method constructor = greeter.method("<init>", "(Ljava/lang/String;)V").orElseThrow();
        assertEquals(List.of("java.lang.String"), constructor.parameterTypeNames());
        assertTrue(constructor.isConstructor());

        ClassFile.Method run = greeter.method("run", "()V").orElseThrow();
        assertEquals("void", run.returnTypeName());

        ClassFile.Method counter = greeter.method("counter", "()I").orElseThrow();
        assertEquals("public static", counter.modifiers());
        assertEquals("int", counter.returnTypeName());
    }

    @Test
    void reportsMissingMembersAsEmpty() {
        assertEquals(Optional.empty(), greeter.field("nope"));
        assertEquals(Optional.empty(), greeter.method("greet", "()V"));
    }

    @Test
    void describesClassLikeJavap() {
        String description = greeter.describe();
        assertTrue(description.contains(
                "public class org.peter.asm.basics.fixture.Greeter implements java.lang.Runnable {"),
                description);
        assertTrue(description.contains("  private final java.lang.String name"), description);
        assertTrue(description.contains("  private static int counter"), description);
        assertTrue(description.contains("  public org.peter.asm.basics.fixture.Greeter(java.lang.String)"),
                description);
        assertTrue(description.contains("  public java.lang.String greet()"), description);
        assertTrue(description.contains("  public void run()"), description);
        assertTrue(description.contains("  public static int counter()"), description);
        assertTrue(description.trim().endsWith("}"), description);
    }
}
