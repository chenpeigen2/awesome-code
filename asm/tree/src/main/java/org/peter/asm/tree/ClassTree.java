package org.peter.asm.tree;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Tree API 的入口：字节码 ⇄ 可编辑的树。
 * <p>
 * 与 Core API 的区别：Core 是流式访问者，读到就丢；Tree 先把整个类读进
 * {@link ClassNode}，之后可以任意增删改，最后再写回字节码。
 */
public final class ClassTree {

    /** Tree API 使用的 ASM API 版本。 */
    public static final int API = Opcodes.ASM9;

    private ClassTree() {
    }

    /**
     * 读字节码 → 可编辑的树。
     * <p>
     * 使用 {@code 0} 作为解析选项，保留 frames，这样写回时无需重算（见 {@link #write}）。
     */
    public static ClassNode read(byte[] classBytes) {
        ClassNode node = new ClassNode(API);
        new ClassReader(classBytes).accept(node, 0);
        return node;
    }

    /**
     * 树 → 字节码。
     * <p>
     * {@link ClassWriter#COMPUTE_MAXS} 只重算栈大小，不重算 frames ——
     * 因为 {@link #read} 保留了原始 frames，而新增方法由
     * {@link org.objectweb.asm.commons.Method#getMaxStack} 之类无法推导 frame，
     * 所以新增方法时我们显式用 {@code COMPUTE_FRAMES} 的变体（见 {@link #writeComputingFrames}）。
     */
    public static byte[] write(ClassNode node) {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        node.accept(cw);
        return cw.toByteArray();
    }

    /**
     * 树 → 字节码，重算 frames。
     * <p>
     * 增删了跳转/分支的方法必须用这个版本，否则 JVM 会以
     * {@code VerifyError} 拒绝加载。
     */
    public static byte[] writeComputingFrames(ClassNode node) {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        node.accept(cw);
        return cw.toByteArray();
    }

    /** 按名字查找字段，找不到返回 {@code null}。 */
    public static FieldNode field(ClassNode node, String name) {
        return node.fields.stream()
                .filter(f -> f.name.equals(name))
                .findFirst()
                .orElse(null);
    }

    /** 按名字 + 描述符查找方法，找不到返回 {@code null}。 */
    public static MethodNode method(ClassNode node, String name, String descriptor) {
        return node.methods.stream()
                .filter(m -> m.name.equals(name) && m.desc.equals(descriptor))
                .findFirst()
                .orElse(null);
    }

    /** 追加一个字段（不检查重名，调用方负责）。 */
    public static FieldNode addField(ClassNode node, int access, String name, String descriptor) {
        FieldNode field = new FieldNode(access, name, descriptor, null, null);
        node.fields.add(field);
        return field;
    }
}
