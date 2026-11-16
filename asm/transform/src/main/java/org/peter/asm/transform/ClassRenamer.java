package org.peter.asm.transform;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;

import java.util.Map;

/**
 * 重命名类，并把类内所有对自身的引用一起改掉。
 * <p>
 * 「一起改掉」是关键：方法体里的 {@code INVOKESTATIC Owner.method}、
 * 字段类型、方法描述符、{@code this} 的类型都会指向旧名字。
 * {@link ClassRemapper} 遍历整个类树，把每个出现内部名的地方都过一遍
 * {@link Remapper#map}，所以不需要手写指令遍历。
 *
 * <p>示例：
 * <pre>{@code
 * byte[] renamed = ClassRenamer.rename(bytes, "a/b/Old", "a/b/New");
 * }</pre>
 */
public final class ClassRenamer {

    private ClassRenamer() {
    }

    /**
     * 重命名单个类及其自引用。
     *
     * @param classBytes class 文件字节
     * @param oldName    旧 JVM 内部名，例如 {@code com/example/Old}
     * @param newName    新 JVM 内部名，例如 {@code com/example/New}
     */
    public static byte[] rename(byte[] classBytes, String oldName, String newName) {
        return rename(classBytes, Map.of(oldName, newName));
    }

    /** 批量重命名：内部名映射表。 */
    public static byte[] rename(byte[] classBytes, Map<String, String> internalNameMapping) {
        ClassReader reader = new ClassReader(classBytes);
        // 不改方法体结构，只需平移常量池引用 → 不需要重算 frames
        ClassWriter writer = new ClassWriter(reader, 0);
        reader.accept(new ClassRemapper(writer, new MappingRemapper(internalNameMapping)), 0);
        return writer.toByteArray();
    }

    private static final class MappingRemapper extends Remapper {

        private final Map<String, String> mapping;

        private MappingRemapper(Map<String, String> mapping) {
            this.mapping = mapping;
        }

        @Override
        public String map(String internalName) {
            return mapping.getOrDefault(internalName, internalName);
        }
    }
}
