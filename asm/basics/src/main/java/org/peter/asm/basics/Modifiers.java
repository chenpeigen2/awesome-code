package org.peter.asm.basics;

import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.List;

/**
 * 把 access flags 渲染成源码里熟悉的关键字。
 * <p>
 * 同一个 bit 在不同成员上含义不同（{@code ACC_SYNCHRONIZED} 在类上是 {@code ACC_SUPER}，
 * 在字段上是 {@code ACC_VOLATILE}），所以需要按成员种类解释。
 */
final class Modifiers {

    /** 一个 access flag 位在不同成员上的关键字。 */
    private record Flag(int bit, String onClass, String onField, String onMethod) {
    }

    /**
     * 按「类 / 字段 / 方法」三套语义描述过的 flag 表。
     * 顺序即输出顺序，保证 describe() 稳定可断言。
     */
    private static final List<Flag> FLAGS = List.of(
            new Flag(Opcodes.ACC_PUBLIC, "public", "public", "public"),
            new Flag(Opcodes.ACC_PROTECTED, "protected", "protected", "protected"),
            new Flag(Opcodes.ACC_PRIVATE, "private", "private", "private"),
            new Flag(Opcodes.ACC_ABSTRACT, "abstract", null, "abstract"),
            new Flag(Opcodes.ACC_STATIC, "static", "static", "static"),
            new Flag(Opcodes.ACC_FINAL, "final", "final", "final"),
            new Flag(Opcodes.ACC_TRANSIENT, null, "transient", null),
            new Flag(Opcodes.ACC_VOLATILE, null, "volatile", null),
            new Flag(Opcodes.ACC_BRIDGE, null, null, "bridge"),
            new Flag(Opcodes.ACC_SYNCHRONIZED, "super", null, "synchronized"),
            new Flag(Opcodes.ACC_NATIVE, null, null, "native"),
            new Flag(Opcodes.ACC_STRICT, null, null, "strictfp"),
            new Flag(Opcodes.ACC_INTERFACE, "interface", null, null),
            new Flag(Opcodes.ACC_ANNOTATION, "annotation", null, null),
            new Flag(Opcodes.ACC_ENUM, "enum", "enum", "enum"),
            new Flag(Opcodes.ACC_SYNTHETIC, "synthetic", "synthetic", "synthetic"),
            new Flag(Opcodes.ACC_RECORD, "record", null, null));

    private Modifiers() {
    }

    static String describe(int access, MemberKind kind) {
        List<String> keywords = new ArrayList<>();
        for (Flag flag : FLAGS) {
            if ((access & flag.bit()) == 0) {
                continue;
            }
            String keyword = switch (kind) {
                case CLASS -> flag.onClass();
                case FIELD -> flag.onField();
                case METHOD -> flag.onMethod();
            };
            if (keyword != null) {
                keywords.add(keyword);
            }
        }
        // ACC_SUPER 对类来说是编译产物细节，源码里不写
        keywords.remove("super");
        return String.join(" ", keywords);
    }
}
