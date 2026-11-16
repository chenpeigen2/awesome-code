package org.peter.asm.transform.fixture;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** 测试工具：读字节码、加载增强后的类、收集计时回调。 */
public final class Classes {

    private Classes() {
    }

    public static byte[] bytesOf(Class<?> type) {
        String resource = type.getName().replace('.', '/') + ".class";
        try (InputStream in = type.getClassLoader().getResourceAsStream(resource)) {
            if (in == null) {
                throw new IllegalStateException("class resource not found: " + resource);
            }
            return in.readAllBytes();
        } catch (IOException e) {
            throw new IllegalStateException("cannot read " + resource, e);
        }
    }

    public static Class<?> load(byte[] bytes, String binaryName) {
        return new MemoryClassLoader(bytes, binaryName).load();
    }

    public static Object newInstance(byte[] bytes, String binaryName) throws Exception {
        return load(bytes, binaryName).getDeclaredConstructor().newInstance();
    }

    /** 按名字 + 参数个数反射调用；被调用方法抛出的异常原样透传（不包 InvocationTargetException）。 */
    public static Object call(Object target, String name, Object... args) throws Exception {
        for (Method method : target.getClass().getDeclaredMethods()) {
            if (method.getName().equals(name) && method.getParameterCount() == args.length) {
                try {
                    return method.invoke(target, args);
                } catch (java.lang.reflect.InvocationTargetException e) {
                    Throwable cause = e.getCause();
                    if (cause instanceof Exception checked) {
                        throw checked;
                    }
                    if (cause instanceof Error error) {
                        throw error;
                    }
                    throw e;
                }
            }
        }
        throw new NoSuchMethodException(name + "/" + args.length);
    }

    private static final class MemoryClassLoader extends ClassLoader {
        private final byte[] bytes;
        private final String binaryName;

        private MemoryClassLoader(byte[] bytes, String binaryName) {
            super(Classes.class.getClassLoader());
            this.bytes = bytes;
            this.binaryName = binaryName;
        }

        private Class<?> load() {
            return defineClass(binaryName, bytes, 0, bytes.length);
        }
    }

    /** 记录每次调用：方法名 → 耗时（纳秒）。 */
    public static final class Timings {
        private final List<Entry> entries = new CopyOnWriteArrayList<>();

        public void record(String method, long nanos) {
            entries.add(new Entry(method, nanos));
        }

        public List<Entry> entries() {
            return List.copyOf(entries);
        }

        public List<String> methodNames() {
            return entries.stream().map(Entry::method).toList();
        }

        public void clear() {
            entries.clear();
        }

        public record Entry(String method, long nanos) {
        }
    }
}
