package org.peter.javaparser;

/**
 * 演示入口：把一段内置源码解析成 AST 并打印结构。
 * <p>
 * 运行：{@code ./gradlew :explore:javaParser:demo}
 * <p>
 * 源码以字符串内联，不读磁盘文件。原来的实现硬编码了
 * {@code /home/chenpeigen/demo_java/...}，换台机器就抛 {@code NoSuchFileException}，
 * demo 形同虚设。
 */
public final class ParserDemo {

    private static final String SOURCE = """
            package sample;

            import java.util.List;

            public class Order {
                private String id;
                private List<String> items;
                private long totalCents;

                public long total() { return totalCents; }

                @Deprecated
                public long legacyTotal() { return totalCents; }
            }
            """;

    public static void main(String[] args) {
        System.out.print(new ParserDemo().run());
    }

    public String run() {
        SourceAnalyzer analyzer = new SourceAnalyzer();
        var unit = analyzer.parse(SOURCE);

        StringBuilder out = new StringBuilder();
        out.append("JavaParser turns source text into a typed AST\n");
        out.append("class: ").append(unit.getType(0).getNameAsString()).append('\n');
        out.append("methods: ").append(analyzer.methodSignatures(unit)).append('\n');
        out.append("returns: ").append(analyzer.returnTypes(unit)).append('\n');
        out.append("fields: ").append(analyzer.fieldTypes(unit)).append('\n');
        out.append("@Deprecated methods: ")
                .append(analyzer.countMethodsAnnotatedWith(unit, "Deprecated"));
        return out.toString();
    }
}
