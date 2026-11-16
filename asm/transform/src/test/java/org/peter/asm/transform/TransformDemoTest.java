package org.peter.asm.transform;

import org.junit.jupiter.api.Test;
import org.peter.asm.transform.demo.TransformDemo;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 转换演示程序的规格：三个能力（计时 / 删方法 / 改名）都要真的作用在字节码上。
 */
class TransformDemoTest {

    @Test
    void reportsTimingForEveryInvocation() {
        String report = new TransformDemo().run();

        assertTrue(report.contains("add(2, 3) = 5"), report);
        assertTrue(report.contains("timed: add"), report);
    }

    @Test
    void reportsRemovedMethod() {
        String report = new TransformDemo().run();

        assertTrue(report.contains("removed: obsolete"), report);
        assertTrue(report.contains("obsolete is gone: true"), report);
    }

    @Test
    void reportsRenamedClass() {
        String report = new TransformDemo().run();

        assertTrue(report.contains("renamed to: org.peter.asm.transform.demo.RenamedWork"), report);
    }

    @Test
    void timingSkipsConstructors() {
        String report = new TransformDemo().run();

        assertTrue(report.contains("timed methods exclude <init>"), report);
    }
}
