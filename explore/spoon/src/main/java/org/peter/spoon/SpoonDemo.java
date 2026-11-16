package org.peter.spoon;

import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * 演示入口：把一段 Java 源码解析成模型并做结构化查询。
 * <p>
 * 运行：{@code ./gradlew :explore:spoon:demo}
 * <p>
 * 被解析的源码直接以字符串写在 {@link #SAMPLE} 里，而不是读某个磁盘路径——
 * 这样 demo 不依赖仓库布局，也不会因为别处改动而失效。
 */
public final class SpoonDemo {

    private static final String SAMPLE = """
            package sample;

            import java.util.ArrayList;
import java.util.List;

            public class Order {
                private String id;
                private List<String> items;
                private long totalCents;

                public long total() { return totalCents; }

                @Deprecated
                public long legacyTotal() { return totalCents; }
            }

            class OrderService {
                public long total(Order order) { return order.total(); }
                public Order find(String id) { return new Order(); }
            }
            """;

    public static void main(String[] args) {
        System.out.print(new SpoonDemo().run());
    }

    public String run() {
        CtModel model = buildModel();

        // Spoon 的 getElements 返回原始类型 List<CtClass>，无法直接变成 List<CtClass<?>>；
        // 逐个取值再放进带通配符的列表，避免在后面 getFields()/getMethods() 上踩泛型擦除
        List<CtClass<?>> classes = new ArrayList<>();
        for (CtClass<?> candidate : model.getElements(new TypeFilter<>(CtClass.class))) {
            if (!candidate.getSimpleName().isEmpty()) {
                classes.add(candidate);
            }
        }

        StringBuilder out = new StringBuilder();
        out.append("Spoon parses Java source into a queryable object model\n");
        out.append("classes: ")
                .append(classes.stream().map(CtClass::getSimpleName).sorted().toList())
                .append('\n');

        for (CtClass<?> type : classes) {
            out.append("  ").append(type.getSimpleName()).append('\n');
            out.append("    fields: ")
                    .append(type.getFields().stream()
                            .map(f -> f.getSimpleName() + ":" + f.getType().getSimpleName())
                            .toList())
                    .append('\n');
            out.append("    methods: ")
                    .append(type.getMethods().stream()
                            .map(CtMethod::getSimpleName)
                            .toList())
                    .append('\n');
        }

        TreeSet<String> deprecated = model.getElements(new TypeFilter<>(CtMethod.class)).stream()
                .filter(m -> m.getAnnotation(Deprecated.class) != null)
                .map(CtMethod::getSimpleName)
                .collect(Collectors.toCollection(TreeSet::new));
        out.append("deprecated methods: ").append(deprecated).append('\n');

        List<String> returnsLong = model.getElements(new TypeFilter<>(CtMethod.class)).stream()
                .filter(m -> m.getType() != null && "long".equals(m.getType().getSimpleName()))
                .map(CtMethod::getSimpleName)
                .toList();
        out.append("methods returning long: ").append(returnsLong);
        return out.toString();
    }

    private CtModel buildModel() {
        // 直接解析字符串：不用 addInputResource 落盘，demo 自包含
        Launcher launcher = new Launcher();
        launcher.getEnvironment().setNoClasspath(true);
        launcher.addInputResource(new spoon.support.compiler.VirtualFile(SAMPLE, "sample/Order.java"));
        launcher.buildModel();
        return launcher.getModel();
    }
}
