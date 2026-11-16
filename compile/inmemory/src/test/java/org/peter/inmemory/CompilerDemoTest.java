package org.peter.inmemory;

import org.junit.jupiter.api.Test;
import org.peter.inmemory.demo.CompilerDemo;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 内存编译演示的规格。
 */
class CompilerDemoTest {

    @Test
    void compilesAndRunsInMemory() throws Exception {
        String report = new CompilerDemo().run();

        assertTrue(report.contains("hello() = Hello, in-memory!"), report);
    }

    @Test
    void reportsInnerClassBytecode() throws Exception {
        String report = new CompilerDemo().run();

        assertTrue(report.contains("demo.Greeting"), report);
        assertTrue(report.contains("demo.Greeting$Inner"), report);
        assertTrue(report.contains("Greeting$Inner.answer() = 42"), report);
    }
}
