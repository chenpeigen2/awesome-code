package org.peter.spoon;

import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 Spoon 解析出的<b>模型</b>，而不是它打印的文本。
 * <p>
 * Spoon 的价值在于把 Java 源码变成可查询的对象图：可以按类型过滤、读注解、
 * 改 AST 再写回。这些都能离线断言，所以不需要真项目或真数据库。
 * <p>
 * 被测源码是 {@code src/test/resources/sample/} 下的固定文件，
 * 内容由测试自己掌控，不会因为仓库其他部分改动而失效。
 */
class SpoonModelTest {

    private static CtModel model() {
        Launcher launcher = new Launcher();
        launcher.addInputResource("src/test/resources/sample");
        launcher.getEnvironment().setNoClasspath(true);
        launcher.buildModel();
        return launcher.getModel();
    }

    @Test
    void parsesEveryClassInTheSamplePackage() {
        Set<String> names = model().getElements(new TypeFilter<>(CtClass.class)).stream()
                .map(CtClass::getSimpleName)
                .filter(name -> !name.isEmpty())
                .collect(Collectors.toSet());

        assertTrue(names.contains("Order"), names.toString());
        assertTrue(names.contains("OrderService"), names.toString());
    }

    @Test
    void readsMethodSignatures() {
        CtClass<?> service = model().getElements(new TypeFilter<>(CtClass.class)).stream()
                .filter(c -> "OrderService".equals(c.getSimpleName()))
                .findFirst()
                .orElseThrow();

        List<String> signatures = service.getMethods().stream()
                .map(m -> m.getSimpleName() + "/" + m.getParameters().size())
                .toList();

        assertTrue(signatures.contains("total/1"), signatures.toString());
        assertTrue(signatures.contains("find/1"), signatures.toString());
    }

    @Test
    void readsFieldTypes() {
        CtClass<?> order = model().getElements(new TypeFilter<>(CtClass.class)).stream()
                .filter(c -> "Order".equals(c.getSimpleName()))
                .findFirst()
                .orElseThrow();

        Set<String> fieldTypes = order.getFields().stream()
                .map(f -> f.getType().getSimpleName())
                .collect(Collectors.toSet());

        assertEquals(Set.of("String", "List", "long"), fieldTypes);
    }

    @Test
    void findsMethodsReturningAPrimitive() {
        // 典型的「按结构查询」用法：找出所有返回 long 的方法
        List<String> longs = model().getElements(new TypeFilter<>(CtMethod.class)).stream()
                .filter(m -> m.getType() != null && "long".equals(m.getType().getSimpleName()))
                .map(CtMethod::getSimpleName)
                .toList();

        assertTrue(longs.contains("total"), longs.toString());
    }

    @Test
    void readsAnnotationsOnMethods() {
        CtMethod<?> annotated = model().getElements(new TypeFilter<>(CtMethod.class)).stream()
                .filter(m -> m.getAnnotation(Deprecated.class) != null)
                .findFirst()
                .orElseThrow(() -> new AssertionError("示例里应该有 @Deprecated 方法"));

        assertEquals("legacyTotal", annotated.getSimpleName());
    }

    @Test
    void countsMethodsPerClass() {
        CtClass<?> order = model().getElements(new TypeFilter<>(CtClass.class)).stream()
                .filter(c -> "Order".equals(c.getSimpleName()))
                .findFirst()
                .orElseThrow();

        // 只数声明的，不含继承来的
        assertEquals(2, order.getMethods().size(),
                order.getMethods().stream().map(CtMethod::getSimpleName).toList().toString());
    }

    @Test
    void demoRunsOffline() {
        String report = new SpoonDemo().run();

        assertTrue(report.contains("classes: [Order, OrderService]"), report);
    }
}
