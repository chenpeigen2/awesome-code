package com.peter.androidx.methodstats

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import org.objectweb.asm.ClassVisitor

abstract class MethodStatsClassVisitorFactory : AsmClassVisitorFactory<MethodStatsParameters> {

    override fun createClassVisitor(classContext: ClassContext, nextClassVisitor: ClassVisitor): ClassVisitor {
        return MethodStatsClassVisitor(nextClassVisitor)
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        val className = classData.className
        return !className.contains("R$") && !className.contains("BuildConfig")
    }
}
