package org.peter.asm.transform;

import org.junit.jupiter.api.Test;
import org.peter.asm.transform.fixture.Calculator;
import org.peter.asm.transform.fixture.Classes;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 方法裁剪与类重命名的规格。
 * <p>
 * 两个变换都基于 asm-commons，展示「不改业务代码就能改变类的能力边界」。
 */
class MemberTransformersTest {

    private static final String BINARY_NAME = "org.peter.asm.transform.fixture.Calculator";

    @Test
    void removesNamedMethodAndKeepsTheRest() throws Exception {
        byte[] bytes = MethodRemover.remove(Classes.bytesOf(Calculator.class), "boom");
        Class<?> type = Classes.load(bytes, BINARY_NAME);

        assertFalse(Arrays.stream(type.getDeclaredMethods())
                .anyMatch(m -> m.getName().equals("boom")), "boom should be gone");

        Object calculator = type.getDeclaredConstructor().newInstance();
        assertEquals(7, Classes.call(calculator, "add", 3, 4));
    }

    @Test
    void removingAnAbsentMethodIsANoOp() {
        byte[] original = Classes.bytesOf(Calculator.class);
        byte[] bytes = MethodRemover.remove(original, "doesNotExist");

        assertEquals(
                Arrays.stream(Classes.load(original, BINARY_NAME).getDeclaredMethods())
                        .map(m -> m.getName() + descriptorOf(m))
                        .sorted()
                        .toList(),
                Arrays.stream(Classes.load(bytes, BINARY_NAME).getDeclaredMethods())
                        .map(m -> m.getName() + descriptorOf(m))
                        .sorted()
                        .toList());
    }

    @Test
    void removesSeveralMethodsAtOnce() throws Exception {
        byte[] bytes = MethodRemover.remove(
                Classes.bytesOf(Calculator.class), List.of("boom", "concat"));
        Class<?> type = Classes.load(bytes, BINARY_NAME);

        List<String> remaining = Arrays.stream(type.getDeclaredMethods())
                .map(m -> m.getName())
                .sorted()
                .toList();

        assertTrue(remaining.containsAll(List.of("add", "multiply", "half", "square")), remaining.toString());
        assertFalse(remaining.contains("boom"));
        assertFalse(remaining.contains("concat"));
    }

    @Test
    void removesMethodByFullDescriptorSoOverloadsStayIntact() throws Exception {
        byte[] bytes = MethodRemover.remove(
                Classes.bytesOf(Overloaded.class), "value", "()I");

        Class<?> type = Classes.load(bytes, Overloaded.class.getName());
        List<String> remaining = Arrays.stream(type.getDeclaredMethods())
                .map(m -> m.getName() + descriptorOf(m))
                .sorted()
                .toList();

        assertTrue(remaining.stream().noneMatch(s -> s.equals("value()I")), remaining.toString());
        assertTrue(remaining.stream().anyMatch(s -> s.equals("value(Ljava/lang/String;)I")), remaining.toString());
    }

    @Test
    void renamesClassIncludingItsSelfReferences() throws Exception {
        byte[] bytes = ClassRenamer.rename(
                Classes.bytesOf(Calculator.class),
                "org/peter/asm/transform/fixture/Calculator",
                "org/peter/asm/transform/fixture/RenamedCalculator");

        Class<?> type = Classes.load(bytes, "org.peter.asm.transform.fixture.RenamedCalculator");
        assertEquals("org.peter.asm.transform.fixture.RenamedCalculator", type.getName());

        Object instance = type.getDeclaredConstructor().newInstance();
        // square() 内部用 INVOKESTATIC 引用自己，重命名后必须还能调通
        assertEquals(9, Classes.call(instance, "square", 3));
    }

    @Test
    void renamedClassPassesAsmVerifier() {
        byte[] bytes = ClassRenamer.rename(
                Classes.bytesOf(Calculator.class),
                "org/peter/asm/transform/fixture/Calculator",
                "org/peter/asm/transform/fixture/RenamedCalculator");

        TransformAssertions.assertVerifies(bytes);
    }

    /** 用于验证「按完整描述符删除」不会被同名重载干扰。 */
    public static class Overloaded {
        public int value() {
            return 1;
        }

        public int value(String ignored) {
            return 2;
        }
    }

    private static String descriptorOf(java.lang.reflect.Method method) {
        StringBuilder sb = new StringBuilder("(");
        for (Class<?> parameter : method.getParameterTypes()) {
            sb.append(org.objectweb.asm.Type.getDescriptor(parameter));
        }
        return sb.append(')').append(org.objectweb.asm.Type.getDescriptor(method.getReturnType())).toString();
    }
}
