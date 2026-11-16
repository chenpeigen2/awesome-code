package org.peter.asm.tree;

import org.junit.jupiter.api.Test;
import org.peter.asm.tree.demo.TreeDemo;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tree API 演示程序的规格。
 * <p>
 * 演示程序必须真的跑通「读字节码 → 加 getter/setter → 加载 → 调用」这条链路，
 * 而不是打印一段假的字符串。所以这里断言的是调用结果。
 */
class TreeDemoTest {

    @Test
    void enhancesABeanAndCallsTheGeneratedAccessors() {
        String report = new TreeDemo().run();

        assertTrue(report.contains("added getters/setters"), report);
        assertTrue(report.contains("getName() = 张三"), report);
        assertTrue(report.contains("getAge() = 30"), report);
    }

    @Test
    void reportsWhichMethodsWereAdded() {
        String report = new TreeDemo().run();

        assertTrue(report.contains("getName"), report);
        assertTrue(report.contains("setName"), report);
        assertTrue(report.contains("getAge"), report);
        assertTrue(report.contains("setAge"), report);
    }

    @Test
    void originalBehaviourSurvivesEnhancement() {
        String report = new TreeDemo().run();

        // SampleBean 自带的 describe() 在增强后仍然正常工作
        assertTrue(report.contains("describe() = 张三/30"), report);
    }

    @Test
    void reportsHowManyAccessorsWereAdded() {
        String report = new TreeDemo().run();

        // SampleBean 有 2 个字段 → 4 个访问器；原有 2 个方法（<init> + describe）
        assertTrue(report.contains("methods before: 2"), report);
        assertTrue(report.contains("4 added getters/setters"), report);
        assertTrue(report.contains("methods after: 6"), report);
    }
}
