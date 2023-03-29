package org.peter.basic.utils;

import lombok.NonNull;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

public class ASMUtils {
    public static ClassNode getClassNode(@NonNull ClassReader reader) {
        ClassNode node = new ClassNode(Opcodes.ASM9);
        reader.accept(node, 0); // parameters not check
        return node;
    }
}