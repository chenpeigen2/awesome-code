package org.peter.inmemory.demo;

import org.peter.inmemory.InMemoryCompiler;

import java.util.Map;

/**
 * 内存编译演示：把一段源码编译成字节码、加载、调用，全程不落盘。
 * <p>
 * 运行：{@code ./gradlew :compile:inmemory:demo}
 */
public final class CompilerDemo {

    public static void main(String[] args) throws Exception {
        System.out.println(new CompilerDemo().run());
    }

    /** 跑一遍完整流程并返回可读报告。 */
    public String run() throws Exception {
        String source = """
                package demo;

                public class Greeting {
                    private final String who;

                    public Greeting(String who) { this.who = who; }

                    public String hello() { return "Hello, " + who + "!"; }

                    public static class Inner {
                        public int answer() { return 42; }
                    }
                }
                """;

        Map<String, byte[]> compiled = InMemoryCompiler.compile("demo.Greeting", source);

        StringBuilder out = new StringBuilder();
        out.append("source compiled in memory\n");
        out.append("classes produced: ").append(compiled.keySet().stream().sorted().toList())
                .append('\n');
        compiled.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> out.append("  ").append(e.getKey())
                        .append(".class: ").append(e.getValue().length).append(" bytes\n"));

        ClassLoader loader = InMemoryCompiler.classLoader(compiled);
        Class<?> greeting = loader.loadClass("demo.Greeting");
        Object instance = greeting.getConstructor(String.class).newInstance("in-memory");
        out.append("hello() = ").append(greeting.getMethod("hello").invoke(instance)).append('\n');

        // Inner 是 static 嵌套类，没有外层实例参数
        Class<?> inner = loader.loadClass("demo.Greeting$Inner");
        Object innerInstance = inner.getDeclaredConstructor().newInstance();
        out.append("Greeting$Inner.answer() = ")
                .append(inner.getMethod("answer").invoke(innerInstance));
        return out.toString();
    }
}
