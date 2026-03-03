package com.peter.androidx.methodstats

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class MethodStatsClassVisitor(cv: ClassVisitor) : ClassVisitor(Opcodes.ASM9, cv) {

    private var currentClassName: String = ""

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        currentClassName = name ?: ""
        super.visit(version, access, name, signature, superName, interfaces)
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor? {
        val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        
        if (mv != null && name != null && name != "<clinit>") {
            return MethodStatsMethodVisitor(mv, currentClassName, name, descriptor ?: "")
        }
        return mv
    }
}
