package org.peter.javaparser;

import com.github.javaparser.ast.CompilationUnit;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JavaParser 把源码解析成 AST 后的结构化查询能力。
 * <p>
 * 断言的都是「从源码里读出了什么」，而不是 JavaParser 内部怎么实现。
 */
class SourceAnalyzerTest {

    private final SourceAnalyzer analyzer = new SourceAnalyzer();

    @Test
    void readsPackageAndClassName() {
        CompilationUnit unit = analyzer.parse("""
                package demo;
                public class Foo {}
                """);

        assertEquals("demo", unit.getPackageDeclaration().orElseThrow().getNameAsString());
        assertEquals("Foo", unit.getType(0).getNameAsString());
    }

    @Test
    void readsMethodSignatures() {
        CompilationUnit unit = analyzer.parse("""
                package demo;
                public class Foo {
                    public int add(int a, int b) { return a + b; }
                    private void log(String msg) {}
                }
                """);

        List<String> signatures = analyzer.methodSignatures(unit);

        assertEquals(List.of("add(int, int)", "log(String)"), signatures);
    }

    @Test
    void readsReturnTypes() {
        CompilationUnit unit = analyzer.parse("""
                package demo;
                public class Foo {
                    public long total() { return 0L; }
                    public String name() { return null; }
                }
                """);

        assertEquals(List.of("long", "String"), analyzer.returnTypes(unit));
    }

    @Test
    void readsFieldTypes() {
        CompilationUnit unit = analyzer.parse("""
                package demo;
                import java.util.List;
                public class Foo {
                    private String id;
                    private List<String> items;
                    private int count;
                }
                """);

        assertEquals(List.of("String", "List<String>", "int"), analyzer.fieldTypes(unit));
    }

    @Test
    void countsAnnotationsOnMethods() {
        CompilationUnit unit = analyzer.parse("""
                package demo;
                public class Foo {
                    @Deprecated
                    public void old() {}

                    @Override
                    public String toString() { return ""; }

                    public void plain() {}
                }
                """);

        assertEquals(1, analyzer.countMethodsAnnotatedWith(unit, "Deprecated"));
        assertEquals(1, analyzer.countMethodsAnnotatedWith(unit, "Override"));
        assertEquals(0, analyzer.countMethodsAnnotatedWith(unit, "SafeVarargs"));
    }

    @Test
    void reportsParseFailureInsteadOfReturningSilently() {
        // 语法错误必须抛异常。原实现用 result.getResult().ifPresent(...)，
        // 解析失败时什么都不发生，看起来像「正常但没输出」，最难查的一类 bug。
        var failure = org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> analyzer.parse("public class Broken { void x( }"));

        assertTrue(failure.getMessage().contains("Broken"), failure.getMessage());
    }

    @Test
    void analyzesSampleFileFromTestResources() {
        // 走真实文件路径：验证 parseResource 能找到 classpath 上的资源，
        // 而不是依赖某个硬编码的绝对路径
        String report = analyzer.analyzeResource("sample/Calculator.java");

        assertTrue(report.contains("class: Calculator"), report);
        assertTrue(report.contains("add(int, int)"), report);
        assertFalse(report.isBlank());
    }

    @Test
    void demoRunsWithoutAnyExternalFile() {
        String report = new ParserDemo().run();

        assertTrue(report.contains("class: Order"), report);
        assertTrue(report.contains("total()"), report);
    }
}
