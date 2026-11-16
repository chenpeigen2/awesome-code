package org.peter.asm.transform;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.util.Collection;
import java.util.List;

/**
 * 按名字删除方法。
 * <p>
 * 只给名字是不够的——{@code overload} 会误删。所以对外提供两个入口：
 * <ul>
 *   <li>{@link #remove(byte[], String)} —— 删掉该名字的所有重载；</li>
 *   <li>{@link #remove(byte[], String, String)} —— 按名字 + 描述符精确删除。</li>
 * </ul>
 *
 * 注意：删掉方法后，如果别处还调用它，加载时会得到 {@code NoSuchMethodError}。
 * 这正是「字节码裁剪」的语义——它不检查调用方。
 */
public final class MethodRemover implements Opcodes {

    private MethodRemover() {
    }

    /** 删除所有名为 {@code methodName} 的方法。 */
    public static byte[] remove(byte[] classBytes, String methodName) {
        return remove(classBytes, List.of(methodName));
    }

    /** 批量删除：删掉名字在 {@code methodNames} 里的所有方法。 */
    public static byte[] remove(byte[] classBytes, Collection<String> methodNames) {
        return rewrite(classBytes, (access, name, descriptor) -> !methodNames.contains(name));
    }

    /** 按名字 + 描述符精确删除单个方法，不影响同名重载。 */
    public static byte[] remove(byte[] classBytes, String methodName, String descriptor) {
        return rewrite(classBytes, (access, name, desc) ->
                !(name.equals(methodName) && desc.equals(descriptor)));
    }

    /** 用「是否保留」谓词重写类。 */
    private static byte[] rewrite(byte[] classBytes, MethodPredicate keep) {
        ClassReader reader = new ClassReader(classBytes);
        ClassWriter writer = new ClassWriter(reader, 0);
        reader.accept(new ClassVisitor(writer, keep), 0);
        return writer.toByteArray();
    }

    private interface MethodPredicate {
        boolean test(int access, String name, String descriptor);
    }

    private static final class ClassVisitor extends org.objectweb.asm.ClassVisitor {

        private final MethodPredicate keep;

        private ClassVisitor(ClassWriter writer, MethodPredicate keep) {
            super(ASM9, writer);
            this.keep = keep;
        }

        @Override
        public org.objectweb.asm.MethodVisitor visitMethod(
                int access, String name, String descriptor, String signature, String[] exceptions) {
            if (!keep.test(access, name, descriptor)) {
                return null; // 返回 null 表示丢弃这个方法
            }
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }
    }
}
