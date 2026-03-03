package com.peter.androidx.methodstats

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class MethodStatsMethodVisitor(
    mv: MethodVisitor,
    private val callerClass: String,
    private val callerMethod: String,
    private val callerDesc: String,
    private val isConstructor: Boolean
) : MethodVisitor(Opcodes.ASM9, mv) {

    override fun visitMethodInsn(
        opcode: Int,
        owner: String?,
        name: String?,
        descriptor: String?,
        isInterface: Boolean
    ) {
        if (owner != null && name != null && descriptor != null) {
            val key = "$owner.$name$descriptor"
            
            val existingInfo = MethodStatsClassVisitor.methodCalls[key]
            if (existingInfo != null) {
                val updatedInfo = existingInfo.copy(
                    callCount = existingInfo.callCount + 1,
                    calls = existingInfo.calls.apply {
                        add(
                            MethodCall(
                                callerClass = callerClass,
                                callerMethod = callerMethod,
                                callerDesc = callerDesc,
                                calleeClass = owner,
                                calleeMethod = name,
                                calleeDesc = descriptor
                            )
                        )
                    }
                )
                MethodStatsClassVisitor.methodCalls[key] = updatedInfo
            } else {
                MethodStatsClassVisitor.methodCalls[key] = MethodCallInfo(
                    className = owner,
                    methodName = name,
                    methodDesc = descriptor,
                    callCount = 1,
                    calls = mutableListOf(
                        MethodCall(
                            callerClass = callerClass,
                            callerMethod = callerMethod,
                            callerDesc = callerDesc,
                            calleeClass = owner,
                            calleeMethod = name,
                            calleeDesc = descriptor
                        )
                    )
                )
            }
        }
        
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
    }

    override fun visitFieldInsn(
        opcode: Int,
        owner: String?,
        name: String?,
        descriptor: String?
    ) {
        super.visitFieldInsn(opcode, owner, name, descriptor)
    }

    override fun visitTypeInsn(opcode: Int, type: String?) {
        super.visitTypeInsn(opcode, type)
    }

    override fun visitInvokeDynamicInsn(
        name: String?,
        descriptor: String?,
        bootstrapMethodHandle: org.objectweb.asm.Handle?,
        vararg bootstrapMethodArguments: Any?
    ) {
        super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, *bootstrapMethodArguments)
    }
}
