package org.peter.asm.transform.demo;

import org.peter.asm.transform.ClassRenamer;
import org.peter.asm.transform.MethodRemover;
import org.peter.asm.transform.TimingRuntime;
import org.peter.asm.transform.TimingTransformer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * asm-commons 演示：对同一个类依次做三件事，每件都真的作用在字节码上。
 * <p>
 * 运行：{@code ./gradlew :asm:transform:demo}
 *
 * <ol>
 *   <li><b>计时</b>：{@link TimingTransformer} 在每个方法前后插 {@code System.nanoTime()}；</li>
 *   <li><b>删方法</b>：{@link MethodRemover} 删掉 {@code obsolete()}；</li>
 *   <li><b>改名</b>：{@link ClassRenamer} 把类改名为 {@code RenamedWork}，类内自引用一起改。</li>
 * </ol>
 */
public final class TransformDemo {

    private static final String ORIGINAL = Work.class.getName();
    private static final String RENAMED = TransformDemo.class.getPackageName() + ".RenamedWork";

    public static void main(String[] args) {
        System.out.println(new TransformDemo().run());
    }

    /** 跑一遍完整流程并返回可读报告。 */
    public String run() {
        try {
            return execute();
        } catch (ReflectiveOperationException | IOException e) {
            throw new IllegalStateException("transform demo failed", e);
        }
    }

    private String execute() throws ReflectiveOperationException, IOException {
        byte[] original = readClassFile(ORIGINAL);
        StringBuilder out = new StringBuilder();

        // ---- 1. 计时 ----
        List<String> timings = new ArrayList<>();
        TimingRuntime.install((method, nanos) -> timings.add(method + " (" + nanos + "ns)"));

        byte[] timedBytes = TimingTransformer.transform(original);
        Class<?> timedType = new MemoryClassLoader(timedBytes, ORIGINAL).loadClass(ORIGINAL);
        Object timed = timedType.getDeclaredConstructor().newInstance();
        int sum = (int) timedType.getMethod("add", int.class, int.class).invoke(timed, 2, 3);

        out.append("timed class: ").append(ORIGINAL).append('\n');
        out.append("add(2, 3) = ").append(sum).append('\n');
        timings.forEach(t -> out.append("timed: ").append(t).append('\n'));
        out.append("timed methods exclude <init>: ")
                .append(timings.stream().noneMatch(t -> t.startsWith("<init>")))
                .append('\n');
        TimingRuntime.clear();

        // ---- 2. 删方法 ----
        byte[] pruned = MethodRemover.remove(original, "obsolete");
        Class<?> prunedType = new MemoryClassLoader(pruned, ORIGINAL).loadClass(ORIGINAL);
        boolean gone = java.util.Arrays.stream(prunedType.getDeclaredMethods())
                .noneMatch(m -> m.getName().equals("obsolete"));

        out.append("removed: obsolete\n");
        out.append("obsolete is gone: ").append(gone).append('\n');
        Object prunedInstance = prunedType.getDeclaredConstructor().newInstance();
        out.append("label() still works: ")
                .append(prunedType.getMethod("label").invoke(prunedInstance))
                .append('\n');

        // ---- 3. 改名 ----
        byte[] renamed = ClassRenamer.rename(original, internalName(ORIGINAL), internalName(RENAMED));
        Class<?> renamedType = new MemoryClassLoader(renamed, RENAMED).loadClass(RENAMED);

        out.append("renamed to: ").append(renamedType.getName()).append('\n');
        out.append("renamed add(4, 5) = ")
                .append(renamedType.getMethod("add", int.class, int.class)
                        .invoke(renamedType.getDeclaredConstructor().newInstance(), 4, 5));
        return out.toString();
    }

    private static String internalName(String binaryName) {
        return binaryName.replace('.', '/');
    }

    private static byte[] readClassFile(String binaryName) throws IOException {
        try (InputStream in = TransformDemo.class
                .getClassLoader()
                .getResourceAsStream(internalName(binaryName) + ".class")) {
            if (in == null) {
                throw new IllegalStateException("class file not found: " + binaryName);
            }
            return in.readAllBytes();
        }
    }

    /**
     * 子优先加载器：必须自己先定义目标类，否则父加载器会返回原始版本，
     * 我们的转换结果就永远用不上（详见 {@code TreeDemo} 里的同类说明）。
     */
    private static final class MemoryClassLoader extends ClassLoader {

        private final byte[] bytes;
        private final String target;

        private MemoryClassLoader(byte[] bytes, String target) {
            super(TransformDemo.class.getClassLoader());
            this.bytes = bytes;
            this.target = target;
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            if (name.equals(target)) {
                Class<?> loaded = findLoadedClass(name);
                return loaded != null ? loaded : findClass(name);
            }
            return super.loadClass(name, resolve);
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            if (name.equals(target)) {
                return defineClass(name, bytes, 0, bytes.length);
            }
            return super.findClass(name);
        }
    }
}
