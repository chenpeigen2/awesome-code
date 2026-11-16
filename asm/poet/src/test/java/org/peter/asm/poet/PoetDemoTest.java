package org.peter.asm.poet;

import org.junit.jupiter.api.Test;
import org.peter.asm.poet.demo.PoetDemo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JavaPoet 演示程序的规格：必须真的走完「生成 → 编译 → 加载 → 调用」。
 */
class PoetDemoTest {

    @Test
    void printsTheGeneratedSource() {
        String report = new PoetDemo().run();

        assertTrue(report.contains("---- generated source ----"), report);
        assertTrue(report.contains("public final class User {"), report);
    }

    @Test
    void compilesTheSourceAndExecutesIt() {
        String report = new PoetDemo().run();

        assertTrue(report.contains("toString(): User{name=Alice, age=30}"), report);
        assertTrue(report.contains("getName(): Alice"), report);
        assertTrue(report.contains("getAge(): 30"), report);
    }

    @Test
    void demonstratesValueSemantics() {
        String report = new PoetDemo().run();

        assertTrue(report.contains("alice.equals(twin): true"), report);
        assertTrue(report.contains("alice.equals(bob): false"), report);
        assertTrue(report.contains("hashCode stable: true"), report);
    }

    @Test
    void cleansUpItsTemporaryOutput() {
        String report = new PoetDemo().run();

        // 报告里不应泄漏临时目录路径（说明清理逻辑没被跳过）
        assertFalse(report.contains("poet-demo-out"), report);
    }
}
