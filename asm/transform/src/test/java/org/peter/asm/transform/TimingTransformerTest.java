package org.peter.asm.transform;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.peter.asm.transform.fixture.Calculator;
import org.peter.asm.transform.fixture.Classes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 方法计时增强的规格。
 * <p>
 * 断言的是「行为不变 + 计时回调被调用」，而不是插入了哪些指令。
 */
class TimingTransformerTest {

    private static final String BINARY_NAME = "org.peter.asm.transform.fixture.Calculator";

    private Classes.Timings timings;

    @BeforeEach
    void setUp() {
        timings = new Classes.Timings();
        TimingRuntime.install(timings::record);
    }

    @AfterEach
    void tearDown() {
        TimingRuntime.clear();
    }

    private Object transformedInstance() throws Exception {
        byte[] bytes = TimingTransformer.transform(Classes.bytesOf(Calculator.class));
        return Classes.newInstance(bytes, BINARY_NAME);
    }

    @Test
    void preservesReturnValuesForAllPrimitiveKinds() throws Exception {
        Object calculator = transformedInstance();

        assertEquals(7, Classes.call(calculator, "add", 3, 4));
        assertEquals(42L, Classes.call(calculator, "multiply", 6L, 7L));
        assertEquals("ab", Classes.call(calculator, "concat", "a", "b"));
        assertEquals(2.5d, Classes.call(calculator, "half", 5.0d));
    }

    @Test
    void preservesStaticMethodBehaviour() throws Exception {
        Object calculator = transformedInstance();

        assertEquals(49, Classes.call(calculator, "square", 7));
    }

    @Test
    void reportsOneTimingEntryPerCall() throws Exception {
        Object calculator = transformedInstance();

        Classes.call(calculator, "add", 1, 2);
        Classes.call(calculator, "multiply", 2L, 3L);

        assertEquals(2, timings.entries().size(), timings.entries().toString());
        assertEquals(java.util.List.of("add", "multiply"), timings.methodNames());
    }

    @Test
    void reportsNonNegativeDuration() throws Exception {
        Object calculator = transformedInstance();

        Classes.call(calculator, "add", 1, 2);

        long nanos = timings.entries().get(0).nanos();
        assertTrue(nanos >= 0, "duration must be non-negative: " + nanos);
    }

    @Test
    void stillThrowsTheOriginalException() {
        Object calculator;
        try {
            calculator = transformedInstance();
        } catch (Exception e) {
            throw new AssertionError(e);
        }

        IllegalStateException thrown = assertThrows(
                IllegalStateException.class, () -> Classes.call(calculator, "boom"));
        assertEquals("boom", thrown.getMessage());
    }

    @Test
    void reportsTimingEvenWhenTheMethodThrows() throws Exception {
        Object calculator = transformedInstance();

        try {
            Classes.call(calculator, "boom");
        } catch (Exception expected) {
            // 计时应该在 finally 里完成
        }

        assertEquals(java.util.List.of("boom"), timings.methodNames());
    }

    @Test
    void skipsConstructorsAndStaticInitialisers() throws Exception {
        Object calculator = transformedInstance();
        timings.clear();

        Classes.call(calculator, "add", 1, 2);

        assertEquals(java.util.List.of("add"), timings.methodNames(),
                "构造器不应产生计时条目");
    }

    @Test
    void transformOverloadInstallsTheSinkForTheCurrentThread() throws Exception {
        TimingRuntime.clear(); // 清掉 setUp 装的 sink，验证 overload 自己会装
        Classes.Timings ownSink = new Classes.Timings();

        byte[] bytes = TimingTransformer.transform(Classes.bytesOf(Calculator.class), ownSink::record);
        Object calculator = Classes.newInstance(bytes, BINARY_NAME);
        Classes.call(calculator, "add", 1, 2);

        assertEquals(java.util.List.of("add"), ownSink.methodNames());
    }

    @Test
    void transformedClassPassesAsmVerifier() {
        byte[] bytes = TimingTransformer.transform(Classes.bytesOf(Calculator.class));
        TransformAssertions.assertVerifies(bytes);
    }
}
