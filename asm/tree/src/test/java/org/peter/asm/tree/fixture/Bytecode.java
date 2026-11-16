package org.peter.asm.tree.fixture;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

/** 测试工具：读 class 字节、把改造后的字节加载成可调用的类。 */
public final class Bytecode {

    private Bytecode() {
    }

    public static byte[] of(Class<?> type) {
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

    /**
     * 把 {@code bytes} 定义成 {@code binaryName} 并实例化。
     * <p>
     * 用独立 ClassLoader，避免与测试类路径上同名类冲突。
     */
    public static Object newInstance(byte[] bytes, String binaryName) throws Exception {
        return load(bytes, binaryName).getDeclaredConstructor().newInstance();
    }

    public static Class<?> load(byte[] bytes, String binaryName) {
        return new MemoryClassLoader(bytes, binaryName).load();
    }

    private static final class MemoryClassLoader extends ClassLoader {
        private final byte[] bytes;
        private final String binaryName;

        private MemoryClassLoader(byte[] bytes, String binaryName) {
            super(Bytecode.class.getClassLoader());
            this.bytes = bytes;
            this.binaryName = binaryName;
        }

        private Class<?> load() {
            return defineClass(binaryName, bytes, 0, bytes.length);
        }
    }

    /** 反射调用一个方法并返回结果。 */
    public static Object invoke(Object target, String methodName, Object... args) throws Exception {
        for (Method method : target.getClass().getDeclaredMethods()) {
            if (method.getName().equals(methodName) && method.getParameterCount() == args.length) {
                return method.invoke(target, args);
            }
        }
        throw new NoSuchMethodException(methodName);
    }

    /** 读取对象字段值（含 private）。 */
    public static Object field(Object target, String fieldName) throws Exception {
        var field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(target);
    }
}
