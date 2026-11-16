package org.peter.asm.tree.demo;

import org.objectweb.asm.tree.ClassNode;
import org.peter.asm.tree.ClassTree;
import org.peter.asm.tree.GetterSetterGenerator;

import java.util.stream.Collectors;

/**
 * Tree API 演示：给一个没有 getter/setter 的类在字节码层面补上它们，然后真的调用。
 * <p>
 * 运行：{@code ./gradlew :asm:tree:demo}
 *
 * <p>流程：
 * <ol>
 *   <li>读 {@link SampleBean} 的字节码；</li>
 *   <li>用 {@link GetterSetterGenerator} 补 getter/setter；</li>
 *   <li>交给新的 ClassLoader 定义成类；</li>
 *   <li>反射调用新生成的方法，打印真实返回值。</li>
 * </ol>
 */
public final class TreeDemo {

    private static final String BINARY_NAME = SampleBean.class.getName();

    public static void main(String[] args) {
        System.out.println(new TreeDemo().run());
    }

    /** 跑一遍完整流程并返回可读报告（测试直接断言这份报告）。 */
    public String run() {
        try {
            return execute();
        } catch (ReflectiveOperationException | java.io.IOException e) {
            throw new IllegalStateException("tree demo failed", e);
        }
    }

    private String execute() throws ReflectiveOperationException, java.io.IOException {
        byte[] original;
        try (var in = TreeDemo.class
                .getClassLoader()
                .getResourceAsStream(BINARY_NAME.replace('.', '/') + ".class")) {
            if (in == null) {
                throw new IllegalStateException("class file not found: " + BINARY_NAME);
            }
            original = in.readAllBytes();
        }

        ClassNode before = ClassTree.read(original);
        int methodsBefore = before.methods.size();

        byte[] enhanced = GetterSetterGenerator.enhance(original);
        ClassNode after = ClassTree.read(enhanced);

        Object bean = newInstance(enhanced);
        bean.getClass().getMethod("setName", String.class).invoke(bean, "张三");
        bean.getClass().getMethod("setAge", int.class).invoke(bean, 30);

        StringBuilder out = new StringBuilder();
        out.append("class: ").append(BINARY_NAME).append('\n');
        out.append("methods before: ").append(methodsBefore).append('\n');
        out.append("methods after: ").append(after.methods.size())
                .append(" (").append(after.methods.size() - methodsBefore)
                .append(" added getters/setters)\n");
        out.append("added: ").append(addedMethodNames(after)).append('\n');
        out.append("getName() = ").append(bean.getClass().getMethod("getName").invoke(bean)).append('\n');
        out.append("getAge() = ").append(bean.getClass().getMethod("getAge").invoke(bean)).append('\n');
        out.append("describe() = ").append(bean.getClass().getMethod("describe").invoke(bean));
        return out.toString();
    }

    /** 与原始类对比，列出新增的方法名。 */
    private static String addedMethodNames(ClassNode node) {
        return node.methods.stream()
                .map(m -> m.name)
                .filter(n -> n.startsWith("get") || n.startsWith("set"))
                .sorted()
                .collect(Collectors.joining(", "));
    }

    private static Object newInstance(byte[] bytes) throws ReflectiveOperationException {
        Class<?> type = new MemoryClassLoader(bytes).loadClass(BINARY_NAME);
        // SampleBean(String, int) 仍在，先用它构造，再调 setter 覆盖
        var constructor = type.getDeclaredConstructor(String.class, int.class);
        return constructor.newInstance("", 0);
    }

    /** 用给定字节码定义类；其余类交给父加载器。 */
    private static final class MemoryClassLoader extends ClassLoader {

        private final byte[] bytes;

        private MemoryClassLoader(byte[] bytes) {
            super(TreeDemo.class.getClassLoader());
            this.bytes = bytes;
        }

        /**
         * 子优先（child-first）加载。
         * <p>
         * 默认的 {@link ClassLoader#loadClass(String)} 是「父优先」：它会先在父加载器里找
         * {@code SampleBean}，父加载器有同名类（就是原始那份），于是直接返回原始类，
         * 我们的增强字节码永远不会被定义——反射调 setter 就会抛 {@code NoSuchMethodException}。
         * 所以目标类必须自己先定义。
         */
        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            if (name.equals(BINARY_NAME)) {
                Class<?> loaded = findLoadedClass(name);
                return loaded != null ? loaded : findClass(name);
            }
            return super.loadClass(name, resolve);
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            if (name.equals(BINARY_NAME)) {
                return defineClass(name, bytes, 0, bytes.length);
            }
            return super.findClass(name);
        }
    }
}
