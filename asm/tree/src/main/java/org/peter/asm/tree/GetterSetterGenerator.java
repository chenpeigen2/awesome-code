package org.peter.asm.tree;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.Locale;

/**
 * 给 POJO 自动补上 getter / setter。
 * <p>
 * 这是 Tree API 的经典场景：把类读成树，按字段批量加方法，再写回字节码。
 * 对 {@code Person} 这样的类，效果等价于手写了：
 *
 * <pre>{@code
 * public String getName() { return name; }
 * public void setName(String name) { this.name = name; }
 * }</pre>
 *
 * 实现要点：
 * <ul>
 *   <li>按字段描述符选正确的 {@code xLOAD} / {@code xRETURN} 指令，long/double 占 2 个局部变量槽；</li>
 *   <li>已存在的同签名方法不重复添加（幂等）；</li>
 *   <li>跳过 static 字段——它们的访问语义不同，不属于 getter/setter 范畴。</li>
 * </ul>
 */
public final class GetterSetterGenerator implements Opcodes {

    private GetterSetterGenerator() {
    }

    /** 读入字节码，补齐 getter/setter，写回字节码。 */
    public static byte[] enhance(byte[] classBytes) {
        ClassNode node = ClassTree.read(classBytes);
        addAccessors(node);
        return ClassTree.writeComputingFrames(node);
    }

    /** 在已读入的树上就地添加 getter/setter。 */
    public static void addAccessors(ClassNode node) {
        // 先快照字段列表：addMethod 不会改 fields，但保持「读的时候不改集合」的习惯
        for (FieldNode field : java.util.List.copyOf(node.fields)) {
            if ((field.access & ACC_STATIC) != 0) {
                continue;
            }
            String suffix = capitalize(field.name);
            addGetterIfAbsent(node, field, suffix);
            addSetterIfAbsent(node, field, suffix);
        }
    }

    private static void addGetterIfAbsent(ClassNode node, FieldNode field, String suffix) {
        String name = "get" + suffix;
        String descriptor = "()" + field.desc;
        if (ClassTree.method(node, name, descriptor) != null) {
            return;
        }

        Type type = Type.getType(field.desc);
        var method = new org.objectweb.asm.tree.MethodNode(
                ACC_PUBLIC, name, descriptor, null, null);
        InsnList code = method.instructions;

        // 指令序列：aload_0; getfield owner.field:desc; xRETURN
        code.add(new VarInsnNode(ALOAD, 0));
        code.add(new org.objectweb.asm.tree.FieldInsnNode(GETFIELD, node.name, field.name, field.desc));
        code.add(new InsnNode(type.getOpcode(IRETURN)));

        method.maxStack = 1;
        method.maxLocals = 1;
        node.methods.add(method);
    }

    private static void addSetterIfAbsent(ClassNode node, FieldNode field, String suffix) {
        String name = "set" + suffix;
        String descriptor = "(" + field.desc + ")V";
        if (ClassTree.method(node, name, descriptor) != null) {
            return;
        }

        Type type = Type.getType(field.desc);
        var method = new org.objectweb.asm.tree.MethodNode(
                ACC_PUBLIC, name, descriptor, null, null);
        InsnList code = method.instructions;

        // 指令序列：aload_0; xload 1; putfield owner.field:desc; return
        code.add(new VarInsnNode(ALOAD, 0));
        code.add(new VarInsnNode(type.getOpcode(ILOAD), 1));
        code.add(new org.objectweb.asm.tree.FieldInsnNode(PUTFIELD, node.name, field.name, field.desc));
        code.add(new InsnNode(RETURN));

        // long/double 参数占 2 个槽，所以 maxLocals 是 1 + size
        method.maxStack = 1 + type.getSize();
        method.maxLocals = 1 + type.getSize();
        node.methods.add(method);
    }

    /** {@code name} → {@code Name}，遵循 JavaBean 命名习惯。 */
    private static String capitalize(String name) {
        if (name.isEmpty()) {
            return name;
        }
        return name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1);
    }
}
