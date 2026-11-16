package org.peter.asm.basics.fixture;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 测试工具：把被测类自己的 .class 读成字节数组。
 * <p>
 * 被测类既是「输入」，也是「独立真值」——它的源码就是字节码结构的规格。
 */
public final class ClassBytes {

    private ClassBytes() {
    }

    public static byte[] of(Class<?> type) {
        String resource = type.getName().replace('.', '/') + ".class";
        ClassLoader loader = type.getClassLoader();
        try (InputStream in = (loader == null ? ClassLoader.getSystemClassLoader() : loader)
                .getResourceAsStream(resource)) {
            if (in == null) {
                throw new IllegalStateException("class resource not found: " + resource);
            }
            return in.readAllBytes();
        } catch (IOException e) {
            throw new IllegalStateException("cannot read class resource: " + resource, e);
        }
    }

    public static byte[] read(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new IllegalStateException("cannot read " + path, e);
        }
    }
}
