package org.peter.javaparser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.Problem;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 把 Java 源码解析成 AST 并做结构化查询。
 * <p>
 * 为什么不直接调 {@code JavaParser}：它的 {@code ParseResult} 是「结果 + 问题列表」的复合体，
 * 天真的用法 {@code result.getResult().ifPresent(System.out::println)} 在解析失败时
 * <b>静默什么都不做</b>——看起来程序正常退出，只是没输出。这里统一把失败转成异常，
 * 让错误在第一时间暴露。
 */
public final class SourceAnalyzer {

    private final JavaParser parser = new JavaParser();

    /**
     * 解析一段源码。解析失败抛 {@link IllegalArgumentException}，消息里带全部语法问题。
     */
    public CompilationUnit parse(String source) {
        ParseResult<CompilationUnit> result = parser.parse(source);
        return unwrap(result, "inline source", source);
    }

    /**
     * 从 classpath 资源解析。路径相对于 classpath 根，例如 {@code sample/Calculator.java}。
     * <p>
     * 用资源而不是文件路径：模块不该假设自己被放在哪个目录下运行。
     */
    public CompilationUnit parseResource(String resourcePath) {
        try {
            ParseResult<CompilationUnit> result = parser.parseResource(resourcePath);
            return unwrap(result, resourcePath, null);
        } catch (java.io.IOException cause) {
            throw new IllegalArgumentException("cannot read resource: " + resourcePath, cause);
        }
    }

    private CompilationUnit unwrap(ParseResult<CompilationUnit> result, String what, String source) {
        if (result.isSuccessful() && result.getResult().isPresent()) {
            return result.getResult().orElseThrow();
        }
        String problems = result.getProblems().stream()
                .map(Problem::getVerboseMessage)
                .collect(Collectors.joining("; "));
        // 附上源码片段：只有行列号时，得回头翻原文件才知道在说什么；带上片段就能直接定位
        String excerpt = source == null ? "" : "\n--- source ---\n" + truncate(source);
        throw new IllegalArgumentException("failed to parse " + what + ": " + problems + excerpt);
    }

    private static String truncate(String source) {
        int limit = 500;
        return source.length() <= limit ? source : source.substring(0, limit) + "\n...(truncated)";
    }

    /** 方法签名列表，形如 {@code add(int, int)}——只要参数类型，不要参数名。 */
    public List<String> methodSignatures(CompilationUnit unit) {
        List<String> signatures = new ArrayList<>();
        unit.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(MethodDeclaration method, Void arg) {
                String params = method.getParameters().stream()
                        .map(p -> p.getType().asString())
                        .collect(Collectors.joining(", "));
                signatures.add(method.getNameAsString() + "(" + params + ")");
                super.visit(method, arg);
            }
        }, null);
        return signatures;
    }

    /** 所有方法声明的返回类型，按出现顺序。 */
    public List<String> returnTypes(CompilationUnit unit) {
        List<String> types = new ArrayList<>();
        unit.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(MethodDeclaration method, Void arg) {
                types.add(method.getType().asString());
                super.visit(method, arg);
            }
        }, null);
        return types;
    }

    /**
     * 所有字段的类型，按出现顺序。
     * <p>
     * 注意一个 {@code FieldDeclaration} 可能声明多个变量（{@code int a, b;}），
     * 所以要在 {@code getVariables()} 上展开，不能每个字段声明只取一次。
     */
    public List<String> fieldTypes(CompilationUnit unit) {
        List<String> types = new ArrayList<>();
        unit.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(FieldDeclaration field, Void arg) {
                for (VariableDeclarator variable : field.getVariables()) {
                    types.add(field.getElementType().asString()
                            + (variable.getType().isArrayType() ? "[]" : ""));
                }
                super.visit(field, arg);
            }
        }, null);
        return types;
    }

    /** 统计带指定注解的方法数量。注解名用简单名，如 {@code Deprecated}。 */
    public long countMethodsAnnotatedWith(CompilationUnit unit, String annotationSimpleName) {
        return unit.findAll(MethodDeclaration.class).stream()
                .filter(method -> method.getAnnotations().stream()
                        .anyMatch(a -> a.getNameAsString().equals(annotationSimpleName)
                                || a.getNameAsString().endsWith("." + annotationSimpleName)))
                .count();
    }

    /** 生成一段人读的报告，供 demo 与测试共用。 */
    public String analyzeResource(String resourcePath) {
        CompilationUnit unit = parseResource(resourcePath);
        StringBuilder out = new StringBuilder();
        out.append("class: ").append(unit.getType(0).getNameAsString()).append('\n');
        out.append("methods: ").append(methodSignatures(unit)).append('\n');
        out.append("returns: ").append(returnTypes(unit)).append('\n');
        out.append("fields: ").append(fieldTypes(unit));
        return out.toString();
    }
}
