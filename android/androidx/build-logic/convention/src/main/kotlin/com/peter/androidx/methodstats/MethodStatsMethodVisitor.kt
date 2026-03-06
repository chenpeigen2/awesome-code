package com.peter.androidx.methodstats

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class MethodStatsMethodVisitor(
    mv: MethodVisitor,
    private val className: String,
    private val methodName: String,
    private val methodDesc: String
) : MethodVisitor(Opcodes.ASM9, mv) {

    override fun visitCode() {
        super.visitCode()

        mv.visitLdcInsn("MethodStats")
        mv.visitLdcInsn(">>> $className.$methodName$methodDesc")
        
        mv.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            "android/util/Log",
            "d",
            "(Ljava/lang/String;Ljava/lang/String;)I",
            false
        )
        
        mv.visitInsn(Opcodes.POP)
    }

    override fun visitMaxs(maxStack: Int, maxLocals: Int) {
        mv.visitMaxs(maxStack + 4, maxLocals)
    }
}
