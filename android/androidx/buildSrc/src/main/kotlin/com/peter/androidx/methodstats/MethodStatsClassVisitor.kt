package com.peter.androidx.methodstats

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import java.util.concurrent.ConcurrentHashMap

class MethodStatsClassVisitor(
    cv: ClassVisitor,
    private val className: String,
    private val includePatterns: List<String>,
    private val excludePatterns: List<String>,
    private val trackConstructors: Boolean,
    private val trackMethods: Boolean
) : ClassVisitor(Opcodes.ASM9, cv) {

    private var currentMethodName: String? = null
    private var currentMethodDesc: String? = null

    companion object {
        val methodCalls: ConcurrentHashMap<String, MethodCallInfo> = ConcurrentHashMap()
        
        fun resetStats() {
            methodCalls.clear()
        }
        
        fun getStats(): Map<String, MethodCallInfo> {
            return methodCalls.toMap()
        }
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor? {
        val isConstructor = name == "<init>"
        
        if (isConstructor && !trackConstructors) {
            return super.visitMethod(access, name, descriptor, signature, exceptions)
        }
        
        if (!isConstructor && !trackMethods) {
            return super.visitMethod(access, name, descriptor, signature, exceptions)
        }
        
        currentMethodName = name
        currentMethodDesc = descriptor
        
        val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        
        return if (mv != null) {
            MethodStatsMethodVisitor(mv, className, name ?: "", descriptor ?: "", isConstructor)
        } else {
            null
        }
    }

    override fun visitEnd() {
        super.visitEnd()
    }
}

data class MethodCallInfo(
    val className: String,
    val methodName: String,
    val methodDesc: String,
    val callCount: Int = 0,
    val calls: MutableList<MethodCall> = mutableListOf()
)

data class MethodCall(
    val callerClass: String,
    val callerMethod: String,
    val callerDesc: String,
    val calleeClass: String,
    val calleeMethod: String,
    val calleeDesc: String
)
